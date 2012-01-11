/*
 * GUIUtilities.java
 *
 * Created on October 20, 2004, 8:53 AM
 */

package cqf.ui.util;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 *
 * @author  oracle
 */
public class GUIUtilities {    
    //public static final ImageIcon IDLE_PROGRESS_ICON = readImageIcon("image/idleProgress.gif");
    //public static final ImageIcon WORKING_PROGRESS_ICON = readImageIcon("image/workingProgress.gif");
    //public static final ImageIcon WORKING_PROGRESS_ICON = readImageIcon("image/workingProgress.gif");
    public static final Icon ALERT_ICON = readImageIcon("image/alert.gif");
    public static final Icon EMPTY_ICON = readImageIcon("image/idle.gif");
    public static final Icon LEAF_TREE_NODE_ICON = readImageIcon("image/leaf-select.png");
    public static final Icon LEAF_NO_SELECT_TREE_NODE_ICON = readImageIcon("image/leaf-no-select.png");
    public static final Icon NON_LEAF_TREE_NODE_ICON = readImageIcon("image/folder.png");
    public static final Icon NON_LEAF_EXPANDED_TREE_NODE_ICON = readImageIcon("image/folder-visiting.png");
    
    /**
     * Locates the given component with the given offsets from the screen's center.
     *
     * @param   component   the component to be located
     * @param   verticalOffset  a number between -1 and 1 indicating the vertical offset
     *                          as a percentage of half the difference of the screen width
     *                          and the component's width. 
     * @param   horizontalOffset    a number between -1 and 1 indicating the horizaontal offset
     *                              as a percentage of half the difference of the screen height
     *                              and the component's height. 
     */
    public static void locateOnScreen(Component component, double verticalOffset, double horizontalOffset) {
        Dimension paneSize = component.getSize();
        Dimension screenSize = component.getToolkit().getScreenSize();
        int width = (screenSize.width  - paneSize.width)  / 2;
        int height = (screenSize.height - paneSize.height) / 2;
        int x = (int) ((1 + horizontalOffset) * width);
        int y = (int) ((1 + verticalOffset) * height);
        //System.out.println(x + "; " + y);
        component.setLocation(x, y);
    }
    
    /**
     * Locates the given component on the screen's center.
     */
    public static void locateOnScreen(Component component) {
        locateOnScreen(component, 0, 0);
    }

        /*
     * Looks up and answers an icon for the specified filename suffix.<p>
     */
    public static ImageIcon readImageIcon(String path) {
        System.out.println(path);
        URL url = GUIUtilities.class.getClassLoader().getResource(path);
        System.out.println(url);
        return new ImageIcon(url);
    }
    
    /**
     * Creates and returns a <code>JButton</code> 
     * configured for use in a JToolBar.<p>
     * 
     * This is a simplified method that is overriden by the Looks Demo.
     * The full code uses the JGoodies UI framework's ToolBarButton
     * that better handles platform differences.
     */
    public static AbstractButton createButton(String iconName, String text) {
        JButton button = new JButton(text, GUIUtilities.readImageIcon("image/" + iconName));
        //JButton button = new JButton(text);
        //button.setBorderPainted(false);
        button.setFocusable(false);
        return button;
    }

}
