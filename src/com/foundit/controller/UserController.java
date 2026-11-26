package com.foundit.controller;

import com.foundit.model.*;
import com.foundit.service.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * UserController — penghubung antara panel-panel User di package view
 * dengan layer Service & DAO (MySQL).
 */
public class UserController {

    private final ItemService itemService = new ItemService();
    private final ClaimService claimService = new ClaimService();
    private final CategoryService categoryService = new CategoryService();
    private final UserService userService = new UserService();

    /**
     * Mengambil 4 metrik statistik Dashboard User identik dengan user/dashboard.php:
     * [0] totalBarangHilang (Terbit)
     * [1] totalBarangDitemukan (Terbit)
     * [2] totalKlaimSaya
     * [3] totalDikembalikan (Klaim Disetujui)
     */
    public int[] getDashboardStats(String userId) throws ServiceException {
        List<Item> allItems = itemService.findAll();
        int totalHilang = 0;
        int totalDitemukan = 0;
        for (Item it : allItems) {
            if (it.getStatus() == ItemStatus.TERBIT) {
                if (it.getType() == ItemType.LOST) totalHilang++;
                else if (it.getType() == ItemType.FOUND) totalDitemukan++;
            }
        }

        List<Claim> allClaims = claimService.findAll();
        int totalKlaim = 0;
        int totalDikembalikan = 0;
        for (Claim cl : allClaims) {
            if (userId != null && userId.equals(cl.getUserId())) {
                totalKlaim++;
                if (cl.getStatus() == ClaimStatus.DISETUJUI) {
                    totalDikembalikan++;
                }
            }
        }
        return new int[]{totalHilang, totalDitemukan, totalKlaim, totalDikembalikan};
    }

    /** Mengambil maksimal N laporan terbaru yang berstatus Terbit. */
    public List<Item> getLatestPublishedItems(int limit) throws ServiceException {
        List<Item> published = itemService.search(null, null, ItemStatus.TERBIT, null);
        published.sort(Comparator.comparing(
                Item::getCreatedAt,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));
        if (published.size() > limit) {
            return new ArrayList<>(published.subList(0, limit));
        }
        return published;
    }

    /** Pencarian katalog barang yang berstatus Terbit (untuk halaman Cari Barang). */
    public List<Item> searchPublishedItems(String keyword, String categoryId, ItemType type) throws ServiceException {
        String cleanKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        String cleanCat = (categoryId == null || categoryId.isBlank()) ? null : categoryId;
        return itemService.search(cleanKeyword, cleanCat, ItemStatus.TERBIT, type);
    }

    /** Mengambil semua kategori barang dari MySQL. */
    public List<Category> getAllCategories() throws ServiceException {
        return categoryService.findAll();
    }

    /** Mengirim laporan barang hilang (status awal Pending). */
    public Item submitLostItem(
            String judul,
            String categoryId,
            String lokasi,
            LocalDate tanggalKejadian,
            String deskripsi,
            String photoFileName,
            String userId
    ) throws ServiceException {
        Item item = new Item();
        item.setTitle(judul);
        item.setCategoryId(categoryId);
        item.setLocation(lokasi);
        item.setEventDate(tanggalKejadian);
        item.setDescription(deskripsi);
        item.setPhoto(photoFileName);
        item.setType(ItemType.LOST);
        item.setStatus(ItemStatus.PENDING);
        item.setReporterId(userId);
        itemService.save(item);
        return item;
    }

    /** Mengirim laporan barang ditemukan (status awal Pending). */
    public Item submitFoundItem(
            String judul,
            String categoryId,
            String lokasi,
            LocalDate tanggalKejadian,
            String deskripsi,
            String finderName,
            String finderPhone,
            String photoFileName,
            String userId
    ) throws ServiceException {
        Item item = new Item();
        item.setTitle(judul);
        item.setCategoryId(categoryId);
        item.setLocation(lokasi);
        item.setEventDate(tanggalKejadian);
        item.setDescription(deskripsi);
        item.setFinderName(finderName);
        item.setFinderPhone(finderPhone);
        item.setPhoto(photoFileName);
        item.setType(ItemType.FOUND);
        item.setStatus(ItemStatus.PENDING);
        item.setReporterId(userId);
        itemService.save(item);
        return item;
    }

    /** Mengambil barang bertipe FOUND & berstatus TERBIT yang dapat diklaim. */
    public List<Item> getClaimableItems() throws ServiceException {
        return itemService.search(null, null, ItemStatus.TERBIT, ItemType.FOUND);
    }

    /** Mengajukan klaim barang. */
    public Claim submitClaim(String itemId, String userId, String proofDesc, String attachmentPhoto)
            throws ServiceException {
        Claim claim = new Claim();
        claim.setItemId(itemId);
        claim.setUserId(userId);
        claim.setProofDescription(proofDesc);
        claim.setAttachment(attachmentPhoto);
        claimService.save(claim);
        return claim;
    }

    /** Mengambil daftar klaim milik user yang sedang login. */
    public List<Claim> getUserClaims(String userId) throws ServiceException {
        List<Claim> all = claimService.findAll();
        List<Claim> mine = new ArrayList<>();
        for (Claim c : all) {
            if (userId != null && userId.equals(c.getUserId())) {
                mine.add(c);
            }
        }
        return mine;
    }

    /** Membatalkan klaim yang masih berstatus Menunggu. */
    public void cancelClaim(String claimId) throws ServiceException {
        claimService.cancel(claimId);
    }

    /** Mengambil riwayat laporan barang milik user yang sedang login. */
    public List<Item> getUserReports(String userId) throws ServiceException {
        List<Item> all = itemService.findAll();
        List<Item> mine = new ArrayList<>();
        for (Item it : all) {
            if (userId != null && userId.equals(it.getReporterId())) {
                mine.add(it);
            }
        }
        return mine;
    }

    /** Menghapus laporan milik user (hanya jika berstatus Pending). */
    public void deleteUserReport(String itemId) throws ServiceException {
        Item it = itemService.findById(itemId);
        if (it == null) {
            throw new ServiceException("Laporan tidak ditemukan.");
        }
        if (it.getStatus() != ItemStatus.PENDING) {
            throw new ValidationException("Hanya laporan berstatus Pending yang dapat dihapus.");
        }
        itemService.delete(itemId);
    }

    /** Memperbarui profil user yang sedang login. */
    public void updateUserProfile(User user, String newPassword) throws ServiceException {
        userService.save(user, newPassword);
    }
}
