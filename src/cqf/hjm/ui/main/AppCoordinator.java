package cqf.hjm.ui.main;

import cern.jet.random.engine.RandomEngine;
import cqf.hjm.state.AppState;
import cqf.coordination.AbstractCoordinator;
import cqf.core.PricingObserver;
import cqf.hjm.application.AppFacade;
import cqf.hjm.model.ModelObserver;
import cqf.hjm.product.Product;
import cqf.status.AppStatus;
import cqf.status.Status;
import cqf.ui.UIConfigurator;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.ujmp.core.exceptions.MatrixException;

/**
 * http://www.deitel.com/articles/java_tutorials/20051126/JavaMultithreading_Tutorial_Part1.html
 * @author Bob Flagg <bob@calcworks.net>
 */
public class AppCoordinator extends AbstractCoordinator implements Observer  {

    private static final Logger LOGGER = Logger.getLogger(AppCoordinator.class.getName());

    private static final ExecutorService executorService = Executors.newFixedThreadPool(
            Math.max(1, Runtime.getRuntime().availableProcessors()),
            new ThreadFactory() {
                //Give priority to the gui thread

                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setPriority(Thread.MIN_PRIORITY);
                    return thread;
                }
            });


    private AppFacade facade;
    private AppFrame view;
    private AppState state;


    public AppState getState() {
        return state;
    }

    public void initialize(UIConfigurator configurator) {
        try {
            state = new AppState();
            Observer appStatusObserver = new Observer() {
                public void update(Observable observable, Object arg) {
                    Status status = (Status)observable;
                    try{
                        SwingUtilities.invokeLater(new UpdateStatus(status.isWorking(), status.isWarning(), status.getMessage()));
                    } catch(Exception e){e.printStackTrace();}
                }
            };
            AppStatus.instance().addObserver(appStatusObserver);


            AppStatus.instance().updateStatus(true, false, "Initializing HJM Pricer engine....");
            facade = new AppFacade(properties, this);
            //Thread.sleep(3000);
            view = new AppFrame(this, configurator);
            enableUI();
            String statusMessage = "Initialization complete. HJM Pricer is ready for use.";
            AppStatus.instance().updateStatus(false, false, statusMessage);
            //AppStatus.instance().setInitializing(false);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("****************************************************************");
            System.out.println("****************************************************************");
            System.out.println("Unable to initialize HJM Pricer.  Aborting application.");
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

    public void update(Observable o, Object arg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void price() throws Exception {
        final Product product = state.getProduct();
        final RandomEngine engine = state.getEngine();
        SwingWorker worker = new SwingWorker<Double, Void>() {

            @Override
            public Double doInBackground() {
                double price = 0.0;
                try {
                    price = facade.price(product, state.noOfFactors, state.noOfSimulations, engine);
                } catch (MatrixException ex) {
                    Logger.getLogger(AppCoordinator.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(AppCoordinator.class.getName()).log(Level.SEVERE, null, ex);
                }
                return price;
            }

            @Override
            public void done() {
                try {
                    facade.updatePrice(get());
                } catch (InterruptedException ignore) {
                } catch (java.util.concurrent.ExecutionException e) {
                    String why = null;
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        why = cause.getMessage();
                    } else {
                        why = e.getMessage();
                    }
                    System.err.println("Error retrieving file: " + why);
                }
            }
        };
        worker.execute();
    }

    public void addModelObserver(ModelObserver observer) {
        facade.addModelObserver(observer);
    }

    public Vector getModelData() {
        return facade.getModelData();
    }

    public String formatRate(double rate) {
        return facade.formatRate(rate);
    }

    public String formatValue(double value) {
        return facade.formatValue(value);
    }

    public AbstractAction getAction(String actionName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addPricingObserver(PricingObserver observer) {
        facade.addPricingObserver(observer);
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
            System.out.println(_message);
        }
    }


    @Override
    public void exit() {
        try {
            facade.close();
            LOGGER.info("Exiting HJM Pricer client.");
        } catch (Exception e) {
            LOGGER.info("Could not close HJM Pricer: " + e.getMessage());
        }
        System.exit(0);
    }

}
