
package window;

import button.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import main.InfoPanel;
import main.WindowUtil;
import student.Course;
import student.EntryGrade;
import student.Grade;

/**
 * @author Michael
 */
public final class CourseTab implements Drawable {
    public static final int TITLE_HEIGHT = 50; //how much space the title heading takes up
    private final Button tab;
    private final DrawingPanel panel;
    private final Bar labelBar;
    private final EndBar endBar;
    private ArrayList<Bar> gradeBarList;

    private Button addButton;
    private Button infoButton;

    private Course course;
    private Map<String, Object> settings;

    public CourseTab(Course course, Map<String, Object> settings, DrawingPanel panel,
            Rectangle rect, String text, Color color, Button.ButtonPlan plan) {
        this.panel = panel;
        tab = new Button(panel, rect, text, color, plan);
        gradeBarList = new ArrayList<>();
        this.course = course;
        this.settings = settings;

        final int SIZE = (int) settings.get("grade bar size");
        labelBar = new Bar(new String[]{"", "Date", "Name", "Wt", "Type", "Grade"},
                panel);
        labelBar.setDimensions(0, TITLE_HEIGHT, 0, SIZE); //0s are placeholders, will be replaced by drawUsing()
        labelBar.setEnabled(false);

        endBar = new EndBar(course, panel, settings);
        endBar.setDimensions(0, 0, 0, SIZE * 2 + 1);
        for (Button button : endBar.getButtons()) {
            button.setColor((Color) settings.get("color dark"));
            button.addActionListener(event -> {
                updateBars(true);
            });
        }

        initAddButton();
        initInfoButton();

        for (EntryGrade entryGrade : course.getGradeList()) {
            addGrade(entryGrade);
        }
        updateBars(true);
    }

    private void initAddButton() {
        addButton = new Button(panel,
                new Rectangle(0, 0, TITLE_HEIGHT * 2 / 3, TITLE_HEIGHT * 2 / 3),
                "+", new Font("Arial", Font.BOLD, 24), (Color) settings.get("color dark"),
                (Button.ButtonPlan) settings.get("style"));
        addButton.setFontScale(.5);
        addButton.addActionListener(event -> {
            EntryGrade createdGrade = WindowUtil.showCreateDialog();
            if (createdGrade != null) {
                course.addGrade(createdGrade);
                updateBars(createdGrade.getModStatus() == Grade.RESPONDING);
            }
        });
        addButton.setEnabled(false);
    }

    private void initInfoButton() {
        infoButton = new Button(panel,
                new Rectangle(0, 0, TITLE_HEIGHT * 2 / 3, TITLE_HEIGHT * 2 / 3),
                "i", new Font("Courier New", Font.BOLD, 24), (Color) settings.get("color dark"),
                (Button.ButtonPlan) settings.get("style")
        );
        infoButton.setFontScale(.5);
        infoButton.addActionListener(event -> {
            InfoPanel info = new InfoPanel(course);
            JButton[] options = WindowUtil.makeButtons("OK", "Cancel");
            int choice = JOptionPane.showOptionDialog(null, info, course.getName(),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);
            if (choice == 0) {
                course.setMajorSplit(info.getCustomSplit());
                updateBars(false);
            }
        });
        infoButton.setEnabled(false);
    }

    public Button getTab() {
        return tab;
    }

    public Button getAddButton() {
        return addButton;
    }

    public Button getInfoButton() {
        return infoButton;
    }

    //change colors, disable/enable
    private void updateBars(boolean lockResponder) {
        course.update();

        boolean doPositions = false;

        //update colors
        endBar.getButtons()[0].setColor(Grade.getColorDark(
                course.getCurrent(true).getModStatus(), settings));
        endBar.getButtons()[3].setColor(Grade.getColorDark(
                course.getCurrent(false).getModStatus(), settings));
        endBar.getButtons()[4].setColor(Grade.getColorDark(
                course.getExam().getModStatus(), settings));
        endBar.getButtons()[5].setColor(Grade.getColorDark(
                course.getSem().getModStatus(), settings));
        endBar.updateIcons();

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
                for (Button button : removed.getButtons())
                    button.kill();
                doPositions = true;
                course.update();
                break;
            }
        }

        //update texts of grade bars
        for (Bar bar : gradeBarList)
            bar.updateText();

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

        if (lockResponder) {
            Bar soleResponder = null;
            for (Bar bar : gradeBarList)
                if (bar.getGrade().getModStatus() == Grade.RESPONDING
                        && !bar.getGrade().isEmpty())
                    if (soleResponder == null)
                        soleResponder = bar;
                    else {
                        soleResponder = null;
                        break;
                    }
            if (soleResponder != null) //if there is only one responding grade, prevent it from being deleted
                soleResponder.setPreventDelete(true);
            else //otherwise return everything to normal
                for (Bar bar : gradeBarList)
                    bar.setPreventDelete(false);
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
        infoButton.setEnabled(active);
    }

    @Override
    public void drawUsing(Graphics g, DrawingPanel panel, boolean sidebarOn) {
        int x, w;
        if (sidebarOn) {
            x = (int) (panel.getWidth() * Sidebar.SIZE_RATIO) + 1;
            w = (int) (panel.getWidth() * (1 - Sidebar.SIZE_RATIO));
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

        addButton.setX(panel.getWidth() - addButton.getRect().width);
        infoButton.setX(panel.getWidth() - addButton.getRect().width * 2 - 1);

        for (Bar bar : gradeBarList)
            bar.draw(g);

        labelBar.draw(g);

        endBar.setDimensions(x, END_Y + 1, w, endBar.getH());
        endBar.draw(g);

        //draw title bar
        g.setColor(DrawingPanel.BACKGROUND);
        g.fillRect(x, 0, w, TITLE_HEIGHT);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 36));
        g.setColor(Color.WHITE);
        WindowUtil.drawCenteredString(g, course.getName(), x, 0, w, TITLE_HEIGHT);

        addButton.draw(g);
        infoButton.draw(g);
    }

    private int scrollPosition;

    @Override
    public void scroll(int amount) {
        scrollPosition += amount;
        if (scrollPosition < 0)
            scrollPosition = 0;
        else {
            final int SIZE = (int) settings.get("grade bar size");
            int endOfBars = gradeBarList.size() * (SIZE + 1);
            int visibleSegment = panel.getHeight();
            visibleSegment -= TITLE_HEIGHT + SIZE * 3;

            endOfBars -= visibleSegment;
            if (scrollPosition > endOfBars)
                scrollPosition = endOfBars;
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
                buttons[i].addActionListener(event -> updateBars(index == 0));
        }
    }
}
