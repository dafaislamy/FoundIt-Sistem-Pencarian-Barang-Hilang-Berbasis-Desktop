package com.foundit.view;

import com.foundit.model.Claim;
import com.foundit.model.Item;
import com.foundit.service.ClaimService;
import com.foundit.service.ItemService;
import com.foundit.service.ServiceException;
import com.foundit.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Halaman Form Pengajuan Klaim Barang terhubung ke ClaimService & ItemService (Database).
 */
public class KlaimFrame extends JFrame {

    private final ItemService itemService = new ItemService();
    private final ClaimService claimService = new ClaimService();

    private final Integer preselectedItemId;
    private final String prefillTitle;

    private JComboBox<ItemOption> itemCombo;
    private JTextArea taCiriBarang, taKeterangan;
    private JButton btnCancel, btnSubmit;

    private static class ItemOption {
        final Item item;

        ItemOption(Item item) {
            this.item = item;
        }

        @Override
        public String toString() {
            String loc = item.getLocation() != null && !item.getLocation().isBlank() ? " - " + item.getLocation() : "";
            return "#" + item.getId() + " | " + item.getTitle() + loc;
        }
    }

    public KlaimFrame() {
        this(null, "");
    }

    public KlaimFrame(Integer preselectedItemId, String prefillTitle) {
        this.preselectedItemId = preselectedItemId;
        this.prefillTitle = prefillTitle != null ? prefillTitle : "";

        UiKit.frame(this, "FoundIt | Klaim Barang", 1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setContentPane(build());
        loadAvailableItems();
        bindEvents();
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);
        root.add(new UserSidebarPanel(this, UserSidebarPanel.Menu.KLAIM_BARANG), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(26, 34, 25, 34));
        main.add(UserSidebarPanel.buildTopbar("Klaim Barang", "Ajukan klaim untuk barang yang kamu kenali."), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(22, 0, 10, 8));

        JPanel container = UiKit.card();
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(237, 240, 242)),
                new EmptyBorder(28, 30, 28, 30)
        ));
        container.setLayout(new BorderLayout(0, 22));

        JPanel claimHeader = new JPanel();
        claimHeader.setOpaque(false);
        claimHeader.setLayout(new BoxLayout(claimHeader, BoxLayout.Y_AXIS));
        claimHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(237, 240, 242)),
                new EmptyBorder(0, 0, 18, 0)
        ));
        claimHeader.add(UiKit.label("Form Pengajuan Klaim", 20, true, UiKit.TEXT));
        claimHeader.add(Box.createVerticalStrut(6));
        String infoText = prefillTitle.isEmpty()
                ? "Pilih barang yang ingin diklaim dan isi bukti kepemilikan dengan jelas."
                : "Mengajukan klaim untuk: " + prefillTitle;
        claimHeader.add(UiKit.label(infoText, 13, false, UiKit.MUTED));
        container.add(claimHeader, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        itemCombo = new JComboBox<>();
        itemCombo.setFont(UiKit.REG);
        itemCombo.setBackground(Color.WHITE);
        formPanel.add(UiKit.fieldBlock("Pilih Barang *", itemCombo));
        formPanel.add(Box.createVerticalStrut(18));

        taCiriBarang = new JTextArea(4, 30);
        taCiriBarang.setFont(UiKit.REG);
        taCiriBarang.setLineWrap(true);
        taCiriBarang.setWrapStyleWord(true);
        taCiriBarang.setBorder(new EmptyBorder(10, 12, 10, 12));
        JScrollPane scrollCiri = new JScrollPane(taCiriBarang);
        scrollCiri.setPreferredSize(new Dimension(500, 115));
        scrollCiri.setBorder(BorderFactory.createLineBorder(new Color(223, 230, 233), 1, true));
        formPanel.add(UiKit.fieldBlock("Ciri-ciri Barang / Bukti Kepemilikan *", scrollCiri));
        formPanel.add(Box.createVerticalStrut(18));

        taKeterangan = new JTextArea(4, 30);
        taKeterangan.setFont(UiKit.REG);
        taKeterangan.setLineWrap(true);
        taKeterangan.setWrapStyleWord(true);
        taKeterangan.setBorder(new EmptyBorder(10, 12, 10, 12));
        JScrollPane scrollKet = new JScrollPane(taKeterangan);
        scrollKet.setPreferredSize(new Dimension(500, 100));
        scrollKet.setBorder(BorderFactory.createLineBorder(new Color(223, 230, 233), 1, true));
        formPanel.add(UiKit.fieldBlock("Keterangan / Kronologi *", scrollKet));

        container.add(formPanel, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(237, 240, 242)),
                new EmptyBorder(20, 0, 0, 0)
        ));

        btnCancel = UiKit.button("Batal", new Color(236, 240, 241), UiKit.TEXT);
        btnCancel.setPreferredSize(new Dimension(110, 42));

        btnSubmit = UiKit.button("Ajukan Klaim", UiKit.BLUE, Color.WHITE);
        btnSubmit.setPreferredSize(new Dimension(150, 42));

        actions.add(btnCancel);
        actions.add(btnSubmit);
        container.add(actions, BorderLayout.SOUTH);

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

    private void loadAvailableItems() {
        itemCombo.removeAllItems();
        try {
            List<Item> items = itemService.findAll();
            int selectedIdx = 0;
            int idx = 0;
            for (Item it : items) {
                ItemOption opt = new ItemOption(it);
                itemCombo.addItem(opt);
                if (preselectedItemId != null && preselectedItemId.equals(it.getId())) {
                    selectedIdx = idx;
                }
                idx++;
            }
            if (itemCombo.getItemCount() > 0) {
                itemCombo.setSelectedIndex(selectedIdx);
            }
        } catch (ServiceException ex) {
            UiKit.error(this, ex.getMessage());
        }
    }

    private void bindEvents() {
        btnCancel.addActionListener(e -> {
            dispose();
            new CariBarangFrame().setVisible(true);
        });
        btnSubmit.addActionListener(e -> handleSubmitClaim());
    }

    private void handleSubmitClaim() {
        if (Session.getUser() == null) {
            UiKit.warn(this, "Silakan login sebagai User terlebih dahulu.");
            return;
        }
        ItemOption selected = (ItemOption) itemCombo.getSelectedItem();
        if (selected == null || selected.item == null) {
            UiKit.warn(this, "Tidak ada barang yang dipilih untuk diklaim.");
            return;
        }

        String ciri = taCiriBarang.getText().trim();
        String keterangan = taKeterangan.getText().trim();

        if (ciri.isEmpty() || keterangan.isEmpty()) {
            UiKit.warn(this, "Harap lengkapi ciri-ciri barang dan keterangan tambahan!");
            return;
        }

        try {
            Claim claim = new Claim();
            claim.setItemId(selected.item.getId());
            claim.setClaimantUserId(Session.getUser().getId());
            claim.setProofDescription(ciri);
            claim.setNotes(keterangan);

            claimService.submit(claim);
            UiKit.info(this, "Pengajuan klaim berhasil dikirim ke database!");
            dispose();
            new StatusKlaimFrame().setVisible(true);
        } catch (ServiceException ex) {
            UiKit.warn(this, ex.getMessage());
        }
    }
}