package cqf.bcd.pricing;

import cqf.copula.Copula;
import cqf.distribution.Distribution;
import cqf.interest.Discounter;
import java.util.Arrays;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class BCDPricer {
    protected static final int NO_SIMULATIONS = 3000;
    protected int noOfNames;
    protected int k; // the number of defaults required to force a default payment
    protected double delta;   // delta and m determine the tenor of the swap:
    protected int m;          //      delta, 2*delta, ..., m*delta
    protected double[] t;     // the tenor of the swap
    protected double recoveryRate;
    protected double s;   // fair spread of the contract
    protected Discounter discounter; // discount factors
    protected Copula copula;
    protected Distribution[] marginals;     // term structure of default probabilities
    // for all reference names of the BCDS

    public BCDPricer(
        int noOfNames,
        int k,
        double delta,
        int m,
        double recovery_rate,
        Discounter discounter,
        Copula copula,
        Distribution[] marginals
    ) {
        this(noOfNames, k, delta, m, recovery_rate, discounter, marginals);
        this.copula = copula;
    }

    public BCDPricer(
        int noOfNames,
        int k,
        double delta,
        int m,
        double recovery_rate,
        Discounter discounter,
        Distribution[] marginals
    ) {
        this.noOfNames = noOfNames;
        this.k = k;
        this.delta = delta;
        this.m = m;
        this.recoveryRate = recovery_rate;
        this.discounter = discounter;
        this.marginals = marginals;
        // build the tenor of the swap
        t = new double[m];
        for (int i = 0; i < m; i++) {
            t[i] = delta * (i+1);
        }
    }

    public double price(int noSimulations, PricingObserver observer) throws Exception {
        return price(noSimulations, true, observer);
    }

    public double price(int noSimulations, boolean includeAccruedPremium, PricingObserver observer) throws Exception {
        double sum = 0.0, plSum = 0.0, dlSum = 0.0, apSum = 0.0;
        double dl, pl, ap, fairSpread;
        double defaulTime = 0.0;
        double[] uniformVariates = new double[noOfNames];
        for (int i = 0; i < noSimulations; i++) {
            uniformVariates = copula.simulate();
            defaulTime = computeDefaulTime(uniformVariates);
            dl = defaultLegFactor(defaulTime);
            pl = premiumLegFactor(defaulTime);
            if (includeAccruedPremium) {
                ap = accruedPremiumFactor(defaulTime);
            } else {
                if (pl == 0.0) {    // to avoid division by 0 in case default occurs
                                    // in first period and accrued premium is not included
                    ap = 0.5*delta*discounter.discountFactor(t[0]);
                } else {
                    ap = 0.0;
                }
            }
            plSum += pl;
            dlSum += dl;
            apSum += ap;
            fairSpread = dl/(pl+ap);
            observer.dataPointAdded(i, dlSum/(plSum+apSum), pl+ap, dl);
        }
        return dlSum/(plSum+apSum);
    }

    public double price(PricingObserver observer) throws Exception {
        return price(NO_SIMULATIONS, observer);
    }

    private double computeDefaulTime(double[] variates) {
        double[] defaultTimes = new double[noOfNames];
        for (int i = 0; i < noOfNames; i++) {
            defaultTimes[i] = marginals[i].inverseCdf(variates[i]);
        }
        Arrays.sort(defaultTimes);
        return defaultTimes[k - 1];
    }

    private double premiumLegFactor(double defaulTime) {
        double value = 0.0, time = 0.0;
        for (int i = 0; i < m; i++) {
            if (defaulTime <= t[i]) break;  // default occurred in ith period
            value += delta*discounter.discountFactor(t[i]);
        }
        return value;
    }

    private double defaultLegFactor(double defaulTime) {
        for (int i = 0; i < m; i++) {
            if (defaulTime <= t[i]) { // default occurred in ith period
                return (1 - recoveryRate) * discounter.discountFactor(t[i]);
            }
        }
        return 0.0;
    }

    private double accruedPremiumFactor(double defaulTime) {
        for (int i = 0; i < m; i++) {
            if (defaulTime <= t[i]) { // default occurred in ith period
                return 0.5*delta*discounter.discountFactor(t[i]);
            }
        }
        return 0.0;
    }
}
