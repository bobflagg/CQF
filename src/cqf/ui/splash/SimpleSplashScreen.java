/*
 * SplashScreen.java
 *
 * Created on October 11, 2004, 4:58 PM
 */

package cqf.ui.splash;

import cqf.configuration.ConfigurationException;
import cqf.ui.status.StatusBar;
import cqf.ui.util.GUIUtilities;
import java.awt.*;
import java.util.Observable;
import java.util.Properties;

import javax.swing.*;

/**
 * Simple implementation of the <code>SplashScreen</code> interface, displaying
 * a application startup image in a window and using a <code>StatusBar</code>
 * to display startup messages.
 *
 * @author  Bob Flagg <bflagg@sightsoftware.com>
 */
public class SimpleSplashScreen extends JWindow implements SplashScreen {
    // Constants
    private static final String WORKING_IMAGE_KEY = "splash-screen-working-image";
    private static final String WARNING_IMAGE_KEY = "splash-screen-warning-image";
    private static final String IDLE_IMAGE_KEY = "splash-screen-idle-image";
    private static final String BACKGROUND_KEY = "splash-screen-background";
    private static final String BACKGROUND_COLOR_KEY = "splash-screen-background-color";
    private static final String STATUS_BACKGROUND_COLOR_KEY = "splash-screen-status-panel-background-color";
    private static final String STATUS_FOREGROUND_COLOR_KEY = "splash-screen-status-panel-foreground-color";


    private Properties properties;
    private StatusBar statusBar;
    
    public SimpleSplashScreen(){
        setVisible(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stop();
            }
        });
    }    
    
    /**
     * Configures this SplashScreen.
     *
     * @param   properties property map with configuration information. 
     *
     * @throws  ConfigurationException  if configuration fails for any reason.
     */
    public void configure(Properties properties) throws ConfigurationException {
        this.properties = properties;
    }
    
    
    public void start() {
        // create and add main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE,1));        
        mainPanel.setBackground(
            new Color(
                Integer.parseInt(properties.getProperty(BACKGROUND_COLOR_KEY), 16)
            )
        );
        
        // build and add splash screen image
        String backgroundImagePath = properties.getProperty(BACKGROUND_KEY);
        System.out.println(backgroundImagePath);
        ImageIcon backgroundImage = GUIUtilities.readImageIcon(backgroundImagePath);
        JLabel imageLabel = new JLabel(backgroundImage);
        mainPanel.add(imageLabel, BorderLayout.CENTER);
        
        // build, add and configure status bar
        buildStatusBar(backgroundImage.getIconWidth());
        //mainPanel.add(statusBar, BorderLayout.SOUTH);
        statusBar.setBackground(
            new Color(
                Integer.parseInt(properties.getProperty(STATUS_BACKGROUND_COLOR_KEY), 16)
            )
        );
        statusBar.setTextColor(
            new Color(
                Integer.parseInt(properties.getProperty(STATUS_FOREGROUND_COLOR_KEY), 16)
            )
        );

        // display
        pack();
        GUIUtilities.locateOnScreen(this);
        setVisible(true);
        toFront();        
    }
    
    /**
     * Builds and returns the status pane.
     */
    private void buildStatusBar(int width) {
        String workingImagePath = properties.getProperty(WORKING_IMAGE_KEY);
        ImageIcon workingImage = GUIUtilities.readImageIcon(workingImagePath);
        String warningImagePath = properties.getProperty(WARNING_IMAGE_KEY);
        ImageIcon warningImage = GUIUtilities.readImageIcon(workingImagePath);
        String idleImagePath = properties.getProperty(IDLE_IMAGE_KEY);
        ImageIcon idleImage = GUIUtilities.readImageIcon(idleImagePath);
        statusBar = new StatusBar(workingImage, warningImage, idleImage, width);        
    }

        
    public void update(Observable observable, Object arg) {
        statusBar.update(observable, arg);
    }
   
    public void stop(){
        try{           
            SwingUtilities.invokeLater(new CloseSplashScreen());
        } catch(Exception e){e.printStackTrace();}
    }
    
    class CloseSplashScreen implements Runnable {
        public void run() {
            setVisible(false);
            dispose();
        }
    }   
    
}
 

