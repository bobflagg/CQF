package cqf.intensity;

import cqf.core.Quote;
import cqf.interest.Discounter;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class HazardRateCalibrator {
    protected int noOfPeriods;
    protected Discounter discounter;
    protected double recoveryRate;
    protected double[] maturities;
    protected Quote[] quotes;

    public HazardRateCalibrator(
        Discounter discounter,
        double recoveryRate,
        double[] maturities,
        Quote[] quotes
    ) {
        this.noOfPeriods = maturities.length;
        this.discounter = discounter;
        this.recoveryRate = recoveryRate;
        this.maturities = maturities;
        this.quotes = quotes;
    }

    public double[] defaultProbabilities() {
        double x=0, y=0, f=0, t=0.0, l=1.0-recoveryRate;
        double[] probabilities = new double[noOfPeriods];
        for (int i=0;i<noOfPeriods;i++) {
            double discount = discounter.discountFactor(maturities[i]);
            double delta = maturities[i]-t;
            double s = quotes[i].average();
            double numerator = l*(discount*f-y)+delta*s*(x+discount);
            double denominator = discount*(l+delta*s);
            probabilities[i] = numerator/denominator;
            y += discount*(probabilities[i]-f);
            f = probabilities[i];
            x += discount*(1.0-f);
            t = maturities[i];
        }
        return probabilities;
    }

    public double[] defaultProbabilitiesAlt() {
        double x=0, y=0, p=1, t=0.0, l=1.0-recoveryRate;
        double d, delta, s, denominator;
        double[] probabilities = new double[noOfPeriods];
        for (int i=0;i<noOfPeriods;i++) {
            d = discounter.discountFactor(maturities[i]);
            delta = maturities[i]-t;
            s = quotes[i].average();
            denominator = d*(l+delta*s);
            probabilities[i] = (l*x-delta*s*y)/denominator+p*l/(l+delta*s);
            x += d*(p-probabilities[i]);
            y += d*probabilities[i];
            p = probabilities[i];
            t = maturities[i];
        }
        return probabilities;
    }

    public double[] calibrate() {
        double t=0.0,sum=0.0;
        double[] probabilities = defaultProbabilitiesAlt();
        double[] alpha = new double[noOfPeriods];
        for (int i=0;i<noOfPeriods;i++) {
            double delta = maturities[i]-t;
            alpha[i] = -(Math.log(probabilities[i])+sum)/delta;
            sum += alpha[i]*delta;
            t = maturities[i];
        }
        return alpha;
    }

    public double[] calibrateAlt() {
        double t=0.0,p=1.0;
        double[] probabilities = defaultProbabilitiesAlt();
        double[] alpha = new double[noOfPeriods];
        for (int i=0;i<noOfPeriods;i++) {
            double delta = maturities[i]-t;
            alpha[i] = -(Math.log(probabilities[i]/p))/delta;
            p = probabilities[i];
            t = maturities[i];
        }
        return alpha;
    }
}
