package com.foundit.apk.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Form edit profil: nama, nomor identitas, email, telepon seperti edit-profil.php. */
public class EditProfilFrame extends JFrame {
    private final String loginEmail,role;
    private JTextField nama,nim,email,telp;
    public EditProfilFrame(String email,String role) {
        this.loginEmail=email; this.role=role;
        UiKit.frame(this,"FoundIt | Edit Profil",850,680); setContentPane(build());
    }
    private JPanel build() {
        JPanel root=new JPanel(new BorderLayout()); root.setBackground(UiKit.BG);
        JPanel form=UiKit.card(); form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS)); form.setBorder(new EmptyBorder(28,34,28,34));
        form.add(UiKit.header("Edit Profil","Perbarui informasi akun kamu.")); form.add(Box.createVerticalStrut(24));
        nama=UiKit.field(); nama.setText(ProfileStore.get(loginEmail,"nama","Pengguna FoundIt"));
        nim=UiKit.field(); nim.setText(ProfileStore.get(loginEmail,"nim",""));
        email=UiKit.field(); email.setText(ProfileStore.get(loginEmail,"email",loginEmail));
        telp=UiKit.field(); telp.setText(ProfileStore.get(loginEmail,"telp",""));
        form.add(UiKit.fieldBlock("Nama Lengkap",nama)); form.add(Box.createVerticalStrut(14));
        form.add(UiKit.fieldBlock("NIM / Nomor Identitas",nim)); form.add(Box.createVerticalStrut(14));
        form.add(UiKit.fieldBlock("Email",email)); form.add(Box.createVerticalStrut(14));
        form.add(UiKit.fieldBlock("Nomor WhatsApp",telp)); form.add(Box.createVerticalStrut(24));
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.LEFT)); buttons.setOpaque(false);
        JButton cancel=UiKit.button("Batal",UiKit.PALE,UiKit.BLUE_DARK); cancel.addActionListener(e->openProfile());
        JButton save=UiKit.button("Simpan Perubahan",UiKit.BLUE,Color.WHITE); save.addActionListener(e->save());
        buttons.add(cancel); buttons.add(save); form.add(buttons); root.add(form,BorderLayout.CENTER); return root;
    }
    private void save() {
        String n=nama.getText().trim(), em=email.getText().trim(), id=nim.getText().trim(), hp=telp.getText().trim();
        if(n.isEmpty()||n.length()>100){UiKit.info(this,"Nama wajib diisi (maksimal 100 karakter).");return;}
        if(!em.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")){UiKit.info(this,"Format email tidak valid.");return;}
        if(!id.isEmpty()&&!id.matches("[A-Za-z0-9]{3,20}")){UiKit.info(this,"NIM harus 3-20 karakter huruf/angka.");return;}
        if(!hp.isEmpty()&&!hp.matches("[0-9+\\-\\s]{8,20}")){UiKit.info(this,"Nomor WhatsApp tidak valid.");return;}
        ProfileStore.set(loginEmail,"nama",n); ProfileStore.set(loginEmail,"nim",id);
        ProfileStore.set(loginEmail,"email",em); ProfileStore.set(loginEmail,"telp",hp);
        UiKit.info(this,"Profil berhasil diperbarui."); openProfile();
    }
    private void openProfile(){new ProfilFrame(loginEmail,role).setVisible(true);dispose();}
}
