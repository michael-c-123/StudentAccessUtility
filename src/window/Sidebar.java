
package window;

import button.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael
 */
public class Sidebar implements Drawable {
    private List<Button> buttonList;
    public static final int SIDEBAR_BUTTON_SIZE = 30;

    public Sidebar(){
        buttonList = new ArrayList<>();
    }

    @Override
    public void drawUsing(Graphics g, DrawingPanel panel, boolean sidebarOn) {
        if (!sidebarOn)
            return;

        //update position
        int w = panel.getWidth() / 4;
        for (Button button : buttonList) {
            button.setEnabled(sidebarOn);
            button.setRect(new Rectangle(button.getRect().x, button.getRect().y, w, button.getRect().height));
        }

        //draw
        g.setColor(Color.DARK_GRAY.darker());
        g.fillRect(0, 0, panel.getWidth() / 4, panel.getHeight()); //one fourth of screen
        for (Button button : buttonList)
            button.draw(g);
    }

    public List<Button> getButtonList() {
        return buttonList;
    }

    public void addButton(Button button) {
        button.setRect(new Rectangle(0, buttonList.size() * (SIDEBAR_BUTTON_SIZE + 1), 0, SIDEBAR_BUTTON_SIZE)); //0 placeholder for width
        buttonList.add(button);
        buttonList = Button.groupButtons(buttonList);
    }

    public void setEnabled(boolean enabled) {
        for (Button button : buttonList) {
            button.setVisible(enabled);
        }
    }

    @Override
    public void scroll(int scrollAmount) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void scrollReset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setActive(boolean active) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
