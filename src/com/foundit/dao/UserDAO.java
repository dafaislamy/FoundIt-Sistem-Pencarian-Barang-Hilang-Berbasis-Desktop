package com.foundit.dao;

import com.foundit.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDAO extends AbstractDAO<User> {

    private static final String SELECT =
            "SELECT id, nama, nomor_identitas, email, password, no_telp, role, created_at FROM users";

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
        return "users";
    }

    @Override
    protected String prefix() {
        return "USR";
    }

    @Override
    protected User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getString("id"));
        u.setFullName(rs.getString("nama"));
        u.setIdentityNumber(rs.getString("nomor_identitas"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password"));
        u.setPhone(rs.getString("no_telp"));
        u.setRole(rs.getString("role"));
        u.setCreatedAt(timestamp(rs, "created_at"));
        return u;
    }

    @Override
    public List<User> search(String keyword) throws DAOException {
        String k = like(keyword);
        return query(SELECT + " WHERE nama LIKE ? OR email LIKE ? OR COALESCE(no_telp, '') LIKE ?"
                + " OR COALESCE(nomor_identitas, '') LIKE ? ORDER BY id", this::mapRow, k, k, k, k);
    }

    @Override
    public String insert(User u) throws DAOException {
        String id = nextId();
        executeUpdate("INSERT INTO users (id, nama, nomor_identitas, email, password, no_telp, role) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                id, u.getFullName(), u.getIdentityNumber(), u.getEmail(), u.getPasswordHash(),
                u.getPhone(), u.getRole());
        u.setId(id);
        return id;
    }

    /** passwordHash == null berarti password tidak diubah. */
    @Override
    public boolean update(User u) throws DAOException {
        return executeUpdate("UPDATE users SET nama = ?, nomor_identitas = ?, email = ?, "
                        + "password = COALESCE(?, password), no_telp = ?, role = ? WHERE id = ?",
                u.getFullName(), u.getIdentityNumber(), u.getEmail(), u.getPasswordHash(),
                u.getPhone(), u.getRole(), u.getId()) > 0;
    }
}