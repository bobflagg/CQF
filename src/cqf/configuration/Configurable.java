/*
 * Configurable.java
 *
 * Created on June 28, 2005, 10:32 AM
 *
 */

package cqf.configuration;

import java.util.Properties;

/**
 *
 * Interface defining methods of a configurable classes.
 *
 * Patterns: Separated Interface
 *
 * @author Bob Flagg (bob@calcworks.net).
 */
public interface Configurable {
    
    /**
     * Configures this instance.
     *
     * @param   properties property map with configuration information. 
     *
     * @throws  ConfigurationException  if configuration fails for any reason.
     */
    public void configure(Properties properties) throws ConfigurationException;

    
}
