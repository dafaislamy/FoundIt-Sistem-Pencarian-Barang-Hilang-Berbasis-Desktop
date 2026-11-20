package com.foundit.view;

import com.foundit.model.Category;
import com.foundit.service.CategoryService;
import com.foundit.service.ServiceException;

import javax.swing.JTextField;
import java.util.List;

public class CategoryPanel extends AbstractCrudPanel<Category> {

    private final CategoryService service = new CategoryService();
    private final JTextField nameField = new JTextField(20);

    public CategoryPanel() {
        super(new String[]{"ID", "Nama Kategori"});
        build("Form Kategori");
    }

    @Override
    protected Object[] toRow(Category c) {
        return new Object[]{c.getId(), c.getName()};
    }

    @Override
    protected List<Category> loadData(String keyword) throws ServiceException {
        return service.search(keyword);
    }

    @Override
    protected void buildForm() {
        addField("Nama Kategori *", nameField);
    }

    @Override
    protected void fillForm(Category c) {
        nameField.setText(c.getName());
    }

    @Override
    protected void resetForm() {
        nameField.setText("");
        nameField.requestFocusInWindow();
    }

    @Override
    protected Category readForm(String id) {
        Category c = new Category();
        c.setId(id);
        c.setName(nameField.getText());
        return c;
    }

    @Override
    protected void persist(Category c) throws ServiceException {
        service.save(c);
    }

    @Override
    protected void remove(Category c) throws ServiceException {
        service.delete(c.getId());
    }
}
