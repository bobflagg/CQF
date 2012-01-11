package cqf.interest;

import cqf.core.TimedQuote;
import static java.lang.Math.*;

/**
 * 
 * @author Bob Flagg <bob@calcworks.net>
 */
public class TermStructure {

    private static final double DELTA = 0.5;

    /**
     * Computes term structure of zero rates based on partial zero rate and swap data.
     *
     * @param   zeroRates   initial semi-annual zero rate data
     * @param   swapData    bid, ask data for swaps of various maturities
     *
     * @author Bob Flagg <bob@calcworks.net>
     */
    public static double[][] yieldCurve(double[] zeroRates, TimedQuote[] swapData) throws IllegalArgumentException {
        int noOfPeriods = (int) (swapData[swapData.length - 1].getTime() / DELTA);
        double startTime = swapData[0].getTime();
        if (zeroRates.length * DELTA + DELTA != startTime) {
            throw new IllegalArgumentException("In TermStructure.yieldCurve zeroRates must provide initial semi-annual zero rate data.");
        }
        double[][] curve = new double[noOfPeriods][2];
        // store initial zero rates
        double time = DELTA;
        int i = 0;
        double sum = 0.0;
        while (i < zeroRates.length) {
            curve[i][0] = time;
            curve[i][1] = zeroRates[i];
            sum += Math.exp(-zeroRates[i] * time);
            time += DELTA;
            i++;
        }
        // bootstrap further rates from swap data
        int j = 0;
        while (time <= noOfPeriods * DELTA) {
            double s = swapData[j].average();
            if (time != swapData[j].getTime()) {
                // interpolate to get appropriate swap rate
                double t = swapData[j].getTime();
                double tp = swapData[j - 1].getTime();
                double sp = swapData[j - 1].average();
                s = sp + (s - sp) * (time - tp) / (t - tp);
            } else {
                j++;
            }
            curve[i][0] = time;
            double z = (1 - DELTA * s * sum) / (1 + DELTA * s);
            curve[i][1] = -Math.log(z) / time;
            sum += z;
            time += DELTA;
            i++;
        }
        return curve;
    }
}

