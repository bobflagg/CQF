package cqf.bcd.pricing;

/**
 *
 * @author birksworks
 */
public interface PricingObserver {
    public void pricingComplete(double price);
    public void pricingStarted();
    public void dataPointAdded(int step, double approximation, double premiumLegValue, double defaultLegValue);
}
