package com.foundit;

import com.foundit.config.DatabaseConnection;
import com.foundit.dao.DAOException;
import com.foundit.view.IndexFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {
            }

            try {
                DatabaseConnection.testConnection();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                        null,
                        DAOException.from(e).getMessage() + "\n\nURL: " + DatabaseConnection.getDisplayUrl(),
                        "FoundIt - Gagal terhubung ke database",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }

            new IndexFrame().setVisible(true);
        });
    }
}