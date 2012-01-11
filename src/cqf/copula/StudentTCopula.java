package cqf.copula;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.random.ChiSquare;
import cern.jet.random.Normal;
import cern.jet.random.StudentT;
import cern.jet.random.engine.RandomEngine;
import cern.jet.stat.Gamma;
import cqf.math.Matrix;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistributionImpl;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class StudentTCopula extends Copula {

    private double[][] covariance;
    private DoubleMatrix2D sigma;
    private DoubleMatrix2D sigmaInverse;
    private double determinant;
    private Algebra algebra = new Algebra();
    private double[][] cholesky_factor;
    private double nu;
    private Normal normal;
    private ChiSquare chiSquare;
    private StudentT studentT;
    private TDistributionImpl tDistribution;

    public StudentTCopula(double[][] covariance, double nu, RandomEngine engine) {
        this.covariance = covariance;
        sigma = new DenseDoubleMatrix2D(covariance);
        sigmaInverse = algebra.inverse(sigma);
        determinant = algebra.det(sigma);
        this.nu = nu;
        normal = new Normal(0.0, 1.0, engine);
        chiSquare = new ChiSquare(nu, engine);
        studentT = new StudentT(nu, engine);
        tDistribution = new TDistributionImpl(nu);
        dimension = covariance[0].length;
        cholesky_factor = new double[dimension][dimension];
        Matrix.CholeskyFactorization(covariance, cholesky_factor);
    }

    public void displayParameters() {
        DoubleMatrix2D sigma = new DenseDoubleMatrix2D(covariance);
        System.out.println("nu -->> "+nu);
        System.out.print(sigma);
    }

    public double[][] getCovariance() {
        return covariance;
    }

    public double getNu() {
        return nu;
    }

    @Override
    public double[] simulate() {
        double z[] = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            z[i] = normal.nextDouble();
        }
        double[] x = Matrix.multiply(cholesky_factor, z);
        double s = chiSquare.nextDouble();
        for (int i = 0; i < dimension; i++) {
            x[i] = studentT.cdf(x[i] * Math.sqrt(nu/s));
        }
        return x;
    }

    @Override
    public double density(double[] u) throws MathException {
        double[] x = new double[dimension];
        double ld = 0.0;
        for (int i=0;i<dimension;i++) {
            x[i] = tDistribution.inverseCumulativeProbability(u[i]);
            ld += Math.log(tDistribution.density(x[i]));
        }
        DenseDoubleMatrix1D xVector = new DenseDoubleMatrix1D(x);
        double a = Gamma.logGamma(0.5 * (nu + dimension)) - Gamma.logGamma(0.5 * nu)-0.5*dimension*Math.log(nu);
        double b = Math.log(1.0 + algebra.mult(xVector, algebra.mult(sigmaInverse, xVector))/nu);
        double c = a-0.5*(dimension*Math.log(Math.PI)+Math.log(determinant)+(nu+dimension)*b);
        return Math.exp(c-ld);
    }

    public double logLikelihood(double[] u) throws MathException {
        double[] x = new double[dimension];
        double n = (double)dimension;
        double ld = 0.0;
        for (int i=0;i<dimension;i++) {
            x[i] = tDistribution.inverseCumulativeProbability(u[i]);
            ld += Math.log(tDistribution.density(x[i]));
        }
        DenseDoubleMatrix1D xVector = new DenseDoubleMatrix1D(x);
        double a = Gamma.logGamma(0.5 * (nu + n)) - Gamma.logGamma(0.5 * nu)-0.5*n*Math.log(nu);
        double b = Math.log(1.0 + algebra.mult(xVector, algebra.mult(sigmaInverse, xVector))/nu);
        double c = a-0.5*(n*Math.log(Math.PI)+Math.log(determinant)+(nu+n)*b);
        return c-ld;
    }
}
