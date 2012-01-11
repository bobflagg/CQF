/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cqf.rv;

import java.util.Random;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public abstract class RandomVariable {
    protected static Random random = new Random();
    /**
     * Sets the seed of the random number generator of this class.
     *
     * @param   seed    new seed
     */
    public static void setSeed(long seed) {
        random.setSeed(seed);
    }
    public static double nextGaussian() {
        return random.nextGaussian();
    }
    public abstract double sample();
    public double[] sample(int n) {
        double[] samples = new double[n];
        for (int i=0;i<n;i++) {
            samples[i] = sample();
        }
        return samples;
    }

}
