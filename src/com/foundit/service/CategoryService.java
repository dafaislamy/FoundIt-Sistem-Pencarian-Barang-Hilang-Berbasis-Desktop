package com.foundit.service;

import com.foundit.dao.CategoryDAO;
import com.foundit.model.Category;

public class CategoryService extends AbstractService<Category> {

    public CategoryService() {
        super(new CategoryDAO());
    }

    @Override
    protected void validate(Category c) throws ValidationException {
        c.setName(Validator.required("Nama kategori", c.getName(), 50));
    }
}
