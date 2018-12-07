
package window;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import main.*;
import student.Profile;

/**
 * @author Michael
 */
public class MainWindow extends JFrame {

    private DrawingPanel drawingPanel;

    public MainWindow(Profile profile) {
        super("Student Access Utility");
        drawingPanel = new DrawingPanel(profile);
        drawingPanel.setFocusable(true);
        initFrame();
    }

    private void initFrame() {
        setContentPane(drawingPanel);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                dispose();
                Driver.exit();
            }
        });
    }

    public void showFrame() {
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        getContentPane().repaint();
    }

}
