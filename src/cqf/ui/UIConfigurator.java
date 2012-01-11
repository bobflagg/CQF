/*
 * Skin.java
 *
 * Created on October 14, 2004, 4:45 PM
 */

package cqf.ui;

import cqf.configuration.Configurable;
import java.awt.Dimension;
import java.awt.Component;

/**
 *
 * @author  oracle
 */
public interface UIConfigurator extends Configurable {

    /**
     * Configures the user interface; requests Swing settings and 
     * jGoodies Looks options from the launcher.
     */
    public void configureUI();

    public void configureComponent(Component component);

    public Dimension getPreferredSize();

}
