/*
 * DefaultUIConfigurator.java
 *
 * Created on November 29, 2004, 4:18 PM
 */

package cqf.ui;

import cqf.configuration.ConfigurationException;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Properties;
import javax.swing.*;

/**
 *
 * @author  oracle
 */
public class DefaultUIConfigurator implements UIConfigurator {
    private Properties cofigurationProperties;
    
    /** Creates a new instance of DefaultUIConfigurator */
    public DefaultUIConfigurator() {
    }
    
    /**
     * Configures this instance.
     *
     * @param   properties property map with configuration information. 
     *
     * @throws  ConfigurationException  if configuration fails for any reason.
     */
    public void configure(Properties properties) throws ConfigurationException {
        cofigurationProperties = properties;
    }
    public void configureComponent(Component component) {
    }
    
    public void configureUI() {
        try {
            //UIManager.setLookAndFeel( new MetalLookAndFeel() );
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            System.out.println("Can't change L&F: " + e);
        }
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(730, 560);
    }
    
}
