package cqf.bcd.pricing;

import cern.jet.random.engine.RandomEngine;
import cqf.copula.GaussianCopula;
import cqf.distribution.Distribution;
import cqf.interest.Discounter;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class GaussianBCDPricer extends BCDPricer {

    public GaussianBCDPricer(
        int no_ref_names,
        int k,
        double delta,
        int m,
        double recovery_rate,
        Discounter discounter,
        Distribution[] marginals,
        double[][] covariance,
        RandomEngine engine
    ) {
        super(no_ref_names,k,delta,m,recovery_rate,discounter,marginals);
        copula = new GaussianCopula(covariance,engine);
    }

}
