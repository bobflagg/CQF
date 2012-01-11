package cqf.distribution;

import java.util.Arrays;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class EmpiricalDistribution extends Distribution {
    private double[] sortedObservations;

    public EmpiricalDistribution(double[] observations) {
        sortedObservations = Arrays.copyOf(observations, observations.length);
        Arrays.sort(sortedObservations);
    }
    
    @Override
    public double cdf(double x) {
        double value = 0.0;
        for (int i=0;i<sortedObservations.length;i++) {
            if (sortedObservations[i]>x) break;
            value++;
        }
        return (value-0.5)/sortedObservations.length;
    }

    @Override
    public double inverseCdf(double u) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
