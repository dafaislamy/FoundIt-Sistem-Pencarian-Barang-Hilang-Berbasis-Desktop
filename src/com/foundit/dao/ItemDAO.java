package com.foundit.dao;

import com.foundit.model.Item;
import com.foundit.model.ItemStatus;
import com.foundit.model.ItemType;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO extends AbstractDAO<Item> {

    private static final String SELECT =
            "SELECT i.id, i.judul, i.deskripsi, i.tipe, i.lokasi, i.tanggal_kejadian, i.foto, i.status, "
            + "i.created_at, i.categories_id, i.nama_penemu, i.no_telp_penemu, i.users_id, "
            + "c.nama_kategori, u.nama AS reporter_name "
            + "FROM items i "
            + "JOIN categories c ON c.id = i.categories_id "
            + "LEFT JOIN users u ON u.id = i.users_id";

    @Override
    protected String baseSelect() {
        return SELECT;
    }

    @Override
    protected String idColumn() {
        return "i.id";
    }

    @Override
    protected String tableName() {
        return "items";
    }

    @Override
    protected String prefix() {
        return "ITM";
    }

    @Override
    protected Item mapRow(ResultSet rs) throws SQLException {
        Item it = new Item();
        it.setId(rs.getString("id"));
        it.setTitle(rs.getString("judul"));
        it.setDescription(rs.getString("deskripsi"));
        it.setType(ItemType.fromDb(rs.getString("tipe")));
        it.setLocation(rs.getString("lokasi"));
        Date d = rs.getDate("tanggal_kejadian");
        it.setEventDate(d == null ? null : d.toLocalDate());
        it.setPhoto(rs.getString("foto"));
        it.setStatus(ItemStatus.fromDb(rs.getString("status")));
        it.setCreatedAt(timestamp(rs, "created_at"));
        it.setCategoryId(rs.getString("categories_id"));
        it.setFinderName(rs.getString("nama_penemu"));
        it.setFinderPhone(rs.getString("no_telp_penemu"));
        it.setReporterId(rs.getString("users_id"));
        it.setCategoryName(rs.getString("nama_kategori"));
        it.setReporterName(rs.getString("reporter_name"));
        return it;
    }

    @Override
    public List<Item> search(String keyword) throws DAOException {
        return search(keyword, null, null, null);
    }

    /** Pencarian kata kunci + filter opsional kategori, status, dan tipe. */
    public List<Item> search(String keyword, String categoryId, ItemStatus status, ItemType type)
            throws DAOException {
        String k = like(keyword);
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT)
                .append(" WHERE (i.judul LIKE ? OR i.deskripsi LIKE ? OR i.lokasi LIKE ?")
                .append(" OR COALESCE(i.nama_penemu, '') LIKE ? OR COALESCE(u.nama, '') LIKE ?)");
        for (int i = 0; i < 5; i++) {
            params.add(k);
        }
        if (categoryId != null) {
            sql.append(" AND i.categories_id = ?");
            params.add(categoryId);
        }
        if (status != null) {
            sql.append(" AND i.status = ?");
            params.add(status.getDb());
        }
        if (type != null) {
            sql.append(" AND i.tipe = ?");
            params.add(type.getDb());
        }
        sql.append(" ORDER BY i.id");
        return query(sql.toString(), this::mapRow, params.toArray());
    }

    @Override
    public String insert(Item it) throws DAOException {
        String id = nextId();
        executeUpdate("INSERT INTO items (id, judul, deskripsi, tipe, lokasi, tanggal_kejadian, foto, status, "
                        + "categories_id, nama_penemu, no_telp_penemu, users_id) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                id, it.getTitle(), it.getDescription(), it.getType().getDb(), it.getLocation(),
                Date.valueOf(it.getEventDate()), it.getPhoto(), it.getStatus().getDb(), it.getCategoryId(),
                it.getFinderName(), it.getFinderPhone(), it.getReporterId());
        it.setId(id);
        return id;
    }

    @Override
    public boolean update(Item it) throws DAOException {
        return executeUpdate("UPDATE items SET judul = ?, deskripsi = ?, tipe = ?, lokasi = ?, "
                        + "tanggal_kejadian = ?, foto = ?, status = ?, categories_id = ?, nama_penemu = ?, "
                        + "no_telp_penemu = ?, users_id = ? WHERE id = ?",
                it.getTitle(), it.getDescription(), it.getType().getDb(), it.getLocation(),
                Date.valueOf(it.getEventDate()), it.getPhoto(), it.getStatus().getDb(), it.getCategoryId(),
                it.getFinderName(), it.getFinderPhone(), it.getReporterId(), it.getId()) > 0;
    }

    public boolean updateStatus(String id, ItemStatus status) throws DAOException {
        return executeUpdate("UPDATE items SET status = ? WHERE id = ?", status.getDb(), id) > 0;
    }
}
