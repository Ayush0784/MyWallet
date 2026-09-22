import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf; // Just in case you want dark mode!
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {

        // --- NEW: Apply the FlatLaf Theme ---
        try {
            // You can change this to 'new FlatDarkLaf()' if you prefer Dark Mode!
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize modern theme.");
        }

        // --- Launch the App ---
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame();
            }
        });
    }
}