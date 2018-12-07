/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package window;

import java.awt.Graphics;

/**
 *
 * @author Michael
 */
public interface Drawable {
    void drawUsing(Graphics g, DrawingPanel panel, boolean sidebarOn);
    void scroll(int scrollAmount);
    void scrollReset();
    void setActive(boolean active);
}
