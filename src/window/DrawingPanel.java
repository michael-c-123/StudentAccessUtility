
package window;

import button.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
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
    private Sidebar sidebar;
    private boolean sidebarOn;
    private Button sidebarButton;
    private static final Button.Icon LEFT = (g, x, y, w, h) -> {
        g.setColor(Color.GRAY);
        int[] xs = {x + w / 8, x + w / 8, x + w * 7 / 8};
        int[] ys = {y + h / 8, y + h * 7 / 8, y + h / 2};
        g.fillPolygon(xs, ys, 3);
    };
    private static final Button.Icon RIGHT = (g, x, y, w, h) -> {
        g.setColor(Color.GRAY);
        int[] xs = {x + w * 7 / 8, x + w * 7 / 8, x + w / 8};
        int[] ys = {y + h / 8, y + h * 7 / 8, y + h / 2};
        g.fillPolygon(xs, ys, 3);
    };
    public static final Color BACKGROUND = new Color(20, 20, 20);

    public DrawingPanel(Profile profile) {
        this.profile = profile;
        setLayout(null);
        setBackground(BACKGROUND);
        sidebar = new Sidebar();
        sidebarButton = new Button(this,
                new Rectangle(0, 0, CourseTab.TITLE_HEIGHT, CourseTab.TITLE_HEIGHT),
                "", (Color) profile.getSettings().get("color dark"), Button.STANDARD);
        sidebarButton.setIcon(LEFT);

        for (Course course : profile.getCourses()) {
            addCourse(course);
            System.out.println(course.getMajorSplit());
        }

        initListeners();
    }

    //constructor helper method
    private void initListeners() {
        //pressing space operates sidebar
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyChar() == ' ')
                    sidebarButton.doClick(true);
                else if (ke.getKeyCode() == KeyEvent.VK_PAGE_DOWN
                        || ke.getKeyCode() == KeyEvent.VK_DOWN && ke.isControlDown()) {
                    int next = sidebar.getSelectionIndex() + 1;
                    if (next < sidebar.getButtonList().size())
                        sidebar.getButtonList().get(next).doClick();
                }
                else if (ke.getKeyCode() == KeyEvent.VK_PAGE_UP
                        || ke.getKeyCode() == KeyEvent.VK_UP && ke.isControlDown()) {
                    int prev = sidebar.getSelectionIndex() - 1;
                    if (prev >= 0)
                        sidebar.getButtonList().get(prev).doClick();
                }
                else if (currentPage instanceof CourseTab) {
                    CourseTab tab = (CourseTab) currentPage;
                    if (ke.isControlDown()) {
                        if (ke.getKeyCode() == KeyEvent.VK_N)
                            tab.getAddButton().doClick();
                        else if (ke.getKeyCode() == KeyEvent.VK_I)
                            tab.getInfoButton().doClick();
                    }
                    else {
                        int scrollBy;
                        switch (ke.getKeyCode()) {
                            case KeyEvent.VK_UP: scrollBy = -(int) profile.getSettings().get("grade bar size");
                                break;
                            case KeyEvent.VK_DOWN: scrollBy = (int) profile.getSettings().get("grade bar size");
                                break;
                            case KeyEvent.VK_HOME: scrollBy = Integer.MIN_VALUE/2;
                                break;
                            case KeyEvent.VK_END: scrollBy = Integer.MAX_VALUE/2;
                                break;
                            default: return;
                        }
                        tab.scroll(scrollBy);
                        repaint();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                if (ke.getKeyChar() == ' ')
                    sidebarButton.doClick(false);
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
        sidebarButton.addActionListener(event -> {
            switchSidebar();
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

        if (sidebarOn) {
            sidebarButton.setX((int) (Sidebar.SIZE_RATIO * getWidth()) + 1);
            sidebarButton.setIcon(RIGHT);
        }
        else {
            sidebarButton.setX(0);
            sidebarButton.setIcon(LEFT);
        }
        sidebarButton.draw(g);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        currentPage.scroll(
                mwe.getWheelRotation() * (int) profile.getSettings().get("scroll sensitivity"));

        repaint();
    }

}
