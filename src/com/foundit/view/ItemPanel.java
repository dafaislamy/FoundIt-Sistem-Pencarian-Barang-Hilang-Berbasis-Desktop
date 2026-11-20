package com.foundit.view;

import com.foundit.model.Item;
import com.foundit.model.ItemStatus;
import com.foundit.model.ItemType;
import com.foundit.service.CategoryService;
import com.foundit.service.ItemService;
import com.foundit.service.ServiceException;
import com.foundit.service.UserService;
import com.foundit.service.Validator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.time.LocalDate;
import java.util.List;

public class ItemPanel extends AbstractCrudPanel<Item> {

    private final ItemService itemService = new ItemService();
    private final CategoryService categoryService = new CategoryService();
    private final UserService userService = new UserService();

    private final JTextField titleField = new JTextField(20);
    private final JComboBox<ItemType> typeCombo = new JComboBox<>(ItemType.values());
    private final JComboBox<Option> categoryCombo = new JComboBox<>();
    private final JComboBox<ItemStatus> statusCombo = new JComboBox<>(ItemStatus.values());
    private final JTextField dateField = new JTextField(10);
    private final JTextField locationField = new JTextField(20);
    private final JComboBox<Option> reporterCombo = new JComboBox<>();
    private final JTextField finderNameField = new JTextField(20);
    private final JTextField finderPhoneField = new JTextField(20);
    private final JTextField photoField = new JTextField(14);
    private final JTextArea descriptionArea = new JTextArea(4, 20);

    private final JComboBox<Option> filterCategory = new JComboBox<>();
    private final JComboBox<Object> filterStatus = new JComboBox<>(UiUtil.withAll(ItemStatus.values()));
    private final JComboBox<Object> filterType = new JComboBox<>(UiUtil.withAll(ItemType.values()));

    public ItemPanel() {
        super(new String[]{"ID", "Judul", "Tipe", "Kategori", "Lokasi", "Tanggal", "Status",
                "Pelapor / Penemu"});
        build("Form Barang");
    }

    @Override
    protected Object[] toRow(Item it) {
        String who = it.getType() == ItemType.FOUND
                ? UiUtil.orDash(it.getFinderName()) : UiUtil.orDash(it.getReporterName());
        return new Object[]{it.getId(), it.getTitle(), it.getType(), it.getCategoryName(), it.getLocation(),
                it.getEventDate(), it.getStatus(), who};
    }

    @Override
    protected List<Item> loadData(String keyword) throws ServiceException {
        String categoryId = UiUtil.selectedId(filterCategory);
        ItemStatus status = filterStatus.getSelectedItem() instanceof ItemStatus s ? s : null;
        ItemType type = filterType.getSelectedItem() instanceof ItemType t ? t : null;
        return itemService.search(keyword, categoryId, status, type);
    }

    @Override
    protected void buildFilters(JPanel bar) {
        bar.add(new JLabel("Kategori:"));
        bar.add(filterCategory);
        bar.add(new JLabel("Status:"));
        bar.add(filterStatus);
        bar.add(new JLabel("Tipe:"));
        bar.add(filterType);
    }

    @Override
    protected void resetFilters() {
        if (filterCategory.getItemCount() > 0) {
            filterCategory.setSelectedIndex(0);
        }
        filterStatus.setSelectedIndex(0);
        filterType.setSelectedIndex(0);
    }

    @Override
    protected void buildForm() {
        addField("Judul *", titleField);
        addField("Tipe *", typeCombo);
        addField("Kategori *", categoryCombo);
        addField("Status *", statusCombo);
        addField("Tanggal Kejadian *", dateField);
        addField("Lokasi *", locationField);
        addField("Pelapor (Hilang)", reporterCombo);
        addField("Nama Penemu", finderNameField);
        addField("Telp. Penemu", finderPhoneField);
        addField("Foto (nama file)", UiUtil.fileRow(photoField));
        addField("Deskripsi *", scrollable(descriptionArea));
        dateField.setToolTipText("Format: yyyy-MM-dd");
        addField("", new JLabel("<html><small>Tipe Hilang: pelapor wajib dipilih.<br>"
                + "Tipe Ditemukan: nama penemu wajib diisi.</small></html>"));
    }

    @Override
    protected void addExtraButtons(JPanel buttons) {
        buttons.add(statusButton("Terbitkan", ItemStatus.TERBIT, "Terbitkan laporan ini?"));
        buttons.add(statusButton("Tolak Laporan", ItemStatus.DITOLAK, "Tolak laporan ini?"));
        buttons.add(statusButton("Tandai Selesai", ItemStatus.SELESAI, "Tandai barang ini sebagai Selesai?"));
        buttons.add(statusButton("Arsipkan", ItemStatus.ARSIP, "Arsipkan barang ini?"));
    }

    private JButton statusButton(String text, ItemStatus target, String question) {
        JButton b = UiUtil.button(text);
        b.addActionListener(e -> {
            if (!hasSelection() || !UiUtil.confirm(this, question)) {
                return;
            }
            execute(() -> {
                itemService.changeStatus(selected.getId(), target);
                UiUtil.info(this, "Status barang diubah menjadi " + target + ".");
            });
        });
        return b;
    }

    @Override
    public void refreshReferences() {
        try {
            List<Option> categories = categoryService.findAll().stream()
                    .map(c -> new Option(c.getId(), c.getName())).toList();
            List<Option> users = userService.findAll().stream()
                    .map(u -> new Option(u.getId(), u.getId() + " - " + u.getFullName())).toList();
            UiUtil.fillCombo(categoryCombo, categories, null);
            UiUtil.fillCombo(filterCategory, categories, UiUtil.ALL);
            UiUtil.fillCombo(reporterCombo, users, "-- Tidak ada --");
        } catch (ServiceException ex) {
            UiUtil.error(this, ex.getMessage());
        }
    }

    @Override
    protected void fillForm(Item it) {
        titleField.setText(it.getTitle());
        typeCombo.setSelectedItem(it.getType());
        UiUtil.select(categoryCombo, it.getCategoryId());
        statusCombo.setSelectedItem(it.getStatus());
        dateField.setText(it.getEventDate() == null ? "" : it.getEventDate().toString());
        locationField.setText(it.getLocation());
        UiUtil.select(reporterCombo, it.getReporterId());
        finderNameField.setText(it.getFinderName() == null ? "" : it.getFinderName());
        finderPhoneField.setText(it.getFinderPhone() == null ? "" : it.getFinderPhone());
        photoField.setText(it.getPhoto() == null ? "" : it.getPhoto());
        descriptionArea.setText(it.getDescription() == null ? "" : it.getDescription());
    }

    @Override
    protected void resetForm() {
        titleField.setText("");
        typeCombo.setSelectedItem(ItemType.FOUND);
        statusCombo.setSelectedItem(ItemStatus.PENDING);
        dateField.setText(LocalDate.now().toString());
        locationField.setText("");
        finderNameField.setText("");
        finderPhoneField.setText("");
        photoField.setText("");
        descriptionArea.setText("");
        if (categoryCombo.getItemCount() > 0) {
            categoryCombo.setSelectedIndex(0);
        }
        if (reporterCombo.getItemCount() > 0) {
            reporterCombo.setSelectedIndex(0);
        }
        titleField.requestFocusInWindow();
    }

    @Override
    protected Item readForm(String id) throws ServiceException {
        Item it = new Item();
        it.setId(id);
        it.setTitle(titleField.getText());
        it.setType((ItemType) typeCombo.getSelectedItem());
        it.setCategoryId(UiUtil.selectedId(categoryCombo));
        it.setStatus((ItemStatus) statusCombo.getSelectedItem());
        it.setEventDate(Validator.parseDate("Tanggal kejadian", dateField.getText()));
        it.setLocation(locationField.getText());
        it.setReporterId(UiUtil.selectedId(reporterCombo));
        it.setFinderName(finderNameField.getText());
        it.setFinderPhone(finderPhoneField.getText());
        it.setPhoto(photoField.getText());
        it.setDescription(descriptionArea.getText());
        return it;
    }

    @Override
    protected void persist(Item it) throws ServiceException {
        itemService.save(it);
    }

    @Override
    protected void remove(Item it) throws ServiceException {
        itemService.delete(it.getId());
    }
}
