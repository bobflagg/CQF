/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cqf.copula;

import org.apache.commons.math.MathException;

/**
 *
 * @author birksworks
 */
public abstract class Copula {
    protected int dimension;

    public abstract double density (double[] u) throws MathException;
    public abstract double[] simulate();
    public abstract void displayParameters();

    public double[][] simulate(int n) {
        double[][] samples = new double[n][dimension];
        for (int i=0;i<n;i++) {
            samples[i] = simulate();
        }
        return samples;
    }


}
