package cqf.bcd.ui.results;

import cqf.bcd.specs.ContractSpecs;
import cqf.bcd.ui.main.AppCoordinator;
import cqf.copula.CalibrationObserver;
import cqf.copula.Copula;
import cqf.reference.Entity;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class CalibrationView extends JTabbedPane implements CalibrationObserver {
    // Constants
    // Locale related
    public static final Locale DEFAULT_LOCALE = Locale.US;
    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(DEFAULT_LOCALE);
    private static DecimalFormat VIEW_DATA_FORMAT = null;
    static {
        // get a NumberFormat object and cast it to
        // a DecimalFormat object
        try {
            VIEW_DATA_FORMAT = (DecimalFormat) NUMBER_FORMAT;
            VIEW_DATA_FORMAT.applyPattern("#,###.00");
        } catch (ClassCastException e) {
            System.err.println(e);
        }
    }

    //private JScrollPane copulaParametersScrollPane = new JScrollPane();
private JPanel copulaParametersCards = new JPanel(new CardLayout());
final static String GAUSSIAN_DISPLAYER = "Gaussian";
final static String STUDENT_T_DISPLAYER = "StudentT";


    private JScrollPane survivalProbabilitiesScrollPane = new JScrollPane();
    private Dimension chartDimension;
    private AppCoordinator coordinator;
    private ContractSpecs contractSpecs;
    private ParameterDisplayer gaussianParameterDisplayer = new GaussianParameterDisplayer();
    private ParameterDisplayer studentTParameterDisplayer = new StudentTParameterDisplayer();

    /** Creates a new instance of View */
    public CalibrationView(AppCoordinator coordinator) {
        setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
        this.coordinator = coordinator;
        contractSpecs = coordinator.getSpecs().getContractSpecs();
        chartDimension = new Dimension(Integer.parseInt(coordinator.getProperty("chart-width")), Integer.parseInt(coordinator.getProperty("chart-height")));
        //setPreferredSize(chartDimension);
        //contract.addObserver(this);

        add(copulaParametersCards, "Copula Specification");
        copulaParametersCards.add(gaussianParameterDisplayer, GAUSSIAN_DISPLAYER);
        copulaParametersCards.add(studentTParameterDisplayer, STUDENT_T_DISPLAYER);

        add(survivalProbabilitiesScrollPane, "Survival Probabilities");
        coordinator.addCalibrationObserver(this);
    }
    /**
     *  This method displays the chart and data text.
     */
    public void updateData(Copula copula, String[] headers) {
        CardLayout cl = (CardLayout)(copulaParametersCards.getLayout());
        switch (coordinator.getSpecs().getSimulationSpecs().getCopulaType()) {
            case Gaussian: {
                gaussianParameterDisplayer.displayParameters(copula, headers);
                cl.show(copulaParametersCards, GAUSSIAN_DISPLAYER);
                break;
            }
            case StudentT: {
                studentTParameterDisplayer.displayParameters(copula, headers);
                cl.show(copulaParametersCards, STUDENT_T_DISPLAYER);
                break;
            }
        }

    }
        /**
     *  This method displays the chart and data text.
     */
    public void displaySurvivalProbabilities() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (Entity entity:contractSpecs.getEntities()) {
            XYSeries series = new XYSeries(entity.getTicker());
            for (int i=0;i<100;i++) {
                series.add(i*0.1, entity.getSurvivalDistribution().Q(i*0.1));
            }
            dataset.addSeries(series);
        }
        JFreeChart chart = ChartFactory.createXYLineChart(
                //"Survival Probability for "+contract.getSelectedEntity(),
                "",
                // chart title
                "Time (in years)",
                // x axis label
                "Probability",
                // y axis label
                dataset,
                // data
                PlotOrientation.VERTICAL,
                true,
                // include legend
                true,
                // tooltips
                false // urls
                );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(chartDimension);
        survivalProbabilitiesScrollPane.setViewportView(chartPanel);
        //this.setSelectedIndex(1);
     }

    public void calibrationComplete(Copula copula) {
        displaySurvivalProbabilities();
        int noOfEntities = contractSpecs.getEntities().size();
        String[] headers = new String[noOfEntities];
        for (int i=0;i<noOfEntities;i++) headers[i] = contractSpecs.getEntities().get(i).getTicker();
        updateData(copula, headers);
    }

    public void calibrationStarted() {
    }

}
