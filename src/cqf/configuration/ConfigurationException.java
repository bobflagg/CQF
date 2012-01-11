/*
 * ConfigurationException.java
 *
 * Created on June 28, 2005, 10:34 AM
 *
 */

package cqf.configuration;

/**
 *  This is a runtime exception class, signaling that the configuration
 *  of an object or a process has somehow failed.
 *
 * @author Bob Flagg (bob@calcworks.net).
 */
public class ConfigurationException extends RuntimeException {
    
    
    // Constuctors
    public ConfigurationException(String message) {
        super(message);
    }
    
    
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
