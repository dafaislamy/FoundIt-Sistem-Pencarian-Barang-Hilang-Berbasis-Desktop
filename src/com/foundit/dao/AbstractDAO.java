package com.foundit.dao;

import com.foundit.config.DatabaseConnection;
import com.foundit.model.BaseEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Kerangka dasar DAO. Seluruh akses JDBC (PreparedStatement + try-with-resources)
 * terpusat di sini sehingga DAO turunan hanya berisi SQL dan pemetaan baris.
 * Primary key tabel berupa varchar (ITM001, USR001, ...) sehingga dibuat oleh {@link #nextId()}.
 */
public abstract class AbstractDAO<T extends BaseEntity> implements CrudDAO<T> {

    @FunctionalInterface
    protected interface RowMapper<R> {
        R map(ResultSet rs) throws SQLException;
    }

    /** SELECT lengkap (beserta JOIN) tanpa WHERE/ORDER BY. */
    protected abstract String baseSelect();

    /** Kolom id yang dipakai pada WHERE di findById (boleh memakai alias). */
    protected abstract String idColumn();

    protected abstract String tableName();

    /** Awalan id, 3 huruf (mis. "ITM"). */
    protected abstract String prefix();

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    @Override
    public List<T> findAll() throws DAOException {
        return search("");
    }

    @Override
    public T findById(String id) throws DAOException {
        List<T> result = query(baseSelect() + " WHERE " + idColumn() + " = ?", this::mapRow, id);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public boolean delete(String id) throws DAOException {
        return executeUpdate("DELETE FROM " + tableName() + " WHERE id = ?", id) > 0;
    }

    /** Id berikutnya, mis. ITM010 -> ITM011. */
    protected String nextId() throws DAOException {
        int start = prefix().length() + 1;
        List<Integer> r = query("SELECT COALESCE(MAX(CAST(SUBSTRING(id, " + start + ") AS UNSIGNED)), 0) + 1 FROM "
                + tableName(), rs -> rs.getInt(1));
        return String.format("%s%03d", prefix(), r.get(0));
    }

    // ---------------------------------------------------------------- helper JDBC

    protected <R> List<R> query(String sql, RowMapper<R> mapper, Object... params) throws DAOException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<R> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(mapper.map(rs));
                }
                return out;
            }
        } catch (SQLException e) {
            throw DAOException.from(e);
        }
    }

    protected int executeUpdate(String sql, Object... params) throws DAOException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw DAOException.from(e);
        }
    }

    protected static void bind(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    protected static String like(String keyword) {
        return "%" + (keyword == null ? "" : keyword.trim()) + "%";
    }

    protected static LocalDateTime timestamp(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts == null ? null : ts.toLocalDateTime();
    }
}
