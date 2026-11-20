package com.foundit.dao;

import com.foundit.config.DatabaseConnection;
import com.foundit.model.Claim;
import com.foundit.model.ClaimStatus;
import com.foundit.model.ItemStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClaimDAO extends AbstractDAO<Claim> {

    private static final String SELECT =
            "SELECT cl.id, cl.bukti_kepemilikan, cl.lampiran_foto, cl.status_klaim, cl.created_at, "
            + "cl.items_id, cl.users_id, cl.admins_id, "
            + "i.judul AS item_title, i.status AS item_status, "
            + "u.nama AS claimant_name, a.nama AS admin_name "
            + "FROM claims cl "
            + "JOIN items i ON i.id = cl.items_id "
            + "JOIN users u ON u.id = cl.users_id "
            + "LEFT JOIN admins a ON a.id = cl.admins_id";

    @Override
    protected String baseSelect() {
        return SELECT;
    }

    @Override
    protected String idColumn() {
        return "cl.id";
    }

    @Override
    protected String tableName() {
        return "claims";
    }

    @Override
    protected String prefix() {
        return "CLM";
    }

    @Override
    protected Claim mapRow(ResultSet rs) throws SQLException {
        Claim c = new Claim();
        c.setId(rs.getString("id"));
        c.setProofDescription(rs.getString("bukti_kepemilikan"));
        c.setAttachment(rs.getString("lampiran_foto"));
        c.setStatus(ClaimStatus.fromDb(rs.getString("status_klaim")));
        c.setCreatedAt(timestamp(rs, "created_at"));
        c.setItemId(rs.getString("items_id"));
        c.setUserId(rs.getString("users_id"));
        c.setAdminId(rs.getString("admins_id"));
        c.setItemTitle(rs.getString("item_title"));
        c.setItemStatus(ItemStatus.fromDb(rs.getString("item_status")));
        c.setClaimantName(rs.getString("claimant_name"));
        c.setAdminName(rs.getString("admin_name"));
        return c;
    }

    @Override
    public List<Claim> search(String keyword) throws DAOException {
        return search(keyword, null);
    }

    /** Pencarian kata kunci + filter opsional status klaim. */
    public List<Claim> search(String keyword, ClaimStatus status) throws DAOException {
        String k = like(keyword);
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT)
                .append(" WHERE (i.judul LIKE ? OR u.nama LIKE ? OR cl.bukti_kepemilikan LIKE ?)");
        params.add(k);
        params.add(k);
        params.add(k);
        if (status != null) {
            sql.append(" AND cl.status_klaim = ?");
            params.add(status.getDb());
        }
        sql.append(" ORDER BY cl.id");
        return query(sql.toString(), this::mapRow, params.toArray());
    }

    @Override
    public String insert(Claim c) throws DAOException {
        String id = nextId();
        executeUpdate("INSERT INTO claims (id, bukti_kepemilikan, lampiran_foto, status_klaim, items_id, users_id) "
                        + "VALUES (?, ?, ?, 'Menunggu', ?, ?)",
                id, c.getProofDescription(), c.getAttachment(), c.getItemId(), c.getUserId());
        c.setId(id);
        return id;
    }

    /** Klaim hanya dapat diubah selama berstatus Menunggu. */
    @Override
    public boolean update(Claim c) throws DAOException {
        return executeUpdate("UPDATE claims SET items_id = ?, users_id = ?, bukti_kepemilikan = ?, "
                        + "lampiran_foto = ? WHERE id = ? AND status_klaim = 'Menunggu'",
                c.getItemId(), c.getUserId(), c.getProofDescription(), c.getAttachment(), c.getId()) > 0;
    }

    /** Apakah pengguna sudah punya klaim aktif (Menunggu/Disetujui) pada barang ini? */
    public boolean existsActive(String itemId, String userId, String excludeClaimId) throws DAOException {
        List<Integer> r = query("SELECT COUNT(*) FROM claims WHERE items_id = ? AND users_id = ? "
                        + "AND status_klaim IN ('Menunggu', 'Disetujui') AND id <> ?",
                rs -> rs.getInt(1), itemId, userId, excludeClaimId == null ? "" : excludeClaimId);
        return r.get(0) > 0;
    }

    /** Apakah barang ini sudah punya klaim yang disetujui? */
    public boolean existsApproved(String itemId) throws DAOException {
        List<Integer> r = query("SELECT COUNT(*) FROM claims WHERE items_id = ? AND status_klaim = 'Disetujui'",
                rs -> rs.getInt(1), itemId);
        return r.get(0) > 0;
    }

    /**
     * Memproses klaim dalam SATU transaksi.
     * Disetujui  : klaim -> Disetujui, barang -> Selesai, klaim Menunggu lain pada barang yang sama -> Ditolak.
     * Ditolak    : klaim -> Ditolak.
     * Dibatalkan : klaim -> Dibatalkan (adminId boleh null).
     */
    public void process(String claimId, ClaimStatus newStatus, String adminId) throws DAOException {
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String itemId;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT items_id FROM claims WHERE id = ? FOR UPDATE")) {
                    ps.setString(1, claimId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new DAOException("Klaim tidak ditemukan (mungkin sudah dihapus).", null);
                        }
                        itemId = rs.getString(1);
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE claims SET status_klaim = ?, admins_id = ? WHERE id = ?")) {
                    ps.setString(1, newStatus.getDb());
                    ps.setString(2, adminId);
                    ps.setString(3, claimId);
                    ps.executeUpdate();
                }
                if (newStatus == ClaimStatus.DISETUJUI) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE items SET status = 'Selesai' WHERE id = ?")) {
                        ps.setString(1, itemId);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE claims SET status_klaim = 'Ditolak', admins_id = ? "
                                    + "WHERE items_id = ? AND id <> ? AND status_klaim = 'Menunggu'")) {
                        ps.setString(1, adminId);
                        ps.setString(2, itemId);
                        ps.setString(3, claimId);
                        ps.executeUpdate();
                    }
                }
                con.commit();
            } catch (SQLException | DAOException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw DAOException.from(e);
        }
    }

    /**
     * Hapus klaim. Bila klaim yang dihapus berstatus Disetujui dan barang masih Selesai,
     * status barang dikembalikan ke Terbit (satu transaksi).
     */
    @Override
    public boolean delete(String id) throws DAOException {
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String itemId = null;
                String status = null;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT items_id, status_klaim FROM claims WHERE id = ? FOR UPDATE")) {
                    ps.setString(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            itemId = rs.getString(1);
                            status = rs.getString(2);
                        }
                    }
                }
                int deleted;
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM claims WHERE id = ?")) {
                    ps.setString(1, id);
                    deleted = ps.executeUpdate();
                }
                if (deleted > 0 && ClaimStatus.DISETUJUI.getDb().equals(status)) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE items SET status = 'Terbit' WHERE id = ? AND status = 'Selesai'")) {
                        ps.setString(1, itemId);
                        ps.executeUpdate();
                    }
                }
                con.commit();
                return deleted > 0;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw DAOException.from(e);
        }
    }
}
