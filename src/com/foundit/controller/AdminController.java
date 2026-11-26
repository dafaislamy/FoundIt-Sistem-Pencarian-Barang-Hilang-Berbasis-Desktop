package com.foundit.controller;

import com.foundit.model.*;
import com.foundit.service.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * AdminController — penghubung antara panel-panel Admin di package view
 * dengan layer Service & DAO (MySQL).
 */
public class AdminController {

    private final ItemService itemService = new ItemService();
    private final ClaimService claimService = new ClaimService();
    private final CategoryService categoryService = new CategoryService();
    private final UserService userService = new UserService();

    /**
     * Mengambil 4 metrik statistik Dashboard Admin identik dengan admin/admin-dashboard.php:
     * [0] totalUser (role 'user')
     * [1] totalLaporan (semua items)
     * [2] perluVerifikasi (items Pending)
     * [3] klaimSelesai (claims Disetujui)
     */
    public int[] getDashboardStats() throws ServiceException {
        int totalUser = 0;
        for (User u : userService.findAll()) {
            if ("user".equalsIgnoreCase(u.getRole())) {
                totalUser++;
            }
        }

        List<Item> allItems = itemService.findAll();
        int totalLaporan = allItems.size();
        int perluVerifikasi = 0;
        for (Item it : allItems) {
            if (it.getStatus() == ItemStatus.PENDING) {
                perluVerifikasi++;
            }
        }

        int klaimSelesai = 0;
        for (Claim cl : claimService.findAll()) {
            if (cl.getStatus() == ClaimStatus.DISETUJUI) {
                klaimSelesai++;
            }
        }

        return new int[]{totalUser, totalLaporan, perluVerifikasi, klaimSelesai};
    }

    /** Mengambil maksimal N laporan berstatus Pending terbaru. */
    public List<Item> getPendingItems(int limit) throws ServiceException {
        List<Item> pending = itemService.search(null, null, ItemStatus.PENDING, null);
        pending.sort(Comparator.comparing(
                Item::getCreatedAt,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));
        if (limit > 0 && pending.size() > limit) {
            return new ArrayList<>(pending.subList(0, limit));
        }
        return pending;
    }

    /** Verifikasi Laporan: Terima (Terbit) atau Tolak (Ditolak). */
    public void verifyItem(String itemId, boolean approve) throws ServiceException {
        itemService.changeStatus(itemId, approve ? ItemStatus.TERBIT : ItemStatus.DITOLAK);
    }

    /** Mengubah status barang sesuai alur kerja ItemService. */
    public void changeItemStatus(String itemId, ItemStatus targetStatus) throws ServiceException {
        itemService.changeStatus(itemId, targetStatus);
    }

    /** Pencarian seluruh laporan barang (untuk Kelola Laporan & Ubah Status). */
    public List<Item> searchItems(String keyword, String categoryId, ItemStatus status, ItemType type)
            throws ServiceException {
        String cleanKw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return itemService.search(cleanKw, categoryId, status, type);
    }

    /** Menghapus laporan barang. */
    public void deleteItem(String itemId) throws ServiceException {
        itemService.delete(itemId);
    }

    /** Pencarian klaim dengan filter status opsional. */
    public List<Claim> searchClaims(String keyword, ClaimStatus status) throws ServiceException {
        String cleanKw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return claimService.search(cleanKw, status);
    }

    /** Menyetujui klaim (otomatis mengubah item -> Selesai dan klaim Menunggu lain -> Ditolak). */
    public void approveClaim(String claimId, String adminId) throws ServiceException {
        claimService.approve(claimId, adminId);
    }

    /** Menolak klaim. */
    public void rejectClaim(String claimId, String adminId) throws ServiceException {
        claimService.reject(claimId, adminId);
    }

    /** Mengambil atau mencari kategori. */
    public List<Category> searchCategories(String keyword) throws ServiceException {
        if (keyword == null || keyword.isBlank()) {
            return categoryService.findAll();
        }
        return categoryService.search(keyword.trim());
    }

    public void saveCategory(Category c) throws ServiceException {
        categoryService.save(c);
    }

    public void deleteCategory(String categoryId) throws ServiceException {
        categoryService.delete(categoryId);
    }

    /** Mengambil atau mencari user. */
    public List<User> searchUsers(String keyword) throws ServiceException {
        if (keyword == null || keyword.isBlank()) {
            return userService.findAll();
        }
        return userService.search(keyword.trim());
    }

    public void saveUser(User u, String plainPassword) throws ServiceException {
        userService.save(u, plainPassword);
    }

    public void deleteUser(String userId) throws ServiceException {
        userService.delete(userId);
    }
}
