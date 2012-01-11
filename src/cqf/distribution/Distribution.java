package cqf.distribution;

/**
 *
 * @author birksworks
 */
public abstract class Distribution {
    public abstract double cdf(double x);
    public double F(double t) {
        return cdf(t);
    }
    public double Q(double t) {
        return 1-cdf(t);
    }
    public abstract double inverseCdf(double u) throws IllegalArgumentException;
}
