package com.foundit.util;

import com.foundit.view.ThemeConfig;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * ImageHelper — utilitas untuk upload/menyalin gambar ke folder uploads/ lokal
 * dan memuat gambar barang/lampiran untuk komponen Swing.
 */
public final class ImageHelper {

    private static final String LOCAL_UPLOAD_DIR = "uploads";
    private static final String WEB_UPLOAD_DIR =
            "FoundIt-Sistem-Pencarian-Barang-Hilang-Berbasis-Web" + File.separator + "uploads";

    private ImageHelper() {
    }

    /**
     * Menyalin file gambar terpilih ke folder uploads/ proyek dan mengembalikan nama file barunya.
     */
    public static String saveUploadedImage(File sourceFile, String prefix) throws IOException {
        if (sourceFile == null || !sourceFile.isFile()) {
            throw new IOException("File gambar tidak ditemukan.");
        }
        Path uploadDir = Paths.get(LOCAL_UPLOAD_DIR);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String originalName = sourceFile.getName().replaceAll("[^a-zA-Z0-9._-]", "_");
        String newFileName = prefix + "_" + System.currentTimeMillis() + "_" + originalName;
        Path target = uploadDir.resolve(newFileName);
        Files.copy(sourceFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        return newFileName;
    }

    /**
     * Mencari file gambar pada folder uploads/ proyek atau folder uploads/ versi web.
     */
    public static File resolveImageFile(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }
        String cleanName = new File(fileName.replace('\\', '/')).getName();

        File local = new File(LOCAL_UPLOAD_DIR, cleanName);
        if (local.isFile()) {
            return local;
        }
        File web = new File(WEB_UPLOAD_DIR, cleanName);
        if (web.isFile()) {
            return web;
        }
        File direct = new File(fileName);
        if (direct.isFile()) {
            return direct;
        }
        return null;
    }

    /**
     * Memuat dan menskalakan gambar menjadi ImageIcon, atau membuat placeholder rapi bila file tidak ada.
     */
    public static ImageIcon loadScaledIcon(String fileName, int width, int height) {
        File file = resolveImageFile(fileName);
        if (file != null && !file.getName().toLowerCase().endsWith(".svg")) {
            try {
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                }
            } catch (IOException ignored) {
            }
        }
        return createPlaceholderIcon(width, height);
    }

    public static ImageIcon createPlaceholderIcon(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(ThemeConfig.ITEM_IMG_BG);
        g2.fillRect(0, 0, width, height);

        g2.setColor(new Color(0x94A3B8));
        g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(11, width / 14)));
        String text = "Tidak Ada Foto";
        FontMetrics fm = g2.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        int y = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, x, y);
        g2.dispose();
        return new ImageIcon(img);
    }
}
