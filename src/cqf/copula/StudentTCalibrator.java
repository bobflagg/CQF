package cqf.copula;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.random.engine.RandomEngine;
import cern.jet.stat.Gamma;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistributionImpl;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class StudentTCalibrator extends Calibrator {
    private static final double MAXIMUM_NU_VALUE = 10.0;
    private static final double DELTA = 0.2;

    private double maximumNuValue;
    private double delta;
    private Algebra algebra = new Algebra();

    public StudentTCalibrator(double maximumNuValue, double delta) {
        this.maximumNuValue = maximumNuValue;
        this.delta = delta;
    }

    public StudentTCalibrator() {
        this(MAXIMUM_NU_VALUE,DELTA);
    }


    public Copula calibrate(double[][] adjustedPrices, RandomEngine engine) throws MathException {
        return calibrateFromReturns(returns(adjustedPrices), engine);
    }

    public Copula calibrateFromReturns(double[][] returns, RandomEngine engine) throws MathException {
        return calibrateFromVariates(transformedTVariates(returns), engine);
    }



    // Galiani, p. 28
    public Copula calibrateFromVariates(double[][] variates, RandomEngine engine) throws MathException {
        int dimension = variates.length;
        int noOfObservations = variates[0].length;
        double[][] covarianceData = covariance(variates);
        DoubleMatrix2D sigma = new DenseDoubleMatrix2D(covarianceData);
        //System.out.print(sigma);
        double nu = optimalNu(variates,sigma,covarianceData);
        return new StudentTCopula(covarianceData, nu, engine);
    }

    private double[][] covariance(double[][] variates) {
        int dimension = variates.length;
        int noOfObservations = variates[0].length;
        double[][] covarianceData = new double[dimension][dimension];
        for (int i=0;i<dimension;i++) {
            for (int j=0;j<i+1;j++) {
                double tau = kendallTau(variates[i],variates[j]);
                covarianceData[i][j] = Math.sin(0.5*Math.PI*tau);
                covarianceData[j][i] = covarianceData[i][j];
            }
        }
        return covarianceData;
    }

    // Galiani, p. 21
    private double optimalNu(double[][] variates, DoubleMatrix2D sigma, double[][] covariance) throws MathException {
        int dimension = variates.length;
        int noOfObservations = variates[0].length;
        DoubleMatrix2D sigmaInverse = algebra.inverse(sigma);
        double determinant = algebra.det(sigma);
        double nu=2.0, value, a, b, argMax = 0.0, maxValue = Double.NEGATIVE_INFINITY;
        while (nu <= maximumNuValue) {
            a = Gamma.logGamma(0.5 * (nu + dimension)) - Gamma.logGamma(0.5 * nu)-0.5*dimension*Math.log(nu);
            b = 0.5*(dimension*Math.log(Math.PI)+Math.log(determinant));
            StudentTCopula tCopula = new StudentTCopula(covariance, nu, null);
            TDistributionImpl tDistribution = new TDistributionImpl(nu);
            value = 0.0;
            double ld = 0.0;
            for (int j=0;j<noOfObservations;j++) {
                double[] variate = new double[dimension];               
                for (int i=0;i<dimension;i++) {
                    variate[i] = tDistribution.inverseCumulativeProbability(variates[i][j]);
                    ld += Math.log(tDistribution.density(variate[i]));
                }
                DenseDoubleMatrix1D variateVector = new DenseDoubleMatrix1D(variate);
                value += Math.log(1.0 + algebra.mult(variateVector, algebra.mult(sigmaInverse, variateVector))/nu);
            }
            value = noOfObservations*(a-b)-0.5*(nu+dimension)*value-ld;
            if (value > maxValue) {
                maxValue = value;
                argMax = nu;
            }
            nu += delta;
        }
        return argMax;
    }

    public double logLikelihood(
        double[] variate,
        DoubleMatrix2D sigma,
        double nu
    ) throws MathException {
        int dimension = variate.length;
        TDistributionImpl tDistribution = new TDistributionImpl(nu);
        double[] tVariate = new double[dimension];
        double[] observation = new double[dimension];
        for (int i=0;i<dimension;i++) {
            //tVariate[i] = tDistribution.inverseCumulativeProbability(variate[i]);
            tVariate[i] = variate[i];
            observation[i] = tVariate[i];
        }
        DenseDoubleMatrix1D variateVector = new DenseDoubleMatrix1D(observation);
        DoubleMatrix2D sigmaInverse = algebra.inverse(sigma);
        double determinant = algebra.det(sigma);
        double value, a = 0.0, b = 0.0;
        value =
                Math.log(Gamma.gamma(0.5 * (nu + dimension)) / Gamma.gamma(0.5 * nu))
                - dimension * Math.log(Gamma.gamma(0.5 * (nu + 1)) / Gamma.gamma(0.5 * nu))
                - 0.5 * Math.log(determinant);
        a = Math.log(1.0 + algebra.mult(variateVector, algebra.mult(sigmaInverse, variateVector))/nu);
        b = 0.0;
        for (int i = 0; i < dimension; i++) {
            b += Math.log(1 + tVariate[i] * tVariate[i] / nu);
        }
        value += 0.5 * ((nu + 1.0) * b - (nu + dimension) * a);
        return value;
    }

}
