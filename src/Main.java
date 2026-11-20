import com.foundit.apk.controller.IndexController;
import javax.swing.SwingUtilities;

/**
 * Entry point aplikasi FoundIt Desktop.
 * Hanya menginisialisasi dan menghubungkan Model, View, dan Controller.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IndexController controller = new IndexController();
            controller.init();
        });
    }
}
