package com.foundit.apk.view;

import com.foundit.apk.data.ReportRepository;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Pencarian laporan barang, mengikuti alur Cari Barang pada website. */
public class CariBarangFrame extends JFrame {
    private final String email, role;
    private final ReportRepository repo = ReportRepository.getInstance();
    private JTextField keyword;
    private JComboBox<String> tipe;
    private JPanel results;

    public CariBarangFrame(String email, String role) {
        this.email=email; this.role=role;
        UiKit.frame(this, "FoundIt | Cari Barang", 1200, 760);
        setContentPane(build());
        search();
    }
    private JPanel build() {
        JPanel root=new JPanel(new BorderLayout()); root.setBackground(UiKit.BG);
        JPanel side=new JPanel(); side.setBackground(Color.WHITE); side.setPreferredSize(new Dimension(225,0));
        side.setLayout(new BoxLayout(side,BoxLayout.Y_AXIS)); side.setBorder(new EmptyBorder(25,18,20,18));
        side.add(UiKit.label("FoundIt",26,true,UiKit.BLUE_DARK)); side.add(Box.createVerticalStrut(28));
        side.add(nav("Dashboard",()->goDashboard())); side.add(nav("Cari Barang",()->{}));
        side.add(nav("Lapor Barang Hilang",()->{new LaporHilangFrame(email,role).setVisible(true);dispose();}));
        side.add(nav("Lapor Barang Ditemukan",()->{new LaporDitemukanFrame(email,role).setVisible(true);dispose();}));
        side.add(nav("Status Klaim",()->{new StatusKlaimFrame(email,role).setVisible(true);dispose();}));
        side.add(nav("Riwayat Laporan",()->{new RiwayatLaporanFrame(email,role).setVisible(true);dispose();}));
        side.add(nav("Profil",()->{new ProfilFrame(email,role).setVisible(true);dispose();}));
        side.add(Box.createVerticalGlue()); side.add(nav("Keluar",()->{new IndexFrame().setVisible(true);dispose();}));
        root.add(side,BorderLayout.WEST);
        JPanel main=new JPanel(new BorderLayout(0,18)); main.setOpaque(false); main.setBorder(new EmptyBorder(28,34,28,34));
        main.add(UiKit.header("Cari Barang","Temukan laporan barang hilang atau ditemukan berdasarkan kata kunci."),BorderLayout.NORTH);
        JPanel body=new JPanel(new BorderLayout(0,15)); body.setOpaque(false);
        JPanel searchBox=UiKit.card(); searchBox.setLayout(new BorderLayout(10,10));
        JPanel fields=new JPanel(new BorderLayout(10,0)); fields.setOpaque(false);
        keyword=UiKit.field(); keyword.setToolTipText("Nama barang, deskripsi, atau lokasi");
        tipe=new JComboBox<>(new String[]{"Semua jenis","HILANG","DITEMUKAN"});
        fields.add(keyword,BorderLayout.CENTER); fields.add(tipe,BorderLayout.EAST);
        JButton btn=UiKit.button("Cari",UiKit.BLUE,Color.WHITE); btn.setPreferredSize(new Dimension(100,42));
        btn.addActionListener(e->search()); keyword.addActionListener(e->search()); tipe.addActionListener(e->search());
        searchBox.add(fields,BorderLayout.CENTER); searchBox.add(btn,BorderLayout.EAST);
        body.add(searchBox,BorderLayout.NORTH);
        results=new JPanel(); results.setOpaque(false); results.setLayout(new BoxLayout(results,BoxLayout.Y_AXIS));
        JScrollPane scroll=new JScrollPane(results); scroll.setBorder(null); scroll.getViewport().setOpaque(false); scroll.setOpaque(false);
        body.add(scroll,BorderLayout.CENTER); main.add(body,BorderLayout.CENTER); root.add(main,BorderLayout.CENTER); return root;
    }
    private JPanel nav(String text,Runnable action) {
        JPanel p=new JPanel(new BorderLayout()); p.setBackground(Color.WHITE); p.setBorder(new EmptyBorder(12,8,12,8));
        JLabel l=UiKit.label(text,13,true,UiKit.MUTED); p.add(l); p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new java.awt.event.MouseAdapter(){public void mouseClicked(java.awt.event.MouseEvent e){action.run();}});
        p.setMaximumSize(new Dimension(210,44)); return p;
    }
    private void search() {
        results.removeAll(); String q=keyword.getText().trim(); String filter=(String)tipe.getSelectedItem(); int count=0;
        List<ReportRepository.Report> all=repo.findAll();
        for(int i=all.size()-1;i>=0;i--) {
            ReportRepository.Report r=all.get(i);
            boolean typeOk="Semua jenis".equals(filter)||filter.equalsIgnoreCase(r.getType());
            boolean textOk=q.isEmpty()||r.getName().toLowerCase().contains(q.toLowerCase())||
                r.getLocation().toLowerCase().contains(q.toLowerCase())||r.getDescription().toLowerCase().contains(q.toLowerCase());
            if(typeOk&&textOk) { results.add(card(r)); results.add(Box.createVerticalStrut(10)); count++; }
        }
        if(count==0) {
            JPanel empty=UiKit.card(); empty.setLayout(new BoxLayout(empty,BoxLayout.Y_AXIS));
            empty.add(UiKit.label(all.isEmpty()?"Belum Ada Data Barang":"Barang Tidak Ditemukan",18,true,UiKit.TEXT));
            empty.add(Box.createVerticalStrut(7));
            empty.add(UiKit.label(all.isEmpty()?"Laporan yang dibuat akan muncul di sini.":"Coba kata kunci atau jenis barang yang lain.",13,false,UiKit.MUTED));
            results.add(empty);
        }
        results.revalidate(); results.repaint();
    }
    private JPanel card(ReportRepository.Report r) {
        JPanel p=UiKit.card(); p.setLayout(new BorderLayout(12,8));
        JPanel info=new JPanel(); info.setOpaque(false); info.setLayout(new BoxLayout(info,BoxLayout.Y_AXIS));
        boolean lost="HILANG".equalsIgnoreCase(r.getType());
        info.add(UiKit.label(lost?"BARANG HILANG":"BARANG DITEMUKAN",11,true,lost?new Color(210,75,65):UiKit.GREEN));
        info.add(Box.createVerticalStrut(5)); info.add(UiKit.label(r.getName(),17,true,UiKit.TEXT));
        info.add(Box.createVerticalStrut(4)); info.add(UiKit.label(r.getCategory()+" • "+r.getLocation()+" • "+r.getDate(),12,false,UiKit.MUTED));
        info.add(Box.createVerticalStrut(5)); info.add(UiKit.label(r.getDescription(),12,false,UiKit.TEXT));
        p.add(info,BorderLayout.CENTER);
        JButton detail=UiKit.button("Lihat Detail",UiKit.BLUE,Color.WHITE); detail.setPreferredSize(new Dimension(125,40));
        detail.addActionListener(e->{new DetailBarangFrame(email,role,r).setVisible(true);dispose();}); p.add(detail,BorderLayout.EAST);
        return p;
    }
    private void goDashboard(){new UserDashboardFrame(email,role).setVisible(true);dispose();}
}
