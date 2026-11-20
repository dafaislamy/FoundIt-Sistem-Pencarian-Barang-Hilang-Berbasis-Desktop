package com.foundit.service;

import com.foundit.dao.CrudDAO;
import com.foundit.dao.DAOException;
import com.foundit.model.BaseEntity;

import java.util.List;

/** Service generik: validasi, lalu delegasi ke DAO. */
public abstract class AbstractService<T extends BaseEntity> {

    protected final CrudDAO<T> dao;

    protected AbstractService(CrudDAO<T> dao) {
        this.dao = dao;
    }

    /** Validasi sekaligus normalisasi (trim) nilai entitas. */
    protected abstract void validate(T entity) throws ValidationException;

    public List<T> findAll() throws ServiceException {
        try {
            return dao.findAll();
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public List<T> search(String keyword) throws ServiceException {
        try {
            return dao.search(keyword);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public T findById(String id) throws ServiceException {
        try {
            return dao.findById(id);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /** id == null -> INSERT, selain itu -> UPDATE. */
    public void save(T entity) throws ServiceException {
        validate(entity);
        try {
            if (entity.getId() == null) {
                dao.insert(entity);
            } else if (!dao.update(entity)) {
                throw new ServiceException("Data tidak ditemukan atau tidak dapat diubah (mungkin sudah dihapus).");
            }
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public void delete(String id) throws ServiceException {
        try {
            if (!dao.delete(id)) {
                throw new ServiceException("Data tidak ditemukan (mungkin sudah dihapus).");
            }
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
