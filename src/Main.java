import com.foundit.apk.view.IndexFrame;
import javax.swing.SwingUtilities;

/** Entry point aplikasi FoundIt Desktop. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IndexFrame().setVisible(true));
    }
}
