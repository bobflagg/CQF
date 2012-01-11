/*
 * StatusBar.java
 *
 * Created on October 6, 2004, 2:12 PM
 */

package cqf.ui.status;

import cqf.status.Status;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

/**
 * A simple swing component to display status information.
 *
 * Patterns: Observer
 *
 * @author  oracle
 */
public class StatusBar extends JPanel implements Observer {
    
    private static final int PADDING = 4;
    private JLabel statusLabel;
    private static ImageIcon workingIcon;
    private static ImageIcon warningIcon;
    private static ImageIcon alertIcon;
    private static ImageIcon idleIcon;
    
    /** 
     * Creates a new instance of StatusBar 
     *
     * @param   workingIcon the icon to display if application is busy.   
     * @param   warningIcon the icon to display if application is idle but 
     *                      last action gave rise to a warning.   
     * @param   alertIcon   the icon to display if application is idle but has
     *                      not empty last action message.   
     * @param   width   preferred width of this component.   
     */
    public StatusBar(ImageIcon workingIcon, ImageIcon warningIcon, ImageIcon alertIcon, int width) {
        super(new GridLayout(0, 1));
        this.workingIcon = workingIcon;
        this.warningIcon = warningIcon;
        this.alertIcon = alertIcon;
        idleIcon = alertIcon;
        statusLabel = new JLabel("", idleIcon, JLabel.LEFT);
        statusLabel.setPreferredSize(new Dimension(width - 8, workingIcon.getIconHeight()));
        add(statusLabel);
        setPreferredSize(new Dimension(width, workingIcon.getIconHeight() + 2 * PADDING));
    }
    
    /** 
     * Creates a new instance of StatusBar 
     *
     * @param   workingIcon the icon to display if application is busy.   
     * @param   warningIcon the icon to display if application is idle but 
     *                      last action gave rise to a warning.   
     * @param   alertIcon   the icon to display if application is idle but has
     *                      not empty last action message.   
     * @param   idleIcon    the icon to display if application is idle and the
     *                      last action did not request a message post.   
     * @param   width       preferred width of this component.   
     */
    public StatusBar(ImageIcon workingIcon, ImageIcon warningIcon, ImageIcon alertIcon, ImageIcon idleIcon, int width) {
        this(workingIcon, warningIcon, alertIcon, width);
        this.idleIcon = idleIcon;
        statusLabel.setIcon(idleIcon);
    }

    /** 
     * Set the color of text displayed in the status bar.
     *
     * @param   textColor the color.   
     */
    public void setTextColor(Color textColor) {
        statusLabel.setForeground(textColor);
    }
            
    /** 
     * Method to implement the <code>Observer</code> interface, which is called 
     * whenever the observed object is changed. 
     *
     * @param   observable  the observable object.
     * @param   arg an argument passed to the notifyObservers method.    
     */
    public void update(Observable observable, Object arg) {
        Status status = (Status)observable;
        try{
            SwingUtilities.invokeLater(new UpdateStatus(status.isWorking(), status.isWarning(), status.getMessage()));
        } catch(Exception e){e.printStackTrace();}        
    }

    /** 
     * Display given status informaton. 
     *
     * @param   working  indication of whether app is busy or idle.
     * @param   message the status message.    
     */
    private void showStatusMessage(boolean working, boolean warning, String message) {
        statusLabel.setFont(new Font("Serif", Font.PLAIN, 12));
        statusLabel.setForeground(Color.BLACK);

        if (working) statusLabel.setIcon(workingIcon);
        else if (warning) {
            statusLabel.setIcon(warningIcon);
            statusLabel.setFont(new Font("Serif", Font.BOLD, 12));
            statusLabel.setForeground(Color.red);
        }
        else if (message.length() > 0) statusLabel.setIcon(alertIcon);        
        else statusLabel.setIcon(idleIcon);
        statusLabel.setText(message);
    }
    
    /** 
     * Runnable inner class to support updating status in Event Dispatching Thread. 
     */
    class UpdateStatus implements Runnable {
        boolean _working;
        boolean _warning;
        String _message;
        
        public UpdateStatus(boolean working, boolean warning, String message) {
            _working = working;
            _warning = warning;
            _message = message;
        }
        
        public void run() {
            showStatusMessage(_working, _warning, _message);
        }
    }
    
}
