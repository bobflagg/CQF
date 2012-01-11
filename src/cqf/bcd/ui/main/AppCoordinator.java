package cqf.bcd.ui.main;

import cqf.status.AppStatus;
import cqf.bcd.application.AppFacade;
import cqf.bcd.pricing.PricingObserver;
import cqf.bcd.ui.action.ActionMethods;
import cqf.bcd.ui.action.BCDActionFactory;
import cqf.bcd.specs.Specs;
import cqf.bcd.specs.SpecsUpdater;
import cqf.coordination.AbstractCoordinator;
import cqf.copula.CalibrationObserver;
import cqf.copula.Copula;
import cqf.reference.Entity;
import cqf.reference.Importer;
import cqf.status.Status;
import cqf.ui.UIConfigurator;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class AppCoordinator extends AbstractCoordinator implements Observer, ActionMethods, PricingObserver {

    private static final Logger LOGGER = Logger.getLogger(AppCoordinator.class.getName());
    private BCDActionFactory actionFactory;
    private AppFacade facade;
    private AppFrame view;
    private Specs specs;
    private List<SpecsUpdater>  specsUpdaterList = new ArrayList<SpecsUpdater>();
    private List<PricingObserver> pricingObserverList = new ArrayList<PricingObserver>();
    private List<CalibrationObserver> calibrationObserverList = new ArrayList<CalibrationObserver>();


    public void initialize(UIConfigurator configurator) {
        try {
            actionFactory = new BCDActionFactory(this);
            facade = new AppFacade(properties, this);
            specs = new Specs();
            Observer appStatusObserver = new Observer() {
                public void update(Observable observable, Object arg) {
                    Status status = (Status)observable;
                    try{
                        SwingUtilities.invokeLater(new UpdateStatus(status.isWorking(), status.isWarning(), status.getMessage()));
                    } catch(Exception e){e.printStackTrace();}
                }
            };
            AppStatus.instance().addObserver(appStatusObserver);


            AppStatus.instance().updateStatus(true, false, "Initializing BCD Pricer engine....");
            new Importer().importData();
            Thread.sleep(3000);
            view = new AppFrame(this, configurator);
            enableUI();
            for (String ticker:new String[]{"BSX","ANF","MDT","MHS","UNH"}) specs.getContractSpecs().addEntity(Entity.lookup(ticker));
            String statusMessage = "Initialization complete. BCD Pricer is ready for use.";
            AppStatus.instance().updateStatus(false, false, statusMessage);
            //AppStatus.instance().setInitializing(false);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("****************************************************************");
            System.out.println("****************************************************************");
            System.out.println("Unable to initialize BCD Pricer.  Aborting application.");
            System.out.println("****************************************************************");
            System.out.println("****************************************************************");
            System.exit(1);
        }
    }
    public JFrame getView() {
        return view;
    }

    public void enableUI() {
        view.setVisible(true);
    }

    public AbstractAction getAction(String actionName) {
        return actionFactory.getAction(actionName);
    }

    public void update(Observable o, Object arg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addSpecsUpdater(SpecsUpdater specsUpdater) {
        specsUpdaterList.add(specsUpdater);
    }

    public void addCalibrationObserver(CalibrationObserver calibrationObserver) {
        calibrationObserverList.add(calibrationObserver);
    }


    public void addPricingObserver(PricingObserver pricingObserver) {
        pricingObserverList.add(pricingObserver);
    }

    public void notifySpecsUpdaters() {
        for (int i = 0; i < specsUpdaterList.size(); i++) {
            specsUpdaterList.get(i).updateSpecs();
        }
    }

    public void notifyPricingStarted() {
        for (int i = 0; i < pricingObserverList.size(); i++) {
            pricingObserverList.get(i).pricingStarted();
        }
    }

    public void notifyPricingCompleted(double price) {
        for (int i = 0; i < pricingObserverList.size(); i++) {
            pricingObserverList.get(i).pricingComplete(price);
        }
    }

    public void notifyCalibrationStarted() {
        for (int i = 0; i < calibrationObserverList.size(); i++) {
            calibrationObserverList.get(i).calibrationStarted();
        }
    }

    public void notifyCalibrationComplete(Copula copula) {
        for (int i = 0; i < calibrationObserverList.size(); i++) {
            calibrationObserverList.get(i).calibrationComplete(copula);
        }
    }


    public Specs getSpecs() {
        return specs;
    }

    public void callibrate() throws Exception {
        String statusMessage = "Calibrating copula and estimating survival probabilities.  Please wait...";
        AppStatus.instance().updateStatus(true, false, statusMessage);
        notifySpecsUpdaters();
        notifyCalibrationStarted();
        SwingWorker worker = new SwingWorker<Copula, Void>() {
            @Override
            public Copula doInBackground() throws Exception {
                return facade.calibrate(
                    specs.getContractSpecs().getEntities(),
                    specs.getSimulationSpecs().getCopulaType(),
                    specs.getSimulationSpecs().getEngineType()
                );
            }

            @Override
            public void done() {
                try {
                    Copula copula = get();
                    specs.getSimulationSpecs().setCopula(copula);
                    String statusMessage = "Calibration complete.";
                    AppStatus.instance().updateStatus(false, false, statusMessage);
                    notifyCalibrationComplete(copula);
                } catch (InterruptedException ignore) {
                } catch (java.util.concurrent.ExecutionException e) {
                    String why = null;
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        why = cause.getMessage();
                    } else {
                        why = e.getMessage();
                    }
                    System.err.println("Error during calibraton: " + why);
                }
            }
        };
        worker.execute();
    }

    public void price() throws Exception {
        String statusMessage = "Pricing basket credit default.  Please wait...";
        AppStatus.instance().updateStatus(true, false, statusMessage);
        notifySpecsUpdaters();
        notifyPricingStarted();
        SwingWorker worker = new SwingWorker<Double, Void>() {
            @Override
            public Double doInBackground() throws Exception {
                double price = facade.priceBCD(
                    specs.getContractSpecs().getEntities(),
                    specs.getSimulationSpecs().getCopula(),
                    specs.getContractSpecs().isPremiumAccrued(),
                    specs.getContractSpecs().getK(),
                    specs.getContractSpecs().getFrequency().getDelta(),
                    specs.getContractSpecs().getNoOfPeriods(),
                    specs.getSimulationSpecs().getRecoveryRate(),
                    specs.getSimulationSpecs().getNoOfSimulations(),
                    AppCoordinator.this
                );
                return price;
            }

            @Override
            public void done() {
                try {
                    double rate = get();
                    notifyPricingCompleted(rate);
                    String statusMessage = "Pricing complete. The basket credit default fair spread is "+formatRate(rate)+".";
                    AppStatus.instance().updateStatus(false, false, statusMessage);
                } catch (InterruptedException ignore) {
                } catch (java.util.concurrent.ExecutionException e) {
                    String why = null;
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        why = cause.getMessage();
                    } else {
                        why = e.getMessage();
                    }
                    System.err.println("Error pricing BCD: " + why);
                }
            }
        };
        worker.execute();
    }
    public String formatRate(double rate) {
        return facade.formatRate(rate);

    }
    public void pricingComplete(double price) {
    }

    public void pricingStarted() {
    }

    public void dataPointAdded(int step, double approximation, double premiumLegValue, double defaultLegValue) {
        for (int i = 0; i < pricingObserverList.size(); i++) {
            pricingObserverList.get(i).dataPointAdded(step, approximation, premiumLegValue, defaultLegValue);
        }
    }
    /**
     * Runnable inner class to support updating status in Event Dispatching Thread.
     */
    class UpdateStatus implements Runnable {
        boolean _working;
        boolean _warning;
        String _message;

        public UpdateStatus(boolean working, boolean warning, String message) {
            _working = working;
            _warning = warning;
            _message = message;
        }

        public void run() {
        }
    }


    @Override
    public void exit() {
        try {
            facade.close();
            LOGGER.info("Exiting BCD Pricer client.");
        } catch (Exception e) {
            LOGGER.info("Could not close BCD Pricer: " + e.getMessage());
        }
        System.exit(0);
    }

}
