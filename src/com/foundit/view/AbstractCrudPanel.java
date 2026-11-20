package com.foundit.view;

import com.foundit.model.BaseEntity;
import com.foundit.service.ServiceException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

/**
 * Kerangka panel CRUD: toolbar pencarian/filter, JTable di tengah, form + tombol di kanan.
 * Subclass cukup mengisi form, memetakan baris tabel, dan memanggil service.
 * Subclass HARUS memanggil build(...) di akhir konstruktornya.
 */
public abstract class AbstractCrudPanel<T extends BaseEntity> extends JPanel {

    @FunctionalInterface
    protected interface ServiceAction {
        void run() throws ServiceException;
    }

    protected final DefaultTableModel tableModel;
    protected final JTable table = new JTable();
    protected final JTextField searchField = new JTextField(18);
    protected final JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    protected final JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 8, 8));

    private final JPanel formPanel = new JPanel(new GridBagLayout());
    private int formRow = 0;
    private List<T> rows = new ArrayList<>();
    /** Entitas yang sedang dipilih di tabel (null bila tidak ada). */
    protected T selected;

    protected AbstractCrudPanel(String[] columns) {
        super(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(tableModel);
        table.setRowHeight(26);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }

    // ------------------------------------------------------------ hook untuk subclass

    protected abstract Object[] toRow(T entity);

    protected abstract List<T> loadData(String keyword) throws ServiceException;

    /** Tambahkan komponen form dengan addField(...). */
    protected abstract void buildForm();

    protected abstract void fillForm(T entity);

    protected abstract void resetForm();

    /** Baca form menjadi entitas (id = null untuk data baru). Boleh melempar ValidationException. */
    protected abstract T readForm(String id) throws ServiceException;

    /** Simpan (insert/update) lewat service. */
    protected abstract void persist(T entity) throws ServiceException;

    protected abstract void remove(T entity) throws ServiceException;

    protected void buildFilters(JPanel bar) {
    }

    protected void resetFilters() {
    }

    protected void addExtraButtons(JPanel buttons) {
    }

    /** Muat ulang data combo relasi; dipanggil saat panel ditampilkan. */
    public void refreshReferences() {
    }

    // ------------------------------------------------------------ konstruksi UI

    protected final void build(String formTitle) {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton searchButton = UiUtil.button("Cari");
        JButton resetButton = UiUtil.button("Reset Filter");
        toolbar.add(new JLabel("Cari:"));
        toolbar.add(searchField);
        buildFilters(filterBar);
        toolbar.add(filterBar);
        toolbar.add(searchButton);
        toolbar.add(resetButton);
        add(toolbar, BorderLayout.NORTH);

        buildForm();
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0;
        filler.gridy = formRow;
        filler.gridwidth = 2;
        filler.weighty = 1;
        formPanel.add(Box.createVerticalGlue(), filler);

        JButton addButton = UiUtil.button("Tambah");
        JButton updateButton = UiUtil.button("Ubah");
        JButton deleteButton = UiUtil.button("Hapus");
        JButton clearButton = UiUtil.button("Bersihkan");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        addExtraButtons(buttonPanel);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        JPanel right = new JPanel(new BorderLayout(0, 10));
        right.setBorder(BorderFactory.createTitledBorder(formTitle));
        right.add(formScroll, BorderLayout.CENTER);
        right.add(buttonPanel, BorderLayout.SOUTH);
        right.setMinimumSize(new Dimension(340, 200));
        right.setPreferredSize(new Dimension(400, 200));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), right);
        split.setResizeWeight(1.0);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        // ---- event
        searchButton.addActionListener(e -> reload());
        searchField.addActionListener(e -> reload());
        resetButton.addActionListener(e -> {
            searchField.setText("");
            resetFilters();
            reload();
        });
        addButton.addActionListener(e -> execute(() -> {
            persist(readForm(null));
            UiUtil.info(this, "Data berhasil ditambahkan.");
        }));
        updateButton.addActionListener(e -> {
            if (!hasSelection()) {
                return;
            }
            execute(() -> {
                persist(readForm(selected.getId()));
                UiUtil.info(this, "Data berhasil diperbarui.");
            });
        });
        deleteButton.addActionListener(e -> {
            if (!hasSelection()) {
                return;
            }
            if (UiUtil.confirm(this, "Yakin ingin menghapus data yang dipilih?")) {
                execute(() -> {
                    remove(selected);
                    UiUtil.info(this, "Data berhasil dihapus.");
                });
            }
        });
        clearButton.addActionListener(e -> clearForm());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) {
                return;
            }
            int modelRow = table.convertRowIndexToModel(viewRow);
            if (modelRow >= 0 && modelRow < rows.size()) {
                selected = rows.get(modelRow);
                fillForm(selected);
            }
        });

        refreshReferences();
        reload();
    }

    /** Tambah satu baris "label : komponen" pada form. */
    protected final void addField(String label, JComponent component) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = formRow;
        g.insets = new Insets(4, 4, 4, 6);
        g.anchor = GridBagConstraints.NORTHWEST;
        g.gridx = 0;
        formPanel.add(new JLabel(label), g);
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(component, g);
        formRow++;
    }

    protected final JScrollPane scrollable(JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return new JScrollPane(area);
    }

    // ------------------------------------------------------------ aksi umum

    /** Jalankan aksi service; sukses -> form dibersihkan & tabel dimuat ulang; gagal -> dialog error. */
    protected final void execute(ServiceAction action) {
        try {
            action.run();
        } catch (ServiceException ex) {
            UiUtil.error(this, ex.getMessage());
            return;
        }
        clearForm();
        reload();
    }

    protected final boolean hasSelection() {
        if (selected == null) {
            UiUtil.warn(this, "Pilih data pada tabel terlebih dahulu.");
            return false;
        }
        return true;
    }

    public final void clearForm() {
        selected = null;
        table.clearSelection();
        resetForm();
    }

    public final void reload() {
        try {
            rows = new ArrayList<>(loadData(searchField.getText()));
        } catch (ServiceException ex) {
            rows = new ArrayList<>();
            UiUtil.error(this, ex.getMessage());
        }
        tableModel.setRowCount(0);
        for (T row : rows) {
            tableModel.addRow(toRow(row));
        }
        clearForm();
    }

    /** Dipanggil ketika tab ini dipilih. */
    public final void onShow() {
        refreshReferences();
        reload();
    }
}
