/*
 * AppController.java
 *
 * Created on October 12, 2004, 8:10 AM
 */

package cqf.coordination;

import cqf.configuration.ConfigurationException;
import java.util.Properties;

/**
 *
 * @author  oracle
 */
public abstract class AbstractCoordinator implements Coordinator {
    protected Properties properties;
    
    public void configure(Properties properties) throws ConfigurationException {
        this.properties = properties;
        
    }
    
    public void exit() {
        System.exit(0);
    }

    /**
     * Getter for property properties.
     * @return Value of property properties.
     */
    public String getProperty(String key) {
        return (String)properties.get(key);
    }

}
