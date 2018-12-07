
package window;

import button.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;
import student.*;

/**
 * @author Michael
 */
public class DrawingPanel extends JPanel implements MouseWheelListener {

    private Profile profile;

    private Drawable currentPage;
    private Sidebar sidebar; //TODO finish sidebar
    private boolean sidebarOn;
    public static final Color BACKGROUND = new Color(20,20,20);

    public DrawingPanel(Profile profile) {
        this.profile = profile;
        setLayout(null);
        setBackground(BACKGROUND);
        sidebar = new Sidebar();

        //TEST
//        profile.setToDefaultSettings();
//        Course courseA = new Course("AP Calculus BC", 0);
//        courseA.addGrade(new EntryGrade(
//                new GregorianCalendar(2018, 3, 14), "Assignment", false, 1.0, 99));
//        for (int i = 0; i < 5; i++) {
//            courseA.addGrade(new EntryGrade(
//                     new GregorianCalendar(2018, 3, 54), "Assignment" + i, false, 1.0, 89+i));
//        }
////        courseA.addGrade(new EntryGrade(
////                new GregorianCalendar(2018, 2, 29), "Test on Subject 3.2", true, 1.0, 85));
//        courseA.addGrade(new EntryGrade(
//                Grade.RESPONDING, "Theoretical Project", true, 1.0, 99));
//        courseA.setOnM1(false);
//        courseA.getM1().setValue(90);
//
//        Course courseB = new Course("AP English", 0);
//        courseB.addGrade(new EntryGrade(new GregorianCalendar(2018, 3, 14), "Paper", false, 1.0, 99));
//        courseB.addGrade(new EntryGrade(Grade.MANIPULATED, "Timed Writing", true, 1.0, 99));
////        courseB.addGrade(new EntryGrade(Grade.NORMAL, new GregorianCalendar(2018, 3, 54), "Thing", true, 1.0, 99));
//
//        addCourse(courseA);
//        addCourse(courseB);

        for (Course course : profile.getCourses()) {
            addCourse(course);
        }

        //endTEST
        initListeners();
    }

    //constructor helper method
    private void initListeners() {
        //pressing space operates sidebar
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == ' ')
                    switchSidebar();
            }
        });
        addMouseWheelListener(this);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ce) {
                currentPage.scroll(0);
                repaint();
            }
        });
    }

    //called when profile's courses are read to be displayed
    private void addCourse(Course course) {
        CourseTab tab = new CourseTab(course, profile.getSettings(), this,
                new Rectangle(), course.getName(), new Color(100, 100, 100), Button.STANDARD);
        tab.addActionListener((ActionEvent ae) -> {
            changePage(tab);
        });

        sidebar.addButton(tab);

        if (sidebar.getButtonList().size() == 1)
            sidebar.getButtonList().get(0).doClick();
        else
            tab.setActive(false);

        //TODO find the m1 grade if currently on m2 (do this before this method is called)
    }

    private void changePage(Drawable page) {
        if (currentPage != null)
            currentPage.setActive(false);
        currentPage = page;
        currentPage.setActive(true);
        repaint();
    }

    public void switchSidebar() {
        sidebarOn = !sidebarOn;
        sidebar.setEnabled(sidebarOn);
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        sidebar.drawUsing(g, this, sidebarOn);

        if (currentPage != null)
            currentPage.drawUsing(g, this, sidebarOn);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        currentPage.scroll(
                mwe.getWheelRotation() * (int) profile.getSettings().get("scroll sensitivity"));

        repaint();
    }


}
