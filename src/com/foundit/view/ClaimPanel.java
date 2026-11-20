package com.foundit.view;

import com.foundit.model.Claim;
import com.foundit.model.ClaimStatus;
import com.foundit.model.ItemType;
import com.foundit.service.AdminService;
import com.foundit.service.ClaimService;
import com.foundit.service.ItemService;
import com.foundit.service.ServiceException;
import com.foundit.service.UserService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.util.List;

public class ClaimPanel extends AbstractCrudPanel<Claim> {

    private final ClaimService claimService = new ClaimService();
    private final ItemService itemService = new ItemService();
    private final UserService userService = new UserService();
    private final AdminService adminService = new AdminService();

    private final JComboBox<Option> itemCombo = new JComboBox<>();
    private final JComboBox<Option> claimantCombo = new JComboBox<>();
    private final JTextArea proofArea = new JTextArea(4, 20);
    private final JTextField attachmentField = new JTextField(14);
    private final JLabel statusLabel = new JLabel("-");
    private final JComboBox<Option> adminCombo = new JComboBox<>();

    private final JComboBox<Object> filterStatus = new JComboBox<>(UiUtil.withAll(ClaimStatus.values()));

    public ClaimPanel() {
        super(new String[]{"ID", "Barang", "Pengklaim", "Status", "Diproses Oleh", "Lampiran", "Diajukan"});
        build("Form Klaim");
    }

    @Override
    protected Object[] toRow(Claim c) {
        return new Object[]{c.getId(), c.getItemId() + " - " + c.getItemTitle(), c.getClaimantName(),
                c.getStatus(), UiUtil.orDash(c.getAdminName()), UiUtil.orDash(c.getAttachment()),
                UiUtil.format(c.getCreatedAt())};
    }

    @Override
    protected List<Claim> loadData(String keyword) throws ServiceException {
        ClaimStatus status = filterStatus.getSelectedItem() instanceof ClaimStatus s ? s : null;
        return claimService.search(keyword, status);
    }

    @Override
    protected void buildFilters(JPanel bar) {
        bar.add(new JLabel("Status:"));
        bar.add(filterStatus);
    }

    @Override
    protected void resetFilters() {
        filterStatus.setSelectedIndex(0);
    }

    @Override
    protected void buildForm() {
        addField("Barang *", itemCombo);
        addField("Pengklaim *", claimantCombo);
        addField("Bukti Kepemilikan *", scrollable(proofArea));
        addField("Lampiran Foto", UiUtil.fileRow(attachmentField));
        addField("Status", statusLabel);
        addField("Admin Pemroses", adminCombo);
        addField("", new JLabel("<html><small>Pilih admin pemroses sebelum menekan<br>"
                + "tombol Setujui / Tolak.</small></html>"));
    }

    @Override
    protected void addExtraButtons(JPanel buttons) {
        JButton approveButton = UiUtil.button("Setujui");
        JButton rejectButton = UiUtil.button("Tolak");
        JButton cancelButton = UiUtil.button("Batalkan");
        approveButton.addActionListener(e -> process(ClaimStatus.DISETUJUI));
        rejectButton.addActionListener(e -> process(ClaimStatus.DITOLAK));
        cancelButton.addActionListener(e -> process(ClaimStatus.DIBATALKAN));
        buttons.add(approveButton);
        buttons.add(rejectButton);
        buttons.add(cancelButton);
    }

    private void process(ClaimStatus target) {
        if (!hasSelection()) {
            return;
        }
        String verb = switch (target) {
            case DISETUJUI -> "MENYETUJUI";
            case DITOLAK -> "MENOLAK";
            default -> "MEMBATALKAN";
        };
        if (!UiUtil.confirm(this, "Yakin ingin " + verb + " klaim " + selected.getId() + "?")) {
            return;
        }
        execute(() -> {
            String adminId = UiUtil.selectedId(adminCombo);
            switch (target) {
                case DISETUJUI -> {
                    claimService.approve(selected.getId(), adminId);
                    UiUtil.info(this, "Klaim disetujui. Status barang menjadi Selesai.");
                }
                case DITOLAK -> {
                    claimService.reject(selected.getId(), adminId);
                    UiUtil.info(this, "Klaim ditolak.");
                }
                default -> {
                    claimService.cancel(selected.getId());
                    UiUtil.info(this, "Klaim dibatalkan.");
                }
            }
        });
    }

    @Override
    public void refreshReferences() {
        try {
            List<Option> items = itemService.findAll().stream()
                    .filter(i -> i.getType() == ItemType.FOUND)
                    .map(i -> new Option(i.getId(), i.getId() + " - " + i.getTitle() + " [" + i.getStatus() + "]"))
                    .toList();
            List<Option> users = userService.findAll().stream()
                    .map(u -> new Option(u.getId(), u.getId() + " - " + u.getFullName())).toList();
            List<Option> admins = adminService.findAll().stream()
                    .map(a -> new Option(a.getId(), a.getId() + " - " + a.getFullName())).toList();
            UiUtil.fillCombo(itemCombo, items, null);
            UiUtil.fillCombo(claimantCombo, users, null);
            UiUtil.fillCombo(adminCombo, admins, "-- Pilih admin --");
        } catch (ServiceException ex) {
            UiUtil.error(this, ex.getMessage());
        }
    }

    @Override
    protected void fillForm(Claim c) {
        UiUtil.select(itemCombo, c.getItemId());
        UiUtil.select(claimantCombo, c.getUserId());
        proofArea.setText(c.getProofDescription());
        attachmentField.setText(c.getAttachment() == null ? "" : c.getAttachment());
        statusLabel.setText(c.getStatus().toString());
        UiUtil.select(adminCombo, c.getAdminId());
    }

    @Override
    protected void resetForm() {
        if (itemCombo.getItemCount() > 0) {
            itemCombo.setSelectedIndex(0);
        }
        if (claimantCombo.getItemCount() > 0) {
            claimantCombo.setSelectedIndex(0);
        }
        if (adminCombo.getItemCount() > 0) {
            adminCombo.setSelectedIndex(0);
        }
        proofArea.setText("");
        attachmentField.setText("");
        statusLabel.setText("-");
    }

    @Override
    protected Claim readForm(String id) {
        Claim c = new Claim();
        c.setId(id);
        c.setItemId(UiUtil.selectedId(itemCombo));
        c.setUserId(UiUtil.selectedId(claimantCombo));
        c.setProofDescription(proofArea.getText());
        c.setAttachment(attachmentField.getText());
        return c;
    }

    @Override
    protected void persist(Claim c) throws ServiceException {
        claimService.save(c);
    }

    @Override
    protected void remove(Claim c) throws ServiceException {
        claimService.delete(c.getId());
    }
}
