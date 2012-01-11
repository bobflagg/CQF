package cqf.bcd.application;

import cern.jet.random.engine.DRand;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import cqf.bcd.pricing.BCDPricer;
import cqf.bcd.pricing.PricingObserver;
import cqf.copula.Calibrator;
import cqf.copula.Copula;
import cqf.copula.GaussianCalibrator;
import cqf.copula.StudentTCalibrator;
import cqf.core.State;
import cqf.core.State.CopulaType;
import cqf.core.State.RandomEngineType;
import cqf.distribution.Distribution;
import cqf.interest.Discounter;
import cqf.interest.SimpleDiscounter;
import cqf.reference.Entity;
import java.util.List;
import java.util.Observer;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * A facade to appplication layer functionality.
 *
 * Patterns: Application Facade.
 */
public class AppFacade {
    private static final Logger LOGGER = Logger.getLogger(AppFacade.class.getName());
    private Discounter discounter;
    private RandomEngine engineMersenneTwister = new MersenneTwister();
    private RandomEngine engineDRand = new DRand();
    private int noOfPricingDataPoints;
    /**
     * Create an instance of AppFacade.
     *
     * @param   properties  configuration properties for this instance of the facade.
     * @param   observer    observer of status update information from analyzer.
     */
    public AppFacade(Properties properties, Observer observer) throws Exception {
        // build discounter
        double[] maturities = {0.5,1.0,2,3,4,5};
        discounter = new SimpleDiscounter(maturities, new double[] {0.9932727,0.9858018,0.9627129,0.9285788,0.8891939,0.8474275});
        noOfPricingDataPoints = Integer.parseInt(properties.getProperty("no-pricing-data-pts"));
    }

    /**
     * Create an instance of AppFacade.
     *
     * @param   properties  configuration properties for this instance of the facade.
     * @param   observer    observer of status update information from analyzer.
     */
    public double priceBCD(
        List<Entity> entities,
        Copula copula,
        boolean includeAccruedPremium,
        int k,
        double delta,
        int noOfPeriods,
        double recoveryRate,
        int noOfSimulations,
        PricingObserver observer
    ) throws Exception {
        double price = 0.0;
        int noOfNames = entities.size();
        Distribution[] marginals = new Distribution[noOfNames];
        for (int i=0;i<noOfNames;i++) marginals[i] = entities.get(i).getSurvivalDistribution();
        BCDPricer pricer = new BCDPricer(noOfNames, k, delta, noOfPeriods, recoveryRate, discounter, copula, marginals);
        price = pricer.price(noOfSimulations, includeAccruedPremium, observer);
        System.out.println("===>>>>>   Price: "+price);
        return price;
    }

    public Copula calibrate(
        List<Entity> entities,
        CopulaType copulaType,
        RandomEngineType engineType
    ) throws Exception {
        int dimension = entities.size();
        double[][] observations = new double[dimension][noOfPricingDataPoints];
        for (int i=0;i<dimension;i++) {
            Entity entity = entities.get(i);
            entity.calibrate();
            entity.priceData(observations, i);
        }
        RandomEngine engine = null;
        switch (engineType) {
            case DRand: {
                engine = engineDRand;
                break;
            }
            case MersenneTwister: {
                engine = engineMersenneTwister;
                break;
            }
        }
        Calibrator calibrator = null;
        switch (copulaType) {
            case Gaussian: {
                calibrator = new GaussianCalibrator();
                break;
            }
            case StudentT: {
                calibrator = new StudentTCalibrator();
                break;
            }
        }
        return calibrator.calibrate(observations, engine);
    }

    public String formatRate(double rate) {
        return State.RATE_NUMBER_FORMAT.format(rate);
    }

    public String formatValue(double value) {
        return State.VIEW_NUMBER_FORMAT.format(value);
    }
    /**
     * Release all resources for this BCD Pricer.
     *
     */
    public void close() {
        LOGGER.info("Closing BCD Pricer.");
    }

}
