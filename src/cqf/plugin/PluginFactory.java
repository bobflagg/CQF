/*
 * PluginFactory.java
 *
 * Created on October 10, 2004, 9:21 AM
 */

package cqf.plugin;


import cqf.configuration.Configurable;
import java.util.Properties;

/**
 * A utility class to retrieve and configure implementations for requested 
 * plugin interfaces.
 *
 * Patterns: Plugin, Separated Interface
 */
public class PluginFactory {
    
    /** 
     * Instantiate, configure and return an implementation of a plugin interface. 
     * 
     * @param   pluginType  the interface which the plugin will implement.
     */
    public static Configurable getPlugin(Properties properties, Class pluginType) {
        String implementationName = properties.getProperty(pluginType.getName());
        if (implementationName == null) {
            throw new RuntimeException("Implementation not specified for " +
            pluginType.getName() + " in PluginFactory properties.");
        }
        try {
            Configurable plugin = (Configurable)Class.forName(implementationName).
            newInstance();
            plugin.configure(properties);
            return plugin;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PluginFactory unable to construct instance of " +
            pluginType.getName());
        }
    }
    
}
