package cqf.copula;

import cern.jet.random.engine.RandomEngine;
import cqf.distribution.Distribution;
import cqf.distribution.EmpiricalDistribution;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;

/**
 *
 * @author birksworks
 */
public abstract class Calibrator {
    private static NormalDistributionImpl normalDistribution = new NormalDistributionImpl();
    public abstract Copula calibrate(double[][] observations, RandomEngine engine) throws MathException ;

    protected static double[][] returns(double[][] adjustedPrices) {
        int dimension = adjustedPrices.length;
        int noOfObservations = adjustedPrices[0].length;
        double[][] returns = new double[dimension][noOfObservations-1];
        for (int i=0;i<dimension;i++) {
            for (int j=1;j<noOfObservations;j++) {
                returns[i][j-1] = Math.log(adjustedPrices[i][j]/adjustedPrices[i][j-1]);
            }
        }
        return returns;
    }

    protected static double[][] transformedVariates(double[][] observations) throws MathException {
        int dimension = observations.length;
        int noOfObservations = observations[0].length;
        double[][] variates = new double[dimension][noOfObservations];
        for (int i=0;i<dimension;i++) {
            Distribution marginal = new EmpiricalDistribution(observations[i]);
            for (int j=0;j<noOfObservations;j++) {
                variates[i][j] = normalDistribution.inverseCumulativeProbability(marginal.cdf(observations[i][j]));
            }
        }
        return variates;
    }

    protected static double[][] transformedTVariates(double[][] observations) throws MathException {
        int dimension = observations.length;
        int noOfObservations = observations[0].length;
        double[][] variates = new double[dimension][noOfObservations];
        for (int i=0;i<dimension;i++) {
            Distribution marginal = new EmpiricalDistribution(observations[i]);
            for (int j=0;j<noOfObservations;j++) {
                variates[i][j] = marginal.cdf(observations[i][j]);
            }
        }
        return variates;
    }

    protected static double[][] uniformVariates(double[][] observations) {
        int dimension = observations.length;
        int noOfObservations = observations[0].length;
        double[][] variates = new double[dimension][noOfObservations];
        for (int i=0;i<dimension;i++) {
            Distribution marginal = new EmpiricalDistribution(observations[i]);
            for (int j=0;j<noOfObservations;j++) {
                variates[i][j] = marginal.cdf(observations[i][j]);
            }
        }
        return variates;
    }

    protected static double pearsonCorrelation(double[] x,double[] y){
        double result = 0;
        double sum_sq_x = 0;
        double sum_sq_y = 0;
        double sum_coproduct = 0;
        double mean_x = x[0];
        double mean_y = y[0];
        for(int i=2;i<x.length+1;i+=1){
            double sweep =Double.valueOf(i-1)/i;
            double delta_x = x[i-1]-mean_x;
            double delta_y = y[i-1]-mean_y;
            sum_sq_x += delta_x * delta_x * sweep;
            sum_sq_y += delta_y * delta_y * sweep;
            sum_coproduct += delta_x * delta_y * sweep;
            mean_x += delta_x / i;
            mean_y += delta_y / i;
        }
        double pop_sd_x = (double) Math.sqrt(sum_sq_x/x.length);
        double pop_sd_y = (double) Math.sqrt(sum_sq_y/x.length);
        double cov_x_y = sum_coproduct / x.length;
        result = cov_x_y / (pop_sd_x*pop_sd_y);
        return result;
    }

    protected static double kendallTau(double[] x,double[] y){
        int n = x.length;
        long n1=0,n2=0,is=0;
        double aa,a1,a2;
        for (int j=0;j<n-1;j++) {
            for (int k=(j+1);k<n;k++) {
                a1 = x[j]-x[k];
                a2 = y[j]-y[k];
                aa = a1*a2;
                if (aa!=0.0) {
                    ++n1;
                    ++n2;
                    if (aa>0.0) ++is;
                    else --is;
                } else {
                    if (a1!=0.0) ++n1;
                    if (a2!=0.0) ++n2;
                }
            }
        }
        return is/(Math.sqrt((double)n1)*Math.sqrt((double)n2));
    }

}

