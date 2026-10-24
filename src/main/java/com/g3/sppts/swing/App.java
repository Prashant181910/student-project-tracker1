package com.g3.sppts.swing;

import com.g3.sppts.dao.ProjectDAO;
import com.g3.sppts.dao.UserDAO;
import com.g3.sppts.util.Bootstrap;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Entry point of the Swing desktop application. */
public class App {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // fall back to the default look and feel
        }

        try {
            Bootstrap.init(); // create tables and demo data on first run
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null,
                    "Could not open the database:\n" + e.getMessage()
                            + "\n\nCheck src/main/resources/db.properties.",
                    "Database error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Swing components must be created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new LoginFrame(new UserDAO(), new ProjectDAO()).setVisible(true));
    }
}
