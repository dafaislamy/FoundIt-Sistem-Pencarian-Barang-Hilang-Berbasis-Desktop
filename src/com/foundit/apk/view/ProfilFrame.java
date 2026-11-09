package com.foundit.apk.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Halaman profil pengguna seperti profil.php. */
public class ProfilFrame extends JFrame {
    private final String email,role;
    public ProfilFrame(String email,String role){this.email=email;this.role=role;UiKit.frame(this,"FoundIt | Profil",900,650);setContentPane(build());}
    private JPanel build(){
        JPanel root=new JPanel(new BorderLayout());root.setBackground(UiKit.BG);
        JPanel content=UiKit.card();content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));content.setBorder(new EmptyBorder(30,34,30,34));
        String nama=ProfileStore.get(email,"nama","Pengguna FoundIt");
        content.add(UiKit.header("Profil Saya","Kelola informasi akun kamu."));content.add(Box.createVerticalStrut(24));
        JLabel avatar=UiKit.label(nama.substring(0,1).toUpperCase(),24,true,Color.WHITE);avatar.setOpaque(true);avatar.setBackground(UiKit.BLUE);avatar.setHorizontalAlignment(SwingConstants.CENTER);avatar.setPreferredSize(new Dimension(64,64));avatar.setMaximumSize(new Dimension(64,64));
        content.add(avatar);content.add(Box.createVerticalStrut(10));content.add(UiKit.label(nama,20,true,UiKit.TEXT));
        content.add(UiKit.label(ProfileStore.get(email,"nim","NIM belum diisi"),12,false,UiKit.MUTED));content.add(Box.createVerticalStrut(25));
        info(content,"Nama Lengkap",nama);info(content,"NIM / Nomor Identitas",ProfileStore.get(email,"nim","-"));
        info(content,"Email",ProfileStore.get(email,"email",email));info(content,"Nomor WhatsApp",ProfileStore.get(email,"telp","-"));
        content.add(Box.createVerticalStrut(18));JButton edit=UiKit.button("Edit Profil",UiKit.BLUE,Color.WHITE);
        edit.addActionListener(e->{new EditProfilFrame(email,role).setVisible(true);dispose();});content.add(edit);
        root.add(content,BorderLayout.CENTER);return root;
    }
    private void info(JPanel p,String label,String value){p.add(UiKit.label(label,12,true,UiKit.MUTED));p.add(Box.createVerticalStrut(3));p.add(UiKit.label(value.isEmpty()?"-":value,15,false,UiKit.TEXT));p.add(Box.createVerticalStrut(12));}
}
