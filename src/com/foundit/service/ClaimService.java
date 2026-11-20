package com.foundit.service;

import com.foundit.dao.ClaimDAO;
import com.foundit.dao.DAOException;
import com.foundit.dao.ItemDAO;
import com.foundit.model.Claim;
import com.foundit.model.ClaimStatus;
import com.foundit.model.Item;
import com.foundit.model.ItemStatus;
import com.foundit.model.ItemType;

import java.util.List;

/**
 * Aturan bisnis klaim:
 * - klaim hanya untuk barang tipe Ditemukan berstatus Terbit, dan bukan oleh pelapornya sendiri;
 * - satu pengguna tidak boleh punya dua klaim aktif pada barang yang sama;
 * - klaim hanya boleh diubah selama Menunggu;
 * - Disetujui -> barang Selesai + klaim Menunggu lain otomatis Ditolak (satu transaksi);
 * - pengklaim dapat membatalkan klaim yang masih Menunggu (Dibatalkan).
 */
public class ClaimService extends AbstractService<Claim> {

    private final ClaimDAO claimDAO;
    private final ItemDAO itemDAO = new ItemDAO();

    public ClaimService() {
        this(new ClaimDAO());
    }

    private ClaimService(ClaimDAO claimDAO) {
        super(claimDAO);
        this.claimDAO = claimDAO;
    }

    @Override
    protected void validate(Claim c) throws ValidationException {
        Validator.requireId("Barang", c.getItemId());
        Validator.requireId("Pengklaim", c.getUserId());
        c.setProofDescription(Validator.required("Bukti kepemilikan", c.getProofDescription(), 1000));
        c.setAttachment(Validator.optional("Lampiran foto", c.getAttachment(), 255));
    }

    public List<Claim> search(String keyword, ClaimStatus status) throws ServiceException {
        try {
            return claimDAO.search(keyword, status);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void save(Claim c) throws ServiceException {
        validate(c);
        try {
            Item item = itemDAO.findById(c.getItemId());
            if (item == null) {
                throw new ValidationException("Barang yang dipilih tidak ditemukan.");
            }
            if (item.getType() != ItemType.FOUND) {
                throw new ValidationException("Klaim hanya dapat diajukan untuk barang bertipe Ditemukan.");
            }
            if (item.getStatus() != ItemStatus.TERBIT) {
                throw new ValidationException("Klaim hanya dapat diajukan untuk barang berstatus Terbit "
                        + "(status saat ini: " + item.getStatus() + ").");
            }
            if (c.getUserId().equals(item.getReporterId())) {
                throw new ValidationException("Pelapor barang tidak dapat mengklaim barangnya sendiri.");
            }
            if (claimDAO.existsApproved(item.getId())) {
                throw new ValidationException("Barang ini sudah memiliki klaim yang disetujui.");
            }
            if (claimDAO.existsActive(c.getItemId(), c.getUserId(), c.getId())) {
                throw new ValidationException("Pengguna ini sudah memiliki klaim aktif untuk barang tersebut.");
            }
            if (c.getId() == null) {
                claimDAO.insert(c);
            } else {
                Claim existing = claimDAO.findById(c.getId());
                if (existing == null) {
                    throw new ServiceException("Klaim tidak ditemukan (mungkin sudah dihapus).");
                }
                if (existing.getStatus() != ClaimStatus.MENUNGGU) {
                    throw new ValidationException("Klaim yang sudah diproses tidak dapat diubah.");
                }
                claimDAO.update(c);
            }
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public void approve(String claimId, String adminId) throws ServiceException {
        process(claimId, ClaimStatus.DISETUJUI, adminId);
    }

    public void reject(String claimId, String adminId) throws ServiceException {
        process(claimId, ClaimStatus.DITOLAK, adminId);
    }

    public void cancel(String claimId) throws ServiceException {
        process(claimId, ClaimStatus.DIBATALKAN, null);
    }

    private void process(String claimId, ClaimStatus target, String adminId) throws ServiceException {
        if (target != ClaimStatus.DIBATALKAN) {
            Validator.requireId("Admin pemroses", adminId);
        }
        try {
            Claim claim = claimDAO.findById(claimId);
            if (claim == null) {
                throw new ServiceException("Klaim tidak ditemukan (mungkin sudah dihapus).");
            }
            if (claim.getStatus() != ClaimStatus.MENUNGGU) {
                throw new ValidationException("Klaim ini sudah diproses (" + claim.getStatus() + ").");
            }
            if (target == ClaimStatus.DISETUJUI) {
                Item item = itemDAO.findById(claim.getItemId());
                if (item == null || item.getStatus() != ItemStatus.TERBIT) {
                    throw new ValidationException("Klaim hanya dapat disetujui untuk barang berstatus Terbit.");
                }
                if (claimDAO.existsApproved(item.getId())) {
                    throw new ValidationException("Barang ini sudah memiliki klaim yang disetujui.");
                }
            }
            claimDAO.process(claimId, target, adminId);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
