package cqf.bcd.pricing;

import cern.jet.random.engine.RandomEngine;
import cqf.copula.StudentTCopula;
import cqf.distribution.Distribution;
import cqf.interest.Discounter;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class StudentTBCDPricer extends BCDPricer {

    public StudentTBCDPricer(
        int no_ref_names,
        int k,
        double delta,
        int m,
        double recovery_rate,
        Discounter discounter,
        Distribution[] marginals,
        double[][] covariance,
        double nu,
        RandomEngine engine
    ) {
        super(no_ref_names,k,delta,m,recovery_rate,discounter,marginals);
        copula = new StudentTCopula(covariance,nu,engine);
    }

}
