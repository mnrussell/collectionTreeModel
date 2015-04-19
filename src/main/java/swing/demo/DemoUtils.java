package swing.demo;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class DemoUtils {

    public static void createAndShowDemoFrame(final String demoTitle,
            final Container demoContainer) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(demoTitle);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(demoContainer);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
