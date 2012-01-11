/*
 * Controller.java
 *
 * Created on October 7, 2004, 10:17 AM
 */

package cqf.coordination;

import javax.swing.AbstractAction;

import cqf.configuration.Configurable;
import cqf.ui.UIConfigurator;

/**
 *
 * @author  oracle
 */
//public interface Presentable extends Configurable, ActionFactory {    
public interface Coordinator extends Configurable {    
    
    public void initialize(UIConfigurator configurator);
    
    public void exit();

    /**
     * Getter for property properties.
     * @return Value of property properties.
     */
    public String getProperty(String key);
    
    
    public AbstractAction getAction(String actionName);

}
