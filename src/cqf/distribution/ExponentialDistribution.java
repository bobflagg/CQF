package cqf.distribution;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class ExponentialDistribution extends Distribution {
    private double lambda;

    public ExponentialDistribution(double lambda) throws IllegalArgumentException {
        if (lambda <= 0.0) throw new IllegalArgumentException("Parameter for exponential distribution must be positive.");
        this.lambda = lambda;
    }

    public double cdf(double x) {
        return 1-Math.exp(-lambda*x);
    }

    public double inverseCdf(double u) throws IllegalArgumentException {
        return -Math.log(1-u)/lambda;
    }

}
