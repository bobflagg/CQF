package cqf.hjm.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.doublematrix.calculation.general.decomposition.Eig;
import org.ujmp.core.enums.FileFormat;
import org.ujmp.core.exceptions.MatrixException;
import org.ujmp.core.matrix.DenseMatrix;

/**
 * A static class implementing the HJM calibration.
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class Calibrator {

    private Calibrator() {}

    public static Model calibrate(InputStream in, int numberOfFactors) throws MatrixException, IOException {
        Matrix forwardRateData = MatrixFactory.importFromStream(FileFormat.CSV, in, ",");
        forwardRateData = forwardRateData.toDoubleMatrix();
        return calibrate(forwardRateData, numberOfFactors);
    }

    public static Model calibrate(Matrix forwardRateData, int noOfFactors) {
        double[] tau = forwardRateData.selectRows(Ret.NEW, 0).toDoubleArray()[0];
        Matrix forwardRates = forwardRateData.deleteRows(Ret.NEW, 0).divide(100.0);
        TreeSet<EigenData> eigenDataSet = findEigenData(forwardRates);    
        double[] forwardRate = initialForwardRates(forwardRates);
        double[][] sigma = volatilityFactors(eigenDataSet,noOfFactors, tau.length);
        return new Model(tau, forwardRate, sigma);
    }

    public static double[][] volatilityFactors(TreeSet<EigenData> eigenDataSet, int noOfFactors, int m) {
        double[][] sigma = new double[noOfFactors][m];
        Iterator<EigenData> iterator = eigenDataSet.iterator();
        int k = 0;
        while(iterator.hasNext()) {
            if (k == noOfFactors) break;
            EigenData eigenData = iterator.next();
            sigma[k] = eigenData.getEigenVector().times(Math.sqrt(eigenData.getEigenvalue())).transpose().toDoubleArray()[0];
            k++;
        }
        return sigma;
    }
    public static double[] initialForwardRates(Matrix forwardRates) {
        double[] initialForwardRates = new double[(int) forwardRates.getColumnCount()];
        for (int i = 0; i < initialForwardRates.length; i++) {
            //forward fill the fwd rate data in case any is missing from the last row...
            int row = (int) (forwardRates.getRowCount() - 1);
            while (Double.isNaN(forwardRates.getAsDouble(row, i))) {
                row--;
            }
            initialForwardRates[i] = forwardRates.getAsDouble(row, i);
        }
        return initialForwardRates;
    }
    /**
     * Return an ordered set of eigenvalue/eigenvector pairs.
     * @return
     * @throws MatrixException
     */
    public static TreeSet<EigenData> findEigenData(Matrix forwardRates) throws MatrixException {
        Matrix diffs = diff(forwardRates);
        //Annualized, using 252 business days...
        Matrix covarianceMatrix = diffs.cov(Ret.NEW, true).times(252d);
        TreeSet<EigenData> results = orderedEigenData(covarianceMatrix);
        return results;
    }

    public static TreeSet<EigenData> orderedEigenData(Matrix covarianceMatrix) {
        Eig.EigMatrix eigenMatrix = new Eig.EigMatrix(covarianceMatrix);
        //covariance matrix is symetric, so only real eigenvalues...
        double[] eigenValues = eigenMatrix.getRealEigenvalues();
        Matrix eigenVectors = eigenMatrix.getV();
        // create an ordered set of EigenData objects, descending by eigenvalues
        TreeSet<EigenData> results = new TreeSet<EigenData>();
        for (int i = 0; i < eigenValues.length; i++) {
            Matrix eigenVector = eigenVectors.selectColumns(Ret.LINK,Collections.singleton(i));
            results.add(new EigenData(eigenValues[i],eigenVector.divide(eigenVector.getEuklideanValue())));
        }
        return results;
    }

    /**
     * Return a matrix of daily changes in F(t, Tau)
     *
     * @param fwds Daily forward rates
     * @return
     */
    public static Matrix diff(Matrix forwardRates) {
        //offset the fwd rates by one
        Matrix offset = DenseMatrix.factory.zeros(1, forwardRates.getColumnCount());
        offset.fill(Ret.LINK, Double.NaN);
        offset = offset.appendVertically(forwardRates);
        offset = offset.deleteRows(Ret.LINK, offset.getRowCount() - 1);
        Matrix diffs = forwardRates.minus(offset).deleteRows(Ret.NEW, 0);
        return diffs;
    }

}
