package cqf.core;

/**
 *
 * @author birksworks
 */
public interface PricingObserver {
    public void pricingComplete(double price);
    public void pricingStarted();
    public void dataPointAdded(int step, double average, double value);
}
