package cqf.hjm.application;

import cern.jet.random.engine.RandomEngine;
import cqf.core.PricingObserver;
import cqf.core.State;
import cqf.hjm.model.Calibrator;
import cqf.hjm.model.Model;
import cqf.hjm.model.ModelObserver;
import cqf.hjm.model.Pricer;
import cqf.hjm.product.Product;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Properties;
import java.util.Vector;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.enums.FileFormat;
import org.ujmp.core.exceptions.MatrixException;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class AppFacade {
    private static final String DATA_FILE_PATH = "/data/fwds.csv";
    private static final double DELTA = 1.0/12.0;

    private Model model;
    private Matrix forwardRateData = null;
    private int noOfFactorsInLastCalibration = -1;
    private List<ModelObserver> modelObservers = new ArrayList<ModelObserver>();
    private List<PricingObserver> pricingObservers = new ArrayList<PricingObserver>();

    /**
     * Create an instance of AppFacade.
     *
     * @param   properties  configuration properties for this instance of the facade.
     * @param   observer    observer of status update information from analyzer.
     */
    public AppFacade(Properties properties, Observer observer) throws Exception {
        loadData();
    }
    public void calibrate(int noOfFactors) throws MatrixException, IOException {
        if (noOfFactors != noOfFactorsInLastCalibration) {
            if (forwardRateData == null) loadData();
            model = Calibrator.calibrate(forwardRateData, noOfFactors);
            notifyModelObservers();
        }
    }
    public void loadData() throws MatrixException, IOException {
        InputStream in = getClass().getResourceAsStream(DATA_FILE_PATH);
        forwardRateData = MatrixFactory.importFromStream(FileFormat.CSV, in, ",");
        forwardRateData = forwardRateData.toDoubleMatrix();
    }
    public double price(Product product, int noOfFactors, int noOfSimulations, RandomEngine engine) throws MatrixException, IOException {
        double maturity = product.getMaturity();
        int gridSize = (int) Math.ceil(maturity/DELTA);
        double[] grid = new double[gridSize];
        for (int i=0;i<gridSize;i++) {
            grid[i] = (i+1)*DELTA;
        }
        calibrate(noOfFactors);
        Pricer pricer = new Pricer(model,grid,engine);
        for (int i = 0; i < pricingObservers.size(); i++) {
            PricingObserver observer = pricingObservers.get(i);
            observer.pricingStarted();
            pricer.addObserver(observer);
        }
        return pricer.price(product, noOfSimulations);
    }
    public void close() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Vector getModelData() {
        return model.getData();
    }

    public void addModelObserver(ModelObserver observer) {
        modelObservers.add(observer);
    }

    public void notifyModelObservers() {
        for (int i=0;i<modelObservers.size();i++) modelObservers.get(i).modelChanged();
    }

    public void addPricingObserver(PricingObserver observer) {
        pricingObservers.add(observer);
    }

    public String formatRate(double rate) {
        return State.RATE_NUMBER_FORMAT.format(rate);
    }

    public String formatValue(double value) {
        return State.VIEW_NUMBER_FORMAT.format(value);
    }

    public void updatePrice(double price) {
        for (int i = 0; i < pricingObservers.size(); i++) {
            pricingObservers.get(i).pricingComplete(price);
        }
    }
}
