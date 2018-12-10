
package window;

import button.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import main.WindowUtil;
import student.Course;
import student.EntryGrade;
import student.Grade;

/**
 * @author Michael
 */
public final class CourseTab extends Button implements Drawable {
    private static final int TITLE_HEIGHT = 50; //how much space the title heading takes up
    private final Bar labelBar;
    private final Bar endBar;
    private ArrayList<Bar> gradeBarList;

    private Button addButton;

    private Course course;
    private Map<String, Object> settings;

    public CourseTab(Course course, Map<String, Object> settings, JPanel panel, //panel is always the drawing panel
            Rectangle rect, String text, Color color, ButtonPlan plan) {
        super(panel, rect, text, color, plan);
        gradeBarList = new ArrayList<>();
        this.course = course;
        this.settings = settings;

        final int SIZE = (int) settings.get("grade bar size");
        labelBar = new Bar(new String[]{"", "Date", "Name", "Wt", "Type", "Grade"},
                panel, settings);
        labelBar.setDimensions(0, TITLE_HEIGHT, 0, SIZE); //0s are placeholders, will be replaced by drawUsing()
        labelBar.setEnabled(false);

        endBar = new Bar(new String[]{"", "", "", "", "", ""}, panel, settings);
        endBar.setDimensions(0, 0, 0, SIZE * 2 + 1);
        for (Button button : endBar.getButtons()) {
            button.setColor((Color) settings.get("color dark"));
        }
        initEndBarListeners();

        initAddButton();

        for (EntryGrade entryGrade : course.getGradeList()) {
            addGrade(entryGrade);
        }
        course.update();
        updateBars(true);
    }

    private void initAddButton() {
        addButton = new Button(panel,
                new Rectangle(0, 0, TITLE_HEIGHT, TITLE_HEIGHT),
                "", (Color) settings.get("color dark"),
                (Button.ButtonPlan) settings.get("style"));
        addButton.addActionListener(event -> {
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
                    null, options, null); //icon, options, initial value

            if (option == 0) { //OK button clicked
                int modStatus = manipButton.isSelected() ? Grade.MANIPULATED : Grade.RESPONDING;
                double grade = manipButton.isSelected() ? (Double) gradeSpinner.getValue() : Grade.NO_VALUE;
                course.addGrade(new EntryGrade(modStatus,
                        nameField.getText().trim(), majorButton.isSelected(),
                        (Double) weightSpinner.getValue(), grade));
                course.update();
                updateBars(true);
            }
        });
        addButton.setEnabled(false);

        addButton.setIcon((g, x, y, w, h) -> {
            g.setColor(Color.WHITE);
            double weight = .05;
            g.fillRect((int) (x + w * .2), (int) (y + h * (.5 - weight / 2)),
                    (int) (w * .6), (int) (h * weight));
            g.fillRect((int) (x + w * (.5 - weight / 2)), (int) (y + h * .2),
                    (int) (w * weight), (int) (h * .6));
        });
    }

    private void initEndBarListeners() {
        endBar.getButtons()[0].addActionListener(event -> {
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
        endBar.getButtons()[1].setEnabled(false);
        endBar.getButtons()[2].setEnabled(false);
        if (course.isOnM1())
            endBar.getButtons()[3].addActionListener(event -> showChangeWindow(course.getCurrent(false), "Control Next Quarter Grade"));
        else
            endBar.getButtons()[3].setEnabled(false);
        endBar.getButtons()[4].addActionListener(event -> showChangeWindow(course.getExam(), "Control Exam Grade"));
        endBar.getButtons()[5].addActionListener(event -> showChangeWindow(course.getSem(), "Control Semester Grade"));
    }

    private void showChangeWindow(Grade grade, String title) {
        double start = grade.isEmpty() ? 100 : grade.getValue();
        double input = WindowUtil.showSpinnerDialog(title,
                start, 0, 120, .1, "0.0", true);
        if (input >= 0)
            grade.manipulate(input);
        else if (input == WindowUtil.RESET)
            grade.reset();
        course.update();
        updateBars(true);
    }

    //change colors, disable/enable
    private void updateBars(boolean lockResponder) {
        boolean doPositions = false;
        endBar.getButtons()[0].setColor(Grade.getColorDark(
                course.getCurrent(true).getModStatus(), settings)); //update color
        endBar.getButtons()[3].setColor(Grade.getColorDark(
                course.getCurrent(false).getModStatus(), settings));
        endBar.getButtons()[4].setColor(Grade.getColorDark(
                course.getExam().getModStatus(), settings));
        endBar.getButtons()[5].setColor(Grade.getColorDark(
                course.getSem().getModStatus(), settings));

        //put last grade in
        if (course.getGradeList().size() > gradeBarList.size()) {
            EntryGrade end = course.getGradeList().get(course.getGradeList().size() - 1);
            addGrade(end);
            doPositions = true;
        }

        //go thru grade list to find grade that is marked for deletion
        for (int i = 0; i < course.getGradeList().size(); i++) {
            EntryGrade grade = course.getGradeList().get(i);
            if (grade.isCustom() && grade.isGrayed()) {
                course.getGradeList().remove(i);
                Bar removed = gradeBarList.remove(i);
                for (Button button : removed.getButtons()) {
                    button.kill();
                }
                doPositions = true;
                break;
            }
        }

        if (lockResponder) {
            Bar soleResponder = null;
            boolean foundSoleResponder = false;
            for (Bar bar : gradeBarList) {
                bar.updateText();
                if (bar.getGrade().getModStatus() == Grade.RESPONDING
                        && !bar.getGrade().isEmpty()) {
                    if (foundSoleResponder) {
                        soleResponder = null;
                    }
                    else {
                        soleResponder = bar;
                        foundSoleResponder = true;
                    }
                }
            }
            if (soleResponder != null) {
                soleResponder.setPreventDelete(true);
                System.out.println("locked");
            }
            else {
                for (Bar bar : gradeBarList)
                    bar.setPreventDelete(false);
                System.out.println("unlocked");
            }
        }

        //update positions
        if (doPositions) {
            final int SIZE = (int) settings.get("grade bar size");
            int vertPosition = TITLE_HEIGHT; //room for title bar
            vertPosition += SIZE + 1; //room for the label bar

            for (Bar bar : gradeBarList) {
                bar.setInitialScroll(vertPosition);
                vertPosition += SIZE + 1; //+1 for small gaps b/t bars
            }
        }
    }

    @Override
    public void setActive(boolean active) {
        for (Bar bar : gradeBarList)
            for (Button button : bar.getButtons())
                button.setVisible(active);
        for (Button button : endBar.getButtons()) {
            button.setVisible(active);
        }
        addButton.setEnabled(active);
    }

    @Override
    public void drawUsing(Graphics g, DrawingPanel panel, boolean sidebarOn) {
        //FIXME refractor to separate into helper method updateText() (which updates position, etc) and drawing stuff only in this method
        int x, w;
        if (sidebarOn) {
            x = panel.getWidth() / 4 + 1;
            w = panel.getWidth() * 3 / 4;
        }
        else {
            x = 0;
            w = panel.getWidth();
        }

        final int SIZE = (int) settings.get("grade bar size");
        int END_Y = panel.getHeight() - SIZE * 2; //marks where the end bar is

        //TOODLE make buttons alwaays clickable as long as a part of it is peeking out
        //updates button positions, enable/disable as necessary
        for (Bar bar : gradeBarList) {
            bar.setDimensions(x, bar.getInitialScroll() - scrollPosition, w, bar.getH());
            if (TITLE_HEIGHT + SIZE - bar.getY() > 0 //disable bars that are covered by the title and label
                    || END_Y - SIZE - bar.getY() < 0) //disable bars that are covered by end bar
                bar.setEnabled(false);
            else
                bar.setEnabled(true);
        }
        labelBar.setX(x);
        labelBar.setW(w);

        addButton.setX(x);

        for (Bar bar : gradeBarList)
            bar.draw(g);

        labelBar.draw(g);

        drawEndBar(g, x, END_Y, w, SIZE);

        //draw title bar
        g.setColor(DrawingPanel.BACKGROUND);
        g.fillRect(x, 0, w, TITLE_HEIGHT);
        g.setFont(new Font(g.getFont().getName(), Font.BOLD, 36));
        g.setColor(Color.WHITE);
        WindowUtil.drawCenteredString(g, course.getName(), x, 0, w, TITLE_HEIGHT);

        addButton.draw(g);

        //TODO draw other stuff here
    }

    private void drawEndBar(Graphics g, int x, int END_Y, int w, int SIZE) {
        Rectangle[] rects = endBar.setDimensionsForEndBar(x, END_Y + 1, w, endBar.getH());
        Rectangle[] labelRects = new Rectangle[6];
        Rectangle[] fieldRects = new Rectangle[6];
        for (int i = 0; i < rects.length; i++) {
            Rectangle r = rects[i];
            labelRects[i] = new Rectangle(r.x, r.y, r.width, r.height / 2);
            fieldRects[i] = new Rectangle(r.x, r.y + SIZE, r.width, r.height / 2);
        }

        g.setColor(DrawingPanel.BACKGROUND);
        g.fillRect(x, END_Y, w, SIZE * 2 + 1);
        g.setColor(Color.WHITE);

        endBar.draw(g);

        WindowUtil.drawCenteredString(g, "MAJOR", labelRects[1]);
        WindowUtil.drawCenteredString(g, "DAILY", labelRects[2]);
        String desc = course.isOnM1() ? "NEXT" : "PREV";
        WindowUtil.drawCenteredString(g, desc + " QUARTER", labelRects[3]);
        WindowUtil.drawCenteredString(g, "EXAM", labelRects[4]);
        WindowUtil.drawCenteredString(g, "SEMESTER", labelRects[5]);

        Font orig = g.getFont();
        g.setFont(new Font(orig.getFontName(), Font.BOLD, orig.getSize()));
        WindowUtil.drawCenteredString(g, "CURRENT", labelRects[0]);
        g.setFont(new Font(orig.getFontName(), Font.BOLD, 16));

        if (course.getCurrent(true).getModStatus() == Grade.REACTING)
            g.setColor(Grade.getColor(Grade.RESPONDING, settings));
        WindowUtil.drawCenteredString(g, course.getCurrent(true).getFormattedString(), fieldRects[0]);
        g.setColor(Color.WHITE);

        if (course.getMajor().getModStatus() == Grade.REACTING)
            g.setColor(Grade.getColor(Grade.RESPONDING, settings));
        WindowUtil.drawCenteredString(g, course.getMajor().getFormattedString(), fieldRects[1]);
        g.setColor(Color.WHITE);

        if (course.getDaily().getModStatus() == Grade.REACTING)
            g.setColor(Grade.getColor(Grade.RESPONDING, settings));
        WindowUtil.drawCenteredString(g, course.getDaily().getFormattedString(), fieldRects[2]);
        g.setColor(Color.WHITE);

        WindowUtil.drawCenteredString(g, course.getCurrent(false).getFormattedString(), fieldRects[3]);
        WindowUtil.drawCenteredString(g, course.getExam().getFormattedString(), fieldRects[4]);
        WindowUtil.drawCenteredString(g, course.getSem().getFormattedString(), fieldRects[5]);
    }

    private int scrollPosition;

    @Override
    public void scroll(int amount) {
        scrollPosition += amount;
        if (scrollPosition < 0)
            scrollPosition = 0;
        else {
            final int SIZE = (int) settings.get("grade bar size");
            int endOfBars = gradeBarList.size() * (SIZE + 5); // TODO make this accurate and stop scrolling at the correct location
            endOfBars += TITLE_HEIGHT;
            if (scrollPosition + panel.getHeight() > endOfBars)
                scrollPosition = endOfBars - panel.getHeight();
            if (scrollPosition < 0)
                scrollPosition = 0;
        }
    }

    @Override
    public void scrollReset() {
        scrollPosition = 0;
    }

    private void addGrade(EntryGrade grade) {
        final int SIZE = (int) settings.get("grade bar size");

        //determine vertical position
        int vertPosition = (gradeBarList.size() + 1) * (SIZE + 1); // +1 to make small gaps b/t bars
        vertPosition += TITLE_HEIGHT; //room for title bar

        //initialize buttons, add to list
        Bar bar = new Bar(grade, panel, settings, vertPosition);
        bar.setDimensions(0, vertPosition, 0, SIZE); //0s are placeholders, will be replaced by drawUsing()
        gradeBarList.add(bar);

        //let buttons trigger course calculator to update, and then update bar text
        Button[] buttons = bar.getButtons();
        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            if (i != 1 && i != 2)
                buttons[i].addActionListener(event -> {
                    updateBars(index == 0);
                    course.update();
                });
        }
    }
}
