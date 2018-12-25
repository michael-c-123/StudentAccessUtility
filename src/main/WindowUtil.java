
package main;

import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import student.EntryGrade;
import student.Grade;

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

    public static final int CANCELLED = -1;
    public static final int RESET = -2;

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
            return RESET;
        return CANCELLED; //cancelled or closed
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

    public static EntryGrade showCreateDialog() {
        //radio buttons, manip/response
        JRadioButton manipButton = new JRadioButton("Control");
        JRadioButton respoButton = new JRadioButton("Response");
        ButtonGroup groupMR = new ButtonGroup();
        groupMR.add(manipButton);
        groupMR.add(respoButton);
        manipButton.setSelected(true);
        JPanel radioMR = new JPanel();
        radioMR.add(manipButton);
        radioMR.add(respoButton);

        //radio buttons, major/daily
        JRadioButton majorButton = new JRadioButton("Major");
        JRadioButton dailyButton = new JRadioButton("Daily");
        ButtonGroup groupMD = new ButtonGroup();
        groupMD.add(majorButton);
        groupMD.add(dailyButton);
        majorButton.setSelected(true);
        JPanel radioMD = new JPanel();
        radioMD.add(majorButton);
        radioMD.add(dailyButton);

        JPanel namePanel = new JPanel();
        namePanel.add(new JLabel("Name"));
        JTextField nameField = new JTextField("Custom Grade", 20);
        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent fe) {
                nameField.selectAll();
            }
        });
        namePanel.add(nameField);

        JSpinner weightSpinner = WindowUtil.makeSpinner(null, 1, .05, 5, .05, "0.00");
        JPanel weightPanel = new JPanel();
        weightPanel.add(new JLabel("Weight"));
        weightPanel.add(weightSpinner);

        JSpinner gradeSpinner = WindowUtil.makeSpinner(null, 5, 0, 6, .01, "0.00");
        respoButton.addItemListener(e -> {
            boolean enable = e.getStateChange() != ItemEvent.SELECTED;
            gradeSpinner.setEnabled(enable);
        });
        JPanel gradePanel = new JPanel();
        gradePanel.add(new JLabel("Grade"));
        gradePanel.add(gradeSpinner);

        JPanel msgPanel = new JPanel();
        BoxLayout layout = new BoxLayout(msgPanel, BoxLayout.Y_AXIS);
        msgPanel.setLayout(layout);
        msgPanel.add(radioMR);
        msgPanel.add(namePanel);
        msgPanel.add(weightPanel);
        msgPanel.add(radioMD);
        msgPanel.add(gradePanel);

        JButton[] options = WindowUtil.makeButtons("OK", "Cancel");

        int option = JOptionPane.showOptionDialog(null, //parent
                msgPanel, //message
                "Create New Grade", //title
                JOptionPane.DEFAULT_OPTION, //option type
                JOptionPane.PLAIN_MESSAGE, //message type
                null, options, options[0]); //icon, options, initial value

        if (option == 0) { //OK button clicked
            int modStatus = manipButton.isSelected() ? Grade.MANIPULATED : Grade.RESPONDING;
            double grade = manipButton.isSelected() ? (Double) gradeSpinner.getValue() : Grade.NO_VALUE;
            return new EntryGrade(modStatus,
                    nameField.getText().trim(), majorButton.isSelected(),
                    (Double) weightSpinner.getValue(), grade);
        }
        return null;
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
