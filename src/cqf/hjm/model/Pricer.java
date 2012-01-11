package cqf.hjm.model;

import cern.jet.random.Normal;
import cern.jet.random.engine.RandomEngine;
import cqf.core.PricingObserver;
import cqf.hjm.product.Product;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class Pricer {
    private List<PricingObserver> observers = new ArrayList<PricingObserver>();
    protected Model model;
    protected double[] grid;
    private Normal standardNormal;
    protected double[] delta;
    private double[] drift;
    private double[] forwardCurve;
    private double[] initialCurve;
    private double[][][] sigmaHat;
    private int noOfFactors;
    private int noOfIntervals;

    public Pricer(Model model, double[] grid, RandomEngine engine) {
        this.model = model;
        this.grid = grid;
        standardNormal = new Normal(0.0, 1.0, engine);
        noOfFactors = model.getNumberOfFactors();
        initialCurve = model.computeInitialCurve(grid);
        sigmaHat = model.computeSigmaHat(grid);
        noOfIntervals = grid.length;
        delta = new double[noOfIntervals];
        double t = 0.0;
        for (int i = 0; i < noOfIntervals; i++) {
            delta[i] = grid[i] - t;
            t = grid[i];
        }
        drift = new double[noOfIntervals];
        forwardCurve = new double[noOfIntervals];
    }

    public double price(Product product, int noOfSimulations) {
        double sum = 0.0;
        for (int i = 0; i < noOfSimulations; i++) {
            double value = presentValue(product);
            sum += value;
            notifyObservers(i, sum/(i+1), value);
        }
        return sum / noOfSimulations;
    }

    private double presentValue(Product product) {
        double discount = 1.0, discountedPayoff = product.initialCashflow();
        for (int i = 0; i < noOfIntervals; i++) {
            forwardCurve[i] = initialCurve[i];
        }
        double lowerLimit = 0.0;
        for (int i = 0; i < noOfIntervals; i++) {
            double shortRate = forwardCurve[0];
            discount = discount * Math.exp(-shortRate * delta[i]);
            updateDrift(i);
            for (int j = 0; j < noOfIntervals - i - 1; j++) {
                double sum = 0.0;
                for (int k = 0; k < noOfFactors; k++) {
                    sum += sigmaHat[k][i][j] * standardNormal.nextDouble();
                }
                forwardCurve[j] = forwardCurve[j + 1] + drift[j] * delta[i] + sum * Math.sqrt(delta[i]);
            }
            discountedPayoff += discount * product.cashflow(shortRate, lowerLimit, grid[i]);
            lowerLimit = grid[i];
        }
        return discountedPayoff;
    }

    private void updateDrift(int i) {
        double[] a = new double[noOfFactors];
        for (int k = 0; k < noOfFactors; k++) {
            a[k] = 0.0;
        }
        double bPrevious = 0.0;
        for (int j = 0; j < noOfIntervals - i - 1; j++) {
            double bNext = 0.0;
            for (int k = 0; k < noOfFactors; k++) {
                a[k] += sigmaHat[k][i][j] * delta[i + j];
                bNext += a[k] * a[k];
            }
            drift[j] = (bNext - bPrevious) / (2.0 * delta[i + j]);
            bPrevious = bNext;
        }
    }

    public void addObserver(PricingObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(int step, double average, double value) {
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).dataPointAdded(step, average, value);
        }
    }
}
