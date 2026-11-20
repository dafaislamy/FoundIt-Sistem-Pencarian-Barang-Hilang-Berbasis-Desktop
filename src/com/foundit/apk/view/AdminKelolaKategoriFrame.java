package com.foundit.apk.view;

import com.foundit.apk.controller.AdminKelolaKategoriController;
import com.foundit.apk.model.Category;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Kelola Kategori - FoundIt Admin (View murni).
 */
public class AdminKelolaKategoriFrame extends JFrame {

    private JTextField tfNama;
    private JTextArea taDesc;
    private JButton btnSimpan;
    private DefaultTableModel tableModel;
    private JTable table;
    private Consumer<Integer> menuNavigationHandler;

    public AdminKelolaKategoriFrame() {
        this(true);
    }

    public AdminKelolaKategoriFrame(boolean bindDefaultController) {
        setTitle("Kelola Kategori - FoundIt Admin");
        setSize(1150, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(229, 231, 235)));
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setLayout(new BorderLayout());

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 25));
        logoPanel.setOpaque(false);
        JLabel logoLabel = new JLabel("<html>Found<font color='#3498db'>It</font></html>");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoPanel.add(logoLabel);
        sidebar.add(logoPanel, BorderLayout.NORTH);

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        String[] menuItems = {
            "Dashboard", "Kelola Kategori", "Kelola Laporan Barang", 
            "Kelola User", "Ubah Status Barang", "Verifikasi Klaim", "Verifikasi Laporan"
        };

        for (int i = 0; i < menuItems.length; i++) {
            JButton btnMenu = new JButton(menuItems[i]);
            btnMenu.setHorizontalAlignment(SwingConstants.LEFT);
            btnMenu.setMaximumSize(new Dimension(210, 42));
            btnMenu.setFocusPainted(false);
            btnMenu.setBorderPainted(false);
            
            if (i == 1) {
                btnMenu.setBackground(new Color(234, 245, 252));
                btnMenu.setForeground(new Color(52, 152, 219));
                btnMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));
            } else {
                btnMenu.setBackground(Color.WHITE);
                btnMenu.setForeground(new Color(102, 112, 133));
                btnMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }

            final int index = i;
            btnMenu.addActionListener(e -> {
                if (menuNavigationHandler != null) {
                    menuNavigationHandler.accept(index);
                }
            });

            navPanel.add(btnMenu);
            navPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        }
        sidebar.add(navPanel, BorderLayout.CENTER);
        add(sidebar, BorderLayout.WEST);

        JPanel mainLayout = new JPanel(new BorderLayout(0, 25));
        mainLayout.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainLayout.setBackground(new Color(247, 249, 252));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setOpaque(false);
        JLabel title = new JLabel("Kelola Kategori Barang");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel subTitle = new JLabel("Atur kategori barang untuk mempermudah pencarian dan filter pengguna.");
        subTitle.setForeground(new Color(119, 119, 119));
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topPanel.add(title);
        topPanel.add(subTitle);
        mainLayout.add(topPanel, BorderLayout.NORTH);

        JPanel contentGrid = new JPanel(new GridLayout(1, 2, 25, 0));
        contentGrid.setOpaque(false);

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel formTitle = new JLabel("Tambah Kategori Baru");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        formCard.add(new JLabel("Nama Kategori"));
        formCard.add(Box.createRigidArea(new Dimension(0, 8)));
        tfNama = new JTextField();
        tfNama.setMaximumSize(new Dimension(2000, 38));
        formCard.add(tfNama);
        formCard.add(Box.createRigidArea(new Dimension(0, 15)));

        formCard.add(new JLabel("Keterangan / Deskripsi"));
        formCard.add(Box.createRigidArea(new Dimension(0, 8)));
        taDesc = new JTextArea(4, 20);
        taDesc.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formCard.add(new JScrollPane(taDesc));
        formCard.add(Box.createRigidArea(new Dimension(0, 25)));

        btnSimpan = new JButton("Simpan Kategori");
        btnSimpan.setBackground(new Color(52, 152, 219));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);
        btnSimpan.setMaximumSize(new Dimension(2000, 42));
        formCard.add(btnSimpan);

        contentGrid.add(formCard);

        String[] columns = {"Nama Kategori & Deskripsi", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0);

        table = new JTable(tableModel);
        table.setRowHeight(60);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane tableScroll = new JScrollPane(table);
        contentGrid.add(tableScroll);

        mainLayout.add(contentGrid, BorderLayout.CENTER);
        add(mainLayout);

        if (bindDefaultController) {
            new AdminKelolaKategoriController(this).bind();
        }
    }

    public void renderTable(List<Category> kategoriList) {
        tableModel.setRowCount(0);
        for (Category c : kategoriList) {
            String desc = "Handphone, Laptop, Flashdisk, Charger";
            if (c.getNamaKategori().equals("Aksesoris")) desc = "Kunci kendaraan, Dompet, Jam Tangan";
            else if (c.getNamaKategori().equals("Dokumen")) desc = "KTM, KTP, SIM, Buku Catatan";
            else if (c.getNamaKategori().equals("Lainnya")) desc = "Payung, Jaket, Tumbler";
            
            tableModel.addRow(new Object[]{
                "<html><b>" + c.getNamaKategori() + "</b><br><font color='#777'>" + desc + "</font></html>",
                "<html><font color='#3498db'><u>Edit | Hapus</u></font></html>"
            });
        }
    }

    public String getNamaKategori() { return tfNama.getText().trim(); }
    public void setNamaKategori(String val) { tfNama.setText(val); }
    public String getDeskripsiKategori() { return taDesc.getText().trim(); }
    public void setDeskripsiKategori(String val) { taDesc.setText(val); }

    public void addSimpanListener(ActionListener l) { btnSimpan.addActionListener(l); }

    public void addTableActionClickListener(Consumer<Integer> onRowClicked) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (col == 1 && row >= 0) {
                    onRowClicked.accept(row);
                }
            }
        });
    }

    public void setMenuNavigationHandler(Consumer<Integer> handler) {
        this.menuNavigationHandler = handler;
    }
}
