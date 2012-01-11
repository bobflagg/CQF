/*
 * Launcher.java
 *
 * Created on October 7, 2004, 10:17 AM
 */

package cqf.ui.launch;

import cqf.coordination.Coordinator;
import cqf.plugin.PluginFactory;
import cqf.status.Status;
import cqf.ui.UIConfigurator;
import cqf.ui.splash.SplashScreen;
import java.util.Properties;

/**
 * Utility class to start up a swing application.
 *
 * <p>
 *      Loads a configuration properties file located at:
 *          /property/configuration.properties
 *      The configuration files will specify an application <code>Coordinator</code> 
 *      implementation and user interface configurator <code>UIConfigurator</code> 
 *      implementation, which will be instantiated.  The coordinator will be
 *      registered with a splash screen as a souce of start up messages and 
 *      receive a request for initialization.  The configurator's configureUI will 
 *      be called.
 * </p>
 *
 * @author  Bob Flagg <bflagg@sightsoftware.com>
 */
public class Launcher {
    private Properties configurationProperties = new Properties();
    
    private void launch() {
        Status status = null;
        SplashScreen splashScreen = null;
        String applicationName = "application";
        try {
            configurationProperties.load(getClass().getResourceAsStream("/property/configuration.properties"));
            applicationName = configurationProperties.getProperty("applicationName");
            // configure user interface
            UIConfigurator configurator = (UIConfigurator) PluginFactory.getPlugin(
            configurationProperties, UIConfigurator.class);
            configurator.configureUI();
            // set up splash screen
            status = new Status();
            splashScreen = (SplashScreen) PluginFactory.getPlugin(configurationProperties, SplashScreen.class);
            status.addObserver(splashScreen);
            splashScreen.start();
            status.updateStatus(true, false, "Starting application...");
            // initialize
            Coordinator coordinator = (Coordinator) PluginFactory.getPlugin(
                    configurationProperties, Coordinator.class);
            coordinator.initialize(configurator);
            // dispose of splash screen
            status.updateStatus(false, false, "Application started.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("****************************************************************");
            System.out.println("****************************************************************");
            System.out.println("Unable to launch " + applicationName + ".  Aborting...");
            System.out.println("****************************************************************");
            System.out.println("****************************************************************");
            System.exit(1);
        } finally {
            if (status != null) 
                if (splashScreen != null) status.deleteObserver(splashScreen);
            if (splashScreen != null) splashScreen.stop();                   
        }
    }
        
    
    public static void main(String args[]) {
        System.out.println("starting application....");
        Launcher launcher = new Launcher();
        launcher.launch();
    }
    
}
