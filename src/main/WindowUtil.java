
package main;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Michael
 */
public class WindowUtil {
    public static JOptionPane getOptionPane(JComponent parent) {
        JOptionPane pane;
        if (parent instanceof JOptionPane)
            pane = (JOptionPane) parent;
        else
            pane = getOptionPane((JComponent) parent.getParent());
        return pane;
    }

    public static void linkTextToButton(JTextField field, JButton button, Runnable updater) {
        if (updater != null)
            field.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updater.run();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updater.run();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    updater.run();
                }
            });
        field.addActionListener(e -> button.doClick());
    }

    public static double showSpinnerDialog(String title, double start, double min, double max, double step,
            String format, boolean giveResetOption, JComponent... compenents) {

        JButton[] options;
        if (giveResetOption)
            options = makeButtons("OK", "Reset", "Cancel");
        else
            options = makeButtons("OK", "Cancel");

        JSpinner spinner = makeSpinner(options[0], start, min, max, step, format);

        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);

        for (JComponent component : compenents) {
            panel.add(component);
        }
        panel.add(spinner);

        int option = JOptionPane.showOptionDialog(null, //parent
                panel, //message
                title, //title
                JOptionPane.DEFAULT_OPTION, //option type
                JOptionPane.PLAIN_MESSAGE, //message type
                null, options, null); //icon, options, initial value

        if (option == 0)  //OK option
            return (Double) spinner.getValue();
        else if (option == 1 && giveResetOption) //Reset option
            return -2;
        return -1;
    }

    public static JSpinner makeSpinner(JButton linkedButton,
            double start, double min, double max, double step, String format) {
        if (start > max)
            start = max;
        else if (start < min)
            start = min;
        SpinnerNumberModel model = new SpinnerNumberModel(start, min, max, step);
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.NumberEditor(spinner, format));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        JTextField field = editor.getTextField();

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                final JTextField textField = (JTextField) event.getComponent();
                EventQueue.invokeLater(() -> {
                    textField.selectAll();
                });
            }
        });
        if (linkedButton != null)
            field.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(final KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        linkedButton.doClick();
                    }
                }
            });
        return spinner;
    }

    public static JButton[] makeButtons(String... names) {
        JButton[] options = new JButton[names.length];
        for (int i = 0; i < names.length; i++) {
            final JButton button = new JButton(names[i]);
            button.addActionListener(e -> {
                JOptionPane pane = WindowUtil.getOptionPane((JComponent) e.getSource());
                pane.setValue(button); //option pane gets JButton value
            });
            options[i] = button;
        }
        return options;
    }

    public static void drawCenteredString(Graphics g, String text, int x, int y, int w, int h) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int xResult = x + (w - metrics.stringWidth(text)) / 2;
        int yResult = y + ((h - metrics.getHeight()) / 2) + metrics.getAscent();
        g.drawString(text, xResult, yResult);
    }

    public static void drawCenteredString(Graphics g, String text, Rectangle rect) {
        int x = (int) rect.getX();
        int y = (int) rect.getY();
        int w = (int) rect.getWidth();
        int h = (int) rect.getHeight();
        drawCenteredString(g, text, x, y, w, h);
    }
}
