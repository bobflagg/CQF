/*
 * ActionFactory.java
 *
 * Created on October 26, 2004, 9:47 AM
 */

package cqf.ui.action;


import javax.swing.AbstractAction;
//import java.awt.event.WindowListener;

/**
 *
 * @author  oracle
 */
public interface ActionFactory {
    public AbstractAction getAction(String actionName);    
    //public WindowListener getWindowListener();    
}
