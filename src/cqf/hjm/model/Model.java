package cqf.hjm.model;

import cqf.core.State;
import java.util.Vector;

/**
 * The parameter values making up an implementation of an HJM model.
 * 
 * @author Bob Flagg <bob@calcworks.net>
 */
public class Model {
    private double[] tau;
    private double[] initialRates;
    private double[][] sigma;
    private int numberOfFactors;

    public Model(double[] tau, double[] initialRates, double[][] sigma) {
        this.tau = tau;
        this.initialRates = initialRates;
        this.sigma = sigma;
        numberOfFactors = sigma.length;
    }

    public int getNumberOfFactors() {
        return numberOfFactors;
    }
    public Vector getData() {
        Vector data = new Vector();
        for (int j=0;j<initialRates.length;j++) {
            Vector rowData = new Vector();
            rowData.add(new Double(tau[j]));
            rowData.add(State.RATE_DATA_FORMAT.format(initialRates[j]*100.0));
            for (int k=0;k<numberOfFactors;k++) rowData.add(State.VIEW_DATA_FORMAT.format(sigma[k][j]));
            data.add(rowData);
        }
        return data;
    }
    public double[] computeInitialCurve(double[] grid) {
        double[] values = new double[grid.length];
        for (int i=0;i<grid.length;i++) values[i] = interpolateIntialRate(grid[i]);
        return values;
    }
    public double[][][] computeSigmaHat(double[] grid) {
        int m = grid.length;
        double[][][] values = new double[numberOfFactors][m][m];
        for (int k=0;k<numberOfFactors;k++) {
            double t = 0.0;
            for (int i=0;i<m;i++) {
                for (int j=0;j<m-i;j++) {
                    values[k][i][j] = interpolateSigma(k, grid[i+j]-t);
                }
                t = grid[i];
            }
        }
        return values;
    }
    private double interpolateIntialRate(double t) {
        return interpolate(initialRates, t);
    }
    private double interpolateSigma(int k, double t) {
        return interpolate(sigma[k], t);
    }
    private double interpolate(double[] values, double t) {
        double lowerLimit = 0.0;
        for (int i=0;i<tau.length;i++) {
            if (lowerLimit<=t && t<tau[i]) return values[i];
        }
        return values[tau.length-1];
    }
}
