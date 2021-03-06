
package window;

import button.Button;
import button.Button.ButtonPlan;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import main.WindowUtil;
import student.EntryGrade;
import student.Grade;

/**
 * @author Michael
 */
public class Bar {
    private final Button[] buttons;
    private int x, y, w, h;
    private int initialScroll; //remembers its initial position before scrolling
    private final EntryGrade grade; //the grade displayed by the bar
    private Map<String, Object> settings;
    private boolean[] enabledStatuses = new boolean[6];

    private static final Button.Icon DELETE = (g, x, y, w, h) -> {
        g.drawLine(x + w / 4, y + w / 4, x + w * 3 / 4, y + w * 3 / 4); //top left - bottom right
        g.drawLine(x + w / 4, y + w * 3 / 4, x + w * 3 / 4, y + w / 4); //bottom left - top right
    };
    private static final Button.Icon HIDDEN = (g, x, y, w, h) -> {
        g.drawOval(x + w / 4, y + w / 4, w / 2, w / 2); //circle
        g.drawLine(x + w / 4, y + w * 3 / 4, x + w * 3 / 4, y + w / 4); //bottom left - top right
    };
    private static final Button.Icon SHOWN = (g, x, y, w, h) -> {
        g.drawOval(x + w / 4, y + w / 4, w / 2, w / 2); //circle
    };

    public Bar(String[] text, JPanel panel) {
        buttons = new Button[6]; //gray, date, name, weight, major, grade
        //initialize accordingly
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new Button(panel, new Rectangle(), "", DrawingPanel.BACKGROUND, Button.STANDARD);
            buttons[i].setFontStyle(Font.BOLD);
        }
        //set texts
        if (text != null)
            for (int i = 0; i < buttons.length; i++)
                if (text[i] != null)
                    buttons[i].setText(text[i]);
        initialScroll = 0;
        grade = null;
    }

    public Bar(EntryGrade grade, JPanel panel, Map<String, Object> settings, int y) {
        this.grade = grade;
        this.settings = settings;
        initialScroll = y;

        Color barColor = Grade.getColor(grade.getModStatus(), settings);
        buttons = new Button[6]; //gray, date, name, weight, major, grade
        for (int i = 0; i < buttons.length; i++)
            buttons[i] = new Button(panel, new Rectangle(), "", barColor, (ButtonPlan) settings.get("style"));
        Button.Icon icon;
        if (grade.isCustom())
            icon = DELETE;
        else if (grade.isGrayed())
            icon = HIDDEN;
        else
            icon = SHOWN;
        buttons[0].setIcon(icon);

        updateText();

        initListener0();
        initListener2();
        if (grade.isCustom()) {
            initListener3();
            initListener4();
            if (grade.getModStatus() == EntryGrade.MANIPULATED)
                initListener5();
            else
                buttons[5].setEnabled(false);
        }
        else {
            buttons[3].setEnabled(false);
            buttons[4].setEnabled(false);
            buttons[5].setEnabled(false);
        }
        buttons[1].setEnabled(false); //DATE button doesn't do anything

        for (int i = 0; i < buttons.length; i++) { //remember which ones are supposed to be enabled
            enabledStatuses[i] = buttons[i].isEnabled();
        }
    }

    private void initListener0() {
        buttons[0].addActionListener(event -> {
            boolean setGray = true;
            if (grade.isCustom()) {
                setGray = false;
                if (preventDelete) {
                    JOptionPane.showMessageDialog(null,
                            "You cannot delete this grade while CURRENT grade "
                            + "is controlled.", "Deadlock",
                            JOptionPane.WARNING_MESSAGE);
                }
                else {
                    int option = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to delete \"" + grade.getName() + "\"?",
                            "Confirm", JOptionPane.YES_NO_OPTION);
                    setGray = option == JOptionPane.OK_OPTION;
                }
            }
            if (setGray) {
                grade.setGrayed(!grade.isGrayed());
                updateText();
            }
        });
    }

    private boolean preventDelete;

    public void setPreventDelete(boolean preventDelete) {
        this.preventDelete = preventDelete;
    }

    //NAME button
    private void initListener2() {
        buttons[2].addActionListener(event -> {

            JButton[] options;
            if (!grade.isCustom())
                options = WindowUtil.makeButtons("OK", "Reset", "Cancel");
            else
                options = WindowUtil.makeButtons("OK", "Cancel");

            JPanel panel = new JPanel(); ///the panel to put in the verification dialog

            final JTextField field = new JTextField(grade.getName(), 15);
            panel.add(field);
            field.selectAll();
            Runnable updater = () -> {
                int length = field.getText().trim().length();
                options[0].setEnabled(length > 0 && length < 50); //disable if too long or short
            };
            WindowUtil.linkTextToButton(field, options[0], updater);

            int choice = JOptionPane.showOptionDialog(null, panel, "Rename",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, null);
            if (choice == 0) {//OK option
                grade.setName(field.getText().trim());
                updateText();
            }
            else if (choice == 1 && !grade.isCustom()) { //Reset option
                grade.resetName();
                updateText();
            }
        });
    }

    //WEIGHT button
    private void initListener3() {
        buttons[3].addActionListener(event -> {
            double input = WindowUtil.showSpinnerDialog("Change Grade Weight",
                    grade.getWeight(), .05, 5, .05, "0.00", false);
            if (input != -1) {
                grade.setWeight(input);
                updateText();
            }
        });
    }

    //DAILY/MAJOR button
    private void initListener4() {
        buttons[4].addActionListener(event -> {
            grade.setMajor(!grade.isMajor());
            updateText();
        });
    }

    //GRADE button
    private void initListener5() {
        buttons[5].addActionListener(event -> {
            double input = WindowUtil.showSpinnerDialog("Change Grade",
                    grade.getValue(), 0, 6, .01, "0.00", false);
            if (input != -1) {
                grade.manipulate(input);
                updateText();
            }
        });
    }

    public EntryGrade getGrade() {
        return grade;
    }

    public final void updateText() {
        if (grade.isCustom()) {
            buttons[1].setText(grade.getModStatus() == Grade.MANIPULATED ? "CONTROL" : "RESPONSE");
        }
        else {
            buttons[1].setText(String.format("%tm-%td-%tY", grade.getDate(), grade.getDate(), grade.getDate()));
        }
        buttons[2].setText(grade.getName());
        buttons[3].setText(String.format("%.2f", grade.getWeight()));
        buttons[4].setText(grade.isMajor() ? "Major" : "Daily");

        if (grade.isGrayed())
            buttons[5].setText("");
        else if (grade.isEmpty())
            buttons[5].setText("-");
        else
            buttons[5].setText(String.format("%.2f", grade.getValue()));

        int bold = grade.isMajor() ? Font.BOLD : Font.PLAIN;
        int ital = grade.isExtra() ? Font.ITALIC : Font.PLAIN;
        for (int i = 0; i < buttons.length; i++) {
            Button button = buttons[i];
            if (i != 0)
                button.setFontStyle(bold | ital);
        }

        if (!grade.isCustom())
            buttons[0].setIcon(grade.isGrayed() ? HIDDEN : SHOWN);

        if (!grade.getName().equals(grade.getOriginalName()) && !grade.isCustom()) //name has been changed
            buttons[2].setFontColor((Color) settings.get("color manipulated"));
        else
            buttons[2].setFontColorForLuminance();

        if (!grade.isCustom() && grade.getValue() < 2.0 && grade.getValue() != Grade.NO_VALUE)
            buttons[5].setFontColor(Color.RED);

    }

    public void draw(Graphics g) {
        for (Button button : buttons)
            button.draw(g);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int getInitialScroll() {
        return initialScroll;
    }

    public Button[] getButtons() {
        return buttons;
    }

    public void setDimensions(final int x, final int y, final int w, final int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        int div01 = x + h; //(h) to the right of left end
        int div12 = div01 + 4 * h; //(4*h) to the right of div01
        int div45 = x + w - 2 * h; //(h) to the left of right end (x+w)
        int div34 = div45 - 2 * h; //(2*h) to the left of div45
        int div23 = div34 - h; //(h) to the left of div34

        buttons[0].getRect().setBounds(x, y, div01 - x, h); // -1 to allow for small space between the buttons
        buttons[1].getRect().setBounds(div01 + 1, y, div12 - div01 - 1, h);
        buttons[2].getRect().setBounds(div12 + 1, y, div23 - div12 - 1, h);
        buttons[3].getRect().setBounds(div23 + 1, y, div34 - div23 - 1, h);
        buttons[4].getRect().setBounds(div34 + 1, y, div45 - div34 - 1, h);
        buttons[5].getRect().setBounds(div45 + 1, y, x + w - div45 - 1, h);
    }

    public Rectangle[] setDimensionsForEndBar(final int x, final int y, final int w, final int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        final double DIV = w / 12.0;
        int div01 = (int) (x + 4 * DIV); //(3*DIV) to the right of left end
        int div12 = (int) (x + 5 * DIV); //(DIV) to the right of div01
        int div23 = (int) (x + 6 * DIV); //(DIV) to the right of div01
        int div34 = (int) (x + 8 * DIV); //(DIV) to the right of div01
        int div45 = (int) (x + 10 * DIV); //(DIV) to the right of div01

        Rectangle[] rects = new Rectangle[6];
        rects[0] = new Rectangle(x, y, div01 - x, h); // -1 to allow for small space between the buttons
        rects[1] = new Rectangle(div01 + 1, y, div12 - div01 - 1, h);
        rects[2] = new Rectangle(div12 + 1, y, div23 - div12 - 1, h);
        rects[3] = new Rectangle(div23 + 1, y, div34 - div23 - 1, h);
        rects[4] = new Rectangle(div34 + 1, y, div45 - div34 - 1, h);
        rects[5] = new Rectangle(div45 + 1, y, x + w - div45 - 1, h);

        for (int i = 0; i < rects.length; i++) {
            buttons[i].setRect(rects[i]);
        }

        return rects;
    }

    public void setX(int x) {
        setDimensions(x, y, w, h);
    }

    public void setY(int y) {
        setDimensions(x, y, w, h);
    }

    public void setInitialScroll(int y) {
        setDimensions(x, y, w, h);
        initialScroll = y;
    }

    public void setW(int w) {
        setDimensions(x, y, w, h);
    }

    public void setH(int h) {
        setDimensions(x, y, w, h);
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            for (int i = 0; i < buttons.length; i++)
                if (buttons[i] != null)
                    buttons[i].setEnabled(enabledStatuses[i]);
        }
        else
            for (Button button : buttons)
                if (button != null)
                    button.setEnabled(false);
    }
}
