package com.foundit.dao;

import com.foundit.model.BaseEntity;

import java.util.List;

/** Kontrak generik operasi CRUD + pencarian untuk semua DAO (primary key bertipe String). */
public interface CrudDAO<T extends BaseEntity> {

    List<T> findAll() throws DAOException;

    List<T> search(String keyword) throws DAOException;

    T findById(String id) throws DAOException;

    /** @return id baru yang dibuat otomatis (mis. ITM011) */
    String insert(T entity) throws DAOException;

    /** @return true bila ada baris yang berubah */
    boolean update(T entity) throws DAOException;

    /** @return true bila ada baris yang terhapus */
    boolean delete(String id) throws DAOException;
}
