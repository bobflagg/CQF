package cqf.distribution;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class StepwiseHazardDistribution extends Distribution {

    protected int noOfPeriods;
    protected double[] tenor;
    protected double[] alpha;
    protected double[] upper;

    public StepwiseHazardDistribution(double[] tenor, double[] alpha) {
        this.noOfPeriods = tenor.length;
        this.tenor = tenor;
        this.alpha = alpha;
        upper = new double[noOfPeriods];
        upper[0] = alpha[0]*tenor[0];
        for (int i = 1; i < noOfPeriods; i++) {
            upper[i] = upper[i-1]+alpha[i] * (tenor[i] - tenor[i-1]);
        }
    }

    public double cdf(double x) {
        double t = 0.0;
        double y = 0.0;
        for (int i = 0; i < noOfPeriods; i++) {
            if (x <= tenor[i]) {
                return 1 - Math.exp(-(y+alpha[i] * (x - t)));
            }
            t = tenor[i];
            y = upper[i];
        }
        return 1 - Math.exp(-(y+alpha[noOfPeriods-1] * (x - t)));
    }

    public double inverseCdf(double u) throws IllegalArgumentException {
        if (u<0 || 1<u) throw new IllegalArgumentException("Arguments to inverseCdf must be between 0 and 1.");
        if (u==0.0) return 0.0;
        double s = -Math.log(1-u);
        double t = 0.0;
        double y = 0.0;
        for (int i = 0; i < noOfPeriods; i++) {
            if (s <= upper[i]) {
                return (s-y)/alpha[i]+t;
            }
            t = tenor[i];
            y = upper[i];
        }
        return (s-y)/alpha[noOfPeriods-1]+t;
    }
}
