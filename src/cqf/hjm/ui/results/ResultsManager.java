package cqf.hjm.ui.results;

import cqf.core.PricingObserver;
import cqf.core.State;
import cqf.hjm.model.ModelObserver;
import cqf.hjm.state.AppState;
import cqf.hjm.state.StateManager;
import cqf.hjm.ui.main.AppCoordinator;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.time.Millisecond;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class ResultsManager extends JTabbedPane implements StateManager, ModelObserver, PricingObserver {
    // Constants
    // Locale related

    private JScrollPane modelScrollPane = new JScrollPane();
    private JScrollPane pcaScrollPane = new JScrollPane();
    private JScrollPane convergenceScrollPane = new JScrollPane();
    private JScrollPane distributionScrollPane = new JScrollPane();
    private AppState state;
    private final AppCoordinator coordinator;
    private Dimension chartDimension;
    private double[] priceValues;
    private int position;
    private double maxPrice;
    private double minPrice;
    private double maxAverage;
    private double minAverage;
    private XYSeries simulationSeries;
    private int notificationCount = 0;
    private int notificationStep = 0;

    /** Creates a new instance of View */
    public ResultsManager(AppCoordinator coordinator) {
        this.coordinator = coordinator;
        this.state = coordinator.getState();
        coordinator.addPricingObserver(this);
        coordinator.addModelObserver(this);
        setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
        setPreferredSize(new Dimension(
            Integer.parseInt(coordinator.getProperty("chart-width")),
            Integer.parseInt(coordinator.getProperty("chart-height"))
        ));
        state.addManager(this);
        add(modelScrollPane, "Model Parameters");
        add(pcaScrollPane, "Principal Components");
        add(convergenceScrollPane, "Convergence");
        add(distributionScrollPane, "Distribution");
        chartDimension = new Dimension(Integer.parseInt(coordinator.getProperty("chart-width")), Integer.parseInt(coordinator.getProperty("chart-height")));
    }
/*
    private ChartPanel buildSimulationChart() {
        simulationValues = new TimeSeries("Product Price", Millisecond.class);
        simulationValues.setMaximumItemAge(1000000);
        DateAxis domain = new DateAxis("Time");
        NumberAxis range = new NumberAxis("Price");
        range.setLowerBound(0.98);
        range.setUpperBound(1.1);
        domain.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        domain.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));
        range.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesPaint(1, Color.green);
        renderer.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(simulationValues);
        XYPlot plot = new XYPlot(dataset, domain, range, renderer);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        domain.setAutoRange(true);
        domain.setLowerMargin(0.0);
        domain.setUpperMargin(0.0);
        domain.setTickLabelsVisible(true);
        range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        JFreeChart chart = new JFreeChart("", new Font("SansSerif", Font.BOLD, 24), plot, false);
        chart.setBackgroundPaint(Color.white);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createLineBorder(Color.black))
        );
        chartPanel.setPreferredSize(chartDimension);
        return chartPanel;
    }
 */
    /**
     *  This method displays the chart and data text.
     */
    public void modelChanged() {
        Vector columnNames = new Vector();
        columnNames.add("T");
        columnNames.add("Forward Rate (%)");
        for (int k = 0; k < state.noOfFactors; k++) {
            columnNames.add("Sigma " + (k + 1));
        }
        Vector modelData = coordinator.getModelData();
        JTable table = new JTable(modelData, columnNames);
        modelScrollPane.setViewportView(table);
        pcaScrollPane.setViewportView(buildPCAChart(modelData));
    }

    /**
     *  This method displays the chart and data text.
     */
    public ChartPanel buildPCAChart(Vector modelData) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int k=0;k<state.noOfFactors;k++) {
            XYSeries series = new XYSeries("PCA "+(k+1));
            for (int j = 0; j < modelData.size(); j++) {
                Vector rowData = (Vector) modelData.get(j);
                double x = ((Double)rowData.get(0)).doubleValue();
                double y = Double.parseDouble((String)rowData.get(k+2));
                series.add(x,y);
            }
            dataset.addSeries(series);
        }
        JFreeChart chart = ChartFactory.createXYLineChart(
                //"Survival Probability for "+contract.getSelectedEntity(),
                "",
                // chart title
                "Time (in years)",
                // x axis label
                "",
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
        return chartPanel;
     }
    /**
     *  This method displays the chart and data text.
     */
    public ChartPanel buildLineChart() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(simulationSeries);
        JFreeChart chart = ChartFactory.createXYLineChart(
                //"Survival Probability for "+contract.getSelectedEntity(),
                "",
                // chart title
                "Simulation Step",
                // x axis label
                "Price",
                // y axis label
                dataset,
                // data
                PlotOrientation.VERTICAL,
                false,
                // include legend
                true,
                // tooltips
                false
                // urls
        );
        XYPlot plot = chart.getXYPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRange(true);
        rangeAxis.setLowerBound(0.9*minAverage);
        rangeAxis.setUpperBound(1.1*maxAverage);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(chartDimension);
        return chartPanel;
     }

    public ChartPanel buildHistogram() {
       HistogramDataset dataset = new HistogramDataset();
       dataset.setType(HistogramType.RELATIVE_FREQUENCY);
       Arrays.sort(priceValues);
       int minIndex = (int) Math.floor(0.01*((double)priceValues.length));
       int maxIndex = (int) Math.floor(0.99*((double)priceValues.length));
       double[] values = new double[maxIndex-minIndex+1];
       for (int i=minIndex;i<=maxIndex;i++) values[i-minIndex] = priceValues[i];
       //dataset.addSeries("Par Rate", values, State.NO_HISTOGRAM_BINS, values[0],values[values.length-1]);
       dataset.addSeries("Price", values, State.NO_HISTOGRAM_BINS);
       String plotTitle = "";
       String xaxis = "Price";
       String yaxis = "Relative Frequency";
       PlotOrientation orientation = PlotOrientation.VERTICAL;
       boolean show = false;
       boolean toolTips = false;
       boolean urls = false;
       JFreeChart chart = ChartFactory.createHistogram(
            plotTitle,
            xaxis,
            yaxis,
            dataset,
            orientation,
            show,
            toolTips,
            urls
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(chartDimension);
        return chartPanel;
     }

    public void refreshState() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    //public void pointAdded(int step, double value) {
    //    simulationValues.add(new Millisecond(), value);
    //}

    //public void restart() {
    //    simulationValues.clear();
    //    this.setSelectedIndex(2);
    //}

   // public void setPrice(double price) {
        //System.out.println("--->>> "+price);
    //}


    public void pricingComplete(double price) {
        distributionScrollPane.setViewportView(buildHistogram());
        convergenceScrollPane.setViewportView(buildLineChart());
    }

    private static JPanel EMPTY_CHART = new JPanel();
    public void pricingStarted() {
        //simulationValues.clear();
        int noOfSimulations = state.noOfSimulations;
        notificationStep = noOfSimulations / State.NUMBER_OF_PRICING_NOTIFICATIONS;
        convergenceScrollPane.setViewportView(EMPTY_CHART);
        distributionScrollPane.setViewportView(EMPTY_CHART);
        simulationSeries = new XYSeries("Price");
        maxPrice = Double.NEGATIVE_INFINITY;
        minPrice = Double.POSITIVE_INFINITY;
        maxAverage = Double.NEGATIVE_INFINITY;
        minAverage = Double.POSITIVE_INFINITY;
        priceValues = new double[noOfSimulations];
        position = 0;
    }

    public void dataPointAdded(int step, double average, double value) {
        //simulationValues.add(new Millisecond(), value);
        notificationCount++;
        if (notificationCount==notificationStep) {
            simulationSeries.add(step, average);
            notificationCount = 0;
            if (average > maxAverage) maxAverage = average;
            if (average < minAverage) minAverage = average;
        }
        if (value > maxPrice) maxPrice = value;
        if (value < minPrice) minPrice = value;
        priceValues[position] = value;
        position++;
    }


}
