package cqf.copula;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.jet.random.Normal;
import cern.jet.random.engine.RandomEngine;
import cqf.math.Matrix;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class GaussianCopula extends Copula {

    private double[][] covariance;
    private double[][] cholesky_factor;
    private Normal normal;

    public GaussianCopula(double[][] covariance, RandomEngine engine) {
        this.covariance = covariance;
        normal = new Normal(0.0, 1.0, engine);
        dimension = covariance[0].length;
        cholesky_factor = new double[dimension][dimension];
        Matrix.CholeskyFactorization(covariance, cholesky_factor);
    }

    public void displayParameters() {
        DoubleMatrix2D sigma = new DenseDoubleMatrix2D(covariance);
        System.out.print(sigma);
    }

    public double[][] getCovariance() {
        return covariance;
    }


    @Override
    public double[] simulate() {
        double z[] = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            z[i] = normal.nextDouble();
        }
        double[] x = Matrix.multiply(cholesky_factor, z);
        for (int i = 0; i < dimension; i++) {
            x[i] = normal.cdf(x[i]);
        }
        return x;
    }

    @Override
    public double density(double[] x) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
