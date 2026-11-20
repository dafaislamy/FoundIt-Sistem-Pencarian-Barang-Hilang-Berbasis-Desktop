package com.foundit.view;

import com.foundit.model.Claim;
import com.foundit.model.ClaimStatus;
import com.foundit.service.ClaimService;
import com.foundit.service.ServiceException;
import com.foundit.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Halaman untuk memantau status pengajuan klaim barang pengguna terhubung ke ClaimService (Database).
 */
public class StatusKlaimFrame extends JFrame {

    private final ClaimService claimService = new ClaimService();
    private JPanel klaimListPanel;
    private JLabel jumlahKlaimLabel;

    public StatusKlaimFrame() {
        UiKit.frame(this, "FoundIt | Status Klaim", 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());
        loadClaims();
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);
        root.add(new UserSidebarPanel(this, UserSidebarPanel.Menu.STATUS_KLAIM), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(26, 34, 25, 34));
        main.add(UserSidebarPanel.buildTopbar("Status Klaim", "Pantau status pengajuan klaim barang kamu."), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(22, 0, 10, 8));

        JPanel container = UiKit.card();
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(237, 240, 242)),
                new EmptyBorder(28, 28, 28, 28)
        ));
        container.setLayout(new BorderLayout(0, 22));

        JPanel statusHeader = new JPanel(new BorderLayout());
        statusHeader.setOpaque(false);
        statusHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(237, 240, 242)),
                new EmptyBorder(0, 0, 20, 0)
        ));

        JPanel headerLeft = new JPanel();
        headerLeft.setOpaque(false);
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        headerLeft.add(UiKit.label("Pengajuan Klaim", 20, true, UiKit.TEXT));
        headerLeft.add(Box.createVerticalStrut(5));
        jumlahKlaimLabel = UiKit.label("Memuat data...", 13, false, UiKit.MUTED);
        headerLeft.add(jumlahKlaimLabel);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        headerRight.setOpaque(false);
        JButton btnAjukan = UiKit.button("+ Ajukan Klaim", UiKit.BLUE, Color.WHITE);
        btnAjukan.setPreferredSize(new Dimension(155, 42));
        btnAjukan.addActionListener(e -> {
            dispose();
            new KlaimFrame().setVisible(true);
        });
        headerRight.add(btnAjukan);

        statusHeader.add(headerLeft, BorderLayout.WEST);
        statusHeader.add(headerRight, BorderLayout.EAST);
        container.add(statusHeader, BorderLayout.NORTH);

        klaimListPanel = new JPanel();
        klaimListPanel.setOpaque(false);
        klaimListPanel.setLayout(new BoxLayout(klaimListPanel, BoxLayout.Y_AXIS));
        container.add(klaimListPanel, BorderLayout.CENTER);

        body.add(container);

        JScrollPane sc = new JScrollPane(body);
        sc.setBorder(null);
        sc.getViewport().setOpaque(false);
        sc.setOpaque(false);
        sc.getVerticalScrollBar().setUnitIncrement(18);

        main.add(sc, BorderLayout.CENTER);
        root.add(main, BorderLayout.CENTER);
        return root;
    }

    private void loadClaims() {
        klaimListPanel.removeAll();
        try {
            List<Claim> all = claimService.findAll();
            Integer myId = Session.getUser() != null ? Session.getUser().getId() : null;
            List<Claim> mine = myId == null
                    ? all
                    : all.stream().filter(c -> myId.equals(c.getClaimantUserId())).toList();

            jumlahKlaimLabel.setText(mine.size() + " pengajuan klaim");

            if (mine.isEmpty()) {
                JPanel emptyPanel = new JPanel();
                emptyPanel.setBackground(new Color(248, 250, 252));
                emptyPanel.setBorder(new EmptyBorder(50, 20, 50, 20));
                emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));

                JLabel emptyTitle = UiKit.label("Belum Ada Pengajuan Klaim", 18, true, UiKit.TEXT);
                emptyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
                JLabel emptySub = UiKit.label("Kamu belum mengajukan klaim barang.", 13, false, UiKit.MUTED);
                emptySub.setAlignmentX(Component.CENTER_ALIGNMENT);

                JButton btnEmpty = UiKit.button("Ajukan Klaim", UiKit.BLUE, Color.WHITE);
                btnEmpty.setPreferredSize(new Dimension(145, 40));
                btnEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);
                btnEmpty.addActionListener(e -> {
                    dispose();
                    new KlaimFrame().setVisible(true);
                });

                emptyPanel.add(emptyTitle);
                emptyPanel.add(Box.createVerticalStrut(8));
                emptyPanel.add(emptySub);
                emptyPanel.add(Box.createVerticalStrut(20));
                emptyPanel.add(btnEmpty);
                klaimListPanel.add(emptyPanel);
            } else {
                for (int i = 0; i < mine.size(); i++) {
                    klaimListPanel.add(createClaimCard(mine.get(i)));
                    if (i < mine.size() - 1) {
                        klaimListPanel.add(Box.createVerticalStrut(15));
                    }
                }
            }
        } catch (ServiceException ex) {
            UiKit.error(this, ex.getMessage());
        }
        klaimListPanel.revalidate();
        klaimListPanel.repaint();
    }

    private JPanel createClaimCard(Claim c) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(237, 240, 242), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel top = new JPanel(new BorderLayout(15, 0));
        top.setOpaque(false);

        String itemTitle = c.getItemTitle() != null ? c.getItemTitle() : ("Barang #" + c.getItemId());
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(UiKit.label(itemTitle, 17, true, UiKit.TEXT));
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(UiKit.label("Kode Klaim #" + c.getId(), 12, false, UiKit.MUTED));
        top.add(titleBox, BorderLayout.WEST);

        ClaimStatus st = c.getStatus();
        String statusText = st != null ? st.label() : "Menunggu";
        JLabel badge = new JLabel(statusText);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(6, 12, 6, 12));

        if (st == ClaimStatus.APPROVED) {
            badge.setBackground(new Color(234, 248, 239));
            badge.setForeground(new Color(39, 174, 96));
        } else if (st == ClaimStatus.REJECTED || st == ClaimStatus.CANCELLED) {
            badge.setBackground(new Color(253, 236, 234));
            badge.setForeground(new Color(231, 76, 60));
        } else {
            badge.setBackground(new Color(255, 246, 223));
            badge.setForeground(new Color(214, 137, 16));
        }

        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge);
        top.add(badgeWrap, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        JPanel detailGrid = new JPanel(new GridLayout(1, 2, 12, 12));
        detailGrid.setOpaque(false);
        detailGrid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(240, 242, 243)),
                new EmptyBorder(15, 0, 0, 0)
        ));

        detailGrid.add(createDetailItem("Bukti Kepemilikan", c.getProofDescription() != null ? c.getProofDescription() : "-"));
        detailGrid.add(createDetailItem("Keterangan", c.getNotes() != null && !c.getNotes().isBlank() ? c.getNotes() : "-"));

        card.add(detailGrid, BorderLayout.CENTER);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 195));
        return card;
    }

    private JPanel createDetailItem(String label, String value) {
        JPanel item = new JPanel();
        item.setBackground(new Color(248, 250, 252));
        item.setBorder(new EmptyBorder(12, 14, 12, 14));
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));

        JLabel lbl = UiKit.label(label, 11, false, new Color(149, 165, 166));
        JLabel val = UiKit.label(value, 13, true, UiKit.TEXT);
        item.add(lbl);
        item.add(Box.createVerticalStrut(5));
        item.add(val);
        return item;
    }
}