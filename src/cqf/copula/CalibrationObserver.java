package cqf.copula;

/**
 *
 * @author birksworks
 */
public interface CalibrationObserver {
    public void calibrationComplete(Copula copula);
    public void calibrationStarted();
}
