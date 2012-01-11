package cqf.interest;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class YieldCurveDiscounter extends Discounter {
    private static final double DELTA = 0.5;
    protected double[][] curve;

    public YieldCurveDiscounter(double[][] curve) {
        this.curve = curve;
    }

    public YieldCurveDiscounter(double[] rates) {
        curve = new double[rates.length][2];
        for (int i=0;i<rates.length;i++) {
            curve[i][0] = i*DELTA;
            curve[i][1] = rates[i];
        }
    }

    public double discountFactor(double time) {
        for (int i=0;i<curve.length;i++) {
            if (time < curve[i][0]) return Math.exp(-time*curve[i][1]);
        }
        return  Math.exp(-time*curve[curve.length-1][1]);
    }


}
