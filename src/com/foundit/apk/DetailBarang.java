package com.foundit.apk.view;

import com.foundit.apk.data.ReportRepository;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Detail laporan dan pengajuan klaim mengikuti alur detail-barang -> klaim di web. */
public class DetailBarangFrame extends JFrame {
    private final String email,role;
    private final ReportRepository.Report report;
    public DetailBarangFrame(String email,String role,ReportRepository.Report report) {
        this.email=email; this.role=role; this.report=report;
        UiKit.frame(this,"FoundIt | Detail Barang",900,650); setContentPane(build());
    }
    private JPanel build() {
        JPanel root=new JPanel(new BorderLayout()); root.setBackground(UiKit.BG);
        JPanel content=UiKit.card(); content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(30,34,30,34));
        content.add(UiKit.header("Detail Barang","Informasi lengkap laporan barang."));
        content.add(Box.createVerticalStrut(22));
        addInfo(content,"Nama Barang",report.getName()); addInfo(content,"Jenis Laporan",report.getType());
        addInfo(content,"Kategori",report.getCategory()); addInfo(content,"Lokasi",report.getLocation());
        addInfo(content,"Tanggal Kejadian",report.getDate()); addInfo(content,"Deskripsi / Ciri Barang",report.getDescription());
        content.add(Box.createVerticalStrut(20));
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0)); actions.setOpaque(false);
        JButton back=UiKit.button("Kembali ke Pencarian",UiKit.PALE,UiKit.BLUE_DARK);
        back.addActionListener(e->{new CariBarangFrame(email,role).setVisible(true);dispose();}); actions.add(back);
        if("DITEMUKAN".equalsIgnoreCase(report.getType())) {
            JButton claim=UiKit.button("Ajukan Klaim",UiKit.BLUE,Color.WHITE);
            claim.addActionListener(e->claim()); actions.add(claim);
        }
        content.add(actions); root.add(content,BorderLayout.CENTER); return root;
    }
    private void addInfo(JPanel p,String title,String value) {
        p.add(UiKit.label(title,12,true,UiKit.MUTED)); p.add(Box.createVerticalStrut(3));
        p.add(UiKit.label(value==null||value.isEmpty()?"-":value,15,false,UiKit.TEXT)); p.add(Box.createVerticalStrut(13));
    }
    private void claim() {
        JTextArea proof=new JTextArea(5,35); proof.setLineWrap(true); proof.setWrapStyleWord(true);
        int choice=JOptionPane.showConfirmDialog(this,new Object[]{"Jelaskan bukti kepemilikan (minimal 20 karakter):",new JScrollPane(proof)},"Ajukan Klaim",JOptionPane.OK_CANCEL_OPTION);
        if(choice==JOptionPane.OK_OPTION) {
            String text=proof.getText().trim();
            if(text.length()<20){UiKit.info(this,"Bukti kepemilikan minimal 20 karakter.");return;}
            StatusKlaimFrame.tambahKlaim(report.getName(),report.getCategory(),report.getLocation(),report.getDescription(),text);
            UiKit.info(this,"Klaim berhasil diajukan dan menunggu verifikasi.");
            new StatusKlaimFrame(email,role).setVisible(true); dispose();
        }
    }
}
