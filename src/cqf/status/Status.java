package cqf.status;

import java.util.Observable;

/**
 * Observable holder of status information.
 *
 * Patterns: Observer
 * Stereotypes: Information Holder
 *
 * @author  oracle
 */
public class Status extends Observable {
    protected boolean working = false;
    protected boolean warning = false;
    protected String message = "";
    protected String shortMessage = "Ready";
    
    /** Creates a new instance of InitializationState */
    public Status() {}
        
    /**
     * Set the status information.
     *
     * @param working   indication of whether the app is busy or idle.
     * @param message   the status message.
     */
    public void updateStatus(boolean working, boolean warning, String message) {
        this.working = working;
        this.warning = warning;
        this.message = message;
        setChanged();
        notifyObservers();
    }
    
    /**
     * Getter for property working.
     * @return Value of property working.
     */
    public boolean isWorking() {
        return working;
    }
    
    /**
     * Getter for property message.
     * @return Value of property message.
     */
    public String getMessage() {
        return message;
    }

    public boolean isWarning() {
        return warning;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public void setShortMessage(String shortMessage) {
        this.shortMessage = shortMessage;
    }
    
}
