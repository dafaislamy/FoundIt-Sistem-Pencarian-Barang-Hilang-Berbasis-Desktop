package com.foundit.dao;

import com.foundit.model.Admin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AdminDAO extends AbstractDAO<Admin> {

    private static final String SELECT = "SELECT id, nama, email, password, no_telp, created_at FROM admins";

    @Override
    protected String baseSelect() {
        return SELECT;
    }

    @Override
    protected String idColumn() {
        return "id";
    }

    @Override
    protected String tableName() {
        return "admins";
    }

    @Override
    protected String prefix() {
        return "ADM";
    }

    @Override
    protected Admin mapRow(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setId(rs.getString("id"));
        a.setFullName(rs.getString("nama"));
        a.setEmail(rs.getString("email"));
        a.setPasswordHash(rs.getString("password"));
        a.setPhone(rs.getString("no_telp"));
        a.setCreatedAt(timestamp(rs, "created_at"));
        return a;
    }

    @Override
    public List<Admin> search(String keyword) throws DAOException {
        String k = like(keyword);
        return query(SELECT + " WHERE nama LIKE ? OR email LIKE ? OR COALESCE(no_telp, '') LIKE ? ORDER BY id",
                this::mapRow, k, k, k);
    }

    @Override
    public String insert(Admin a) throws DAOException {
        String id = nextId();
        executeUpdate("INSERT INTO admins (id, nama, email, password, no_telp) VALUES (?, ?, ?, ?, ?)",
                id, a.getFullName(), a.getEmail(), a.getPasswordHash(), a.getPhone());
        a.setId(id);
        return id;
    }

    /** passwordHash == null berarti password tidak diubah. */
    @Override
    public boolean update(Admin a) throws DAOException {
        return executeUpdate("UPDATE admins SET nama = ?, email = ?, password = COALESCE(?, password), "
                        + "no_telp = ? WHERE id = ?",
                a.getFullName(), a.getEmail(), a.getPasswordHash(), a.getPhone(), a.getId()) > 0;
    }
}