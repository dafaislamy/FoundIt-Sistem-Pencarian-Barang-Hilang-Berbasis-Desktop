package com.foundit.service;

import com.foundit.dao.DAOException;
import com.foundit.dao.ItemDAO;
import com.foundit.model.Item;
import com.foundit.model.ItemStatus;
import com.foundit.model.ItemType;

import java.time.LocalDate;
import java.util.List;

/**
 * Aturan bisnis barang:
 * - tipe Ditemukan: nama penemu wajib; tipe Hilang: pelapor wajib;
 * - alur status: Pending -> Terbit / Ditolak; Terbit -> Selesai; Selesai/Ditolak/Terbit -> Arsip.
 */
public class ItemService extends AbstractService<Item> {

    private static final String DEFAULT_PHOTO = "default.jpg";

    private final ItemDAO itemDAO;

    public ItemService() {
        this(new ItemDAO());
    }

    private ItemService(ItemDAO itemDAO) {
        super(itemDAO);
        this.itemDAO = itemDAO;
    }

    @Override
    protected void validate(Item it) throws ValidationException {
        it.setTitle(Validator.required("Judul", it.getTitle(), 150));
        it.setDescription(Validator.required("Deskripsi", it.getDescription(), 1000));
        it.setLocation(Validator.required("Lokasi", it.getLocation(), 100));
        String photo = Validator.optional("Foto", it.getPhoto(), 255);
        it.setPhoto(photo == null ? DEFAULT_PHOTO : photo);
        if (it.getType() == null) {
            throw new ValidationException("Tipe wajib dipilih.");
        }
        if (it.getEventDate() == null) {
            throw new ValidationException("Tanggal kejadian wajib diisi.");
        }
        if (it.getEventDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Tanggal kejadian tidak boleh di masa depan.");
        }
        if (it.getStatus() == null) {
            throw new ValidationException("Status wajib dipilih.");
        }
        Validator.requireId("Kategori", it.getCategoryId());

        if (it.getType() == ItemType.FOUND) {
            it.setFinderName(Validator.required("Nama penemu", it.getFinderName(), 100));
            it.setFinderPhone(Validator.optionalPhone("Telepon penemu", it.getFinderPhone()));
        } else {
            Validator.requireId("Pelapor", it.getReporterId());
            it.setFinderName(null);
            it.setFinderPhone(null);
        }
    }

    public List<Item> search(String keyword, String categoryId, ItemStatus status, ItemType type)
            throws ServiceException {
        try {
            return itemDAO.search(keyword, categoryId, status, type);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /** Ubah status lewat tombol alur kerja (verifikasi admin, selesai, arsip). */
    public void changeStatus(String itemId, ItemStatus target) throws ServiceException {
        try {
            Item it = itemDAO.findById(itemId);
            if (it == null) {
                throw new ServiceException("Barang tidak ditemukan (mungkin sudah dihapus).");
            }
            ItemStatus current = it.getStatus();
            switch (target) {
                case TERBIT, DITOLAK -> {
                    if (current != ItemStatus.PENDING) {
                        throw new ValidationException("Hanya laporan berstatus Pending yang dapat diterbitkan "
                                + "atau ditolak (status saat ini: " + current + ").");
                    }
                }
                case SELESAI -> {
                    if (current != ItemStatus.TERBIT) {
                        throw new ValidationException("Hanya barang berstatus Terbit yang dapat ditandai Selesai "
                                + "(status saat ini: " + current + ").");
                    }
                }
                case ARSIP -> {
                    if (current == ItemStatus.ARSIP || current == ItemStatus.PENDING) {
                        throw new ValidationException("Barang berstatus " + current + " tidak dapat diarsipkan.");
                    }
                }
                default -> throw new ValidationException("Perubahan status tidak didukung.");
            }
            itemDAO.updateStatus(itemId, target);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
