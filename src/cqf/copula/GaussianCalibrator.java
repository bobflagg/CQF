package cqf.copula;

import cern.jet.random.engine.RandomEngine;
import org.apache.commons.math.MathException;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class GaussianCalibrator extends Calibrator {

    public Copula calibrate(double[][] adjustedPrices, RandomEngine engine) throws MathException {
        return calibrateFromReturns(returns(adjustedPrices), engine);
    }

    public Copula calibrateFromReturns(double[][] returns, RandomEngine engine) throws MathException {
        return calibrateFromVariates(transformedVariates(returns), engine);
    }

    public Copula calibrateFromVariates(double[][] variates, RandomEngine engine) {
        int dimension = variates.length;
        int noOfObservations = variates[0].length;
        double[][] covariance = new double[dimension][dimension];
        for (int i=0;i<dimension;i++) {
            for (int j=0;j<i+1;j++) {
                double sum = 0.0;
                for (int k=0;k<noOfObservations;k++) sum += variates[i][k]*variates[j][k];
                covariance[i][j] = sum/noOfObservations;
                covariance[j][i] = covariance[i][j];
            }
        }
        return new GaussianCopula(covariance, engine);
    }

}
