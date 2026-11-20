package com.foundit.dao;

import com.foundit.model.Category;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CategoryDAO extends AbstractDAO<Category> {

    private static final String SELECT = "SELECT id, nama_kategori FROM categories";

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
        return "categories";
    }

    @Override
    protected String prefix() {
        return "CAT";
    }

    @Override
    protected Category mapRow(ResultSet rs) throws SQLException {
        return new Category(rs.getString("id"), rs.getString("nama_kategori"));
    }

    @Override
    public List<Category> search(String keyword) throws DAOException {
        return query(SELECT + " WHERE nama_kategori LIKE ? ORDER BY nama_kategori", this::mapRow, like(keyword));
    }

    @Override
    public String insert(Category c) throws DAOException {
        String id = nextId();
        executeUpdate("INSERT INTO categories (id, nama_kategori) VALUES (?, ?)", id, c.getName());
        c.setId(id);
        return id;
    }

    @Override
    public boolean update(Category c) throws DAOException {
        return executeUpdate("UPDATE categories SET nama_kategori = ? WHERE id = ?", c.getName(), c.getId()) > 0;
    }
}
