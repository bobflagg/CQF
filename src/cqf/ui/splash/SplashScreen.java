/*
 * InitializationObserver.java
 *
 * Created on October 18, 2004, 3:03 PM
 */

package cqf.ui.splash;

import cqf.configuration.Configurable;
import java.util.Observer;

/**
 *
 * @author  oracle
 */
public interface SplashScreen extends Configurable, Observer {
    public void start();
    public void stop();
}
