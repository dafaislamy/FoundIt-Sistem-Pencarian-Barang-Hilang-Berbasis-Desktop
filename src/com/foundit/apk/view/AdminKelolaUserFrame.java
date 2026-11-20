package com.foundit.apk.view;

import com.foundit.apk.controller.AdminKelolaUserController;
import com.foundit.apk.model.User;
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
 * Kelola Pengguna - FoundIt Admin (View murni).
 */
public class AdminKelolaUserFrame extends JFrame {

    private DefaultTableModel tableModel;
    private JTable table;
    private JButton btnTambah;
    private Consumer<Integer> menuNavigationHandler;

    public AdminKelolaUserFrame() {
        this(true);
    }

    public AdminKelolaUserFrame(boolean bindDefaultController) {
        setTitle("Kelola Pengguna - FoundIt Admin");
        setSize(1150, 650);
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
            
            if (i == 3) {
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

        JPanel mainLayout = new JPanel(new BorderLayout(0, 20));
        mainLayout.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainLayout.setBackground(new Color(247, 249, 252));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setOpaque(false);
        JLabel title = new JLabel("Kelola Pengguna");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel subTitle = new JLabel("Manajemen data akun mahasiswa dan pengguna aplikasi FoundIt.");
        subTitle.setForeground(new Color(119, 119, 119));
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topPanel.add(title);
        topPanel.add(subTitle);
        mainLayout.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        JPanel controlCard = new JPanel(new GridLayout(1, 4, 12, 0));
        controlCard.setBackground(Color.WHITE);
        controlCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        controlCard.setMaximumSize(new Dimension(2000, 75));

        JTextField tfSearch = new JTextField();
        JComboBox<String> cbFakultas = new JComboBox<>(new String[]{"Semua Fakultas", "Fakultas Teknik", "Fakultas Ekonomi", "Fakultas Ilmu Komputer"});
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Semua Status", "Aktif", "Nonaktif"});
        
        btnTambah = new JButton("+ Tambah");
        btnTambah.setBackground(new Color(52, 152, 219));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFocusPainted(false);

        controlCard.add(tfSearch);
        controlCard.add(cbFakultas);
        controlCard.add(cbStatus);
        controlCard.add(btnTambah);

        centerPanel.add(controlCard, BorderLayout.NORTH);

        String[] columns = {"NIM & Nama", "Fakultas / Prodi", "Kontak Email & Telp", "Status Akun", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0);

        table = new JTable(tableModel);
        table.setRowHeight(60);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainLayout.add(centerPanel, BorderLayout.CENTER);
        add(mainLayout);

        if (bindDefaultController) {
            new AdminKelolaUserController(this).bind();
        }
    }

    public void renderTable(List<User> userList) {
        tableModel.setRowCount(0);
        for (User u : userList) {
            tableModel.addRow(new Object[]{
                "<html><b>" + u.getNama() + "</b><br><font color='#777'>NIM: " + u.getNomorIdentitas() + "</font></html>",
                "Fakultas Ilmu Komputer<br><font color='#777'>Informatika (2025)</font>",
                u.getEmail() + "<br><font color='#777'>" + u.getNoTelp() + "</font>",
                "Aktif",
                "<html><font color='#3498db'><u>Nonaktifkan | Hapus</u></font></html>"
            });
        }
    }

    public void addTambahListener(ActionListener l) {
        btnTambah.addActionListener(l);
    }

    public void addTableActionClickListener(Consumer<Integer> onRowClicked) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (col == 4 && row >= 0) {
                    onRowClicked.accept(row);
                }
            }
        });
    }

    public void setMenuNavigationHandler(Consumer<Integer> handler) {
        this.menuNavigationHandler = handler;
    }
}
