
package window;

import button.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import main.WindowUtil;
import student.Course;
import student.Grade;

/**
 * @author Michael
 */
public final class EndBar extends Bar {

    private Course course;
    private Map<String, Object> settings;

    public EndBar(Course course, JPanel panel, Map<String, Object> settings) {
        super(null, panel);
        this.course = course;
        this.settings = settings;
        initListeners();
        updateIcons();
    }

    private void initListeners() {
        getButtons()[0].addActionListener(event -> {
            if (course.getCurrent(false).isFixed()
                    && course.getExam().isFixed()
                    && course.getSem().isFixed()) {
                JOptionPane.showMessageDialog(null,
                        "Reset another controlled value to control this value.", "Deadlock", JOptionPane.WARNING_MESSAGE);
            }
            else {
                showChangeWindow(course.getCurrent(true), "Control Current Grade");
            }
        });
        getButtons()[1].setEnabled(false);
        getButtons()[2].setEnabled(false);
        if (course.isOnM1())
            getButtons()[3].addActionListener(event -> showChangeWindow(course.getCurrent(false), "Control Next Quarter Grade"));
        else
            getButtons()[3].setEnabled(false);
        getButtons()[4].addActionListener(event -> showChangeWindow(course.getExam(), "Control Exam Grade"));
        getButtons()[5].addActionListener(event -> showChangeWindow(course.getSem(), "Control Semester Grade"));
    }

    private void showChangeWindow(Grade grade, String title) {
        double start = grade.isEmpty() ? 100 : grade.getValue();
        double input = WindowUtil.showSpinnerDialog(title,
                start, 0, 120, .1, "0.0", true);
        if (input >= 0)
            grade.manipulate(input);
        else if (input == WindowUtil.RESET)
            grade.reset();
    }

    public void updateIcons() {
        String OTHER = course.isOnM1() ? "NEXT" : "PREV";
        if (course.getActualEstimate() == -1)
            OTHER = "NEXT/PREV";
        String[] labels = {"CURRENT", "MAJOR", "DAILY", OTHER, "EXAM", "SEMESTER"};
        Grade[] grades = {course.getCurrent(true),
            course.getMajor(),
            course.getDaily(),
            course.getCurrent(false),
            course.getExam(),
            course.getSem()};
        for (int i = 0; i < getButtons().length; i++) {
            Button button = getButtons()[i];
            final int I = i;
            button.setIcon((Graphics g, int x, int y, int width, int height) -> {
                int style = I == 0 ? Font.BOLD : Font.PLAIN;
                Color textColor = grades[I].getModStatus() == Grade.REACTING
                        ? Grade.getColor(Grade.RESPONDING, settings) : Color.WHITE;

                g.setFont(g.getFont().deriveFont(style, 13f));
                WindowUtil.drawCenteredString(g, labels[I],
                        x, y, width, height / 2);

                g.setFont(g.getFont().deriveFont(Font.BOLD, 16));
                g.setColor(textColor);
                WindowUtil.drawCenteredString(g, grades[I].getFormattedString(),
                        x, y + height / 2, width, height / 2);
            });
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(DrawingPanel.BACKGROUND);
        g.fillRect(getX(), getY(), getW(), getH());
        g.setColor(Color.WHITE);
        super.draw(g);
    }

    @Override
    public void setDimensions(final int x, final int y, final int w, final int h) {
        super.setDimensions(x, y, w, h);
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
            getButtons()[i].setRect(rects[i]);
        }
    }

}
