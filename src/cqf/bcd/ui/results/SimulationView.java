/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cqf.bcd.ui.results;

import cqf.bcd.pricing.PricingObserver;
import cqf.bcd.ui.main.AppCoordinator;
import cqf.core.State;
import java.awt.Dimension;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class SimulationView extends JTabbedPane implements PricingObserver {
    // Constants
    private static JPanel EMPTY_CHART = new JPanel();
    // Attributes
    private AppCoordinator coordinator;
    private JScrollPane convergenceScrollPane = new JScrollPane();
    private JScrollPane distributionScrollPane = new JScrollPane();
    private TimeSeries simulationValues;
    private Dimension chartDimension;
    private XYSeries simulationSeries;
    private double[] parValues;
    private int position;
    private double maxParRate;
    private double minParRate;
    private double maxAverage;
    private double minAverage;

    /** Creates a new instance of ChartPanel */
    public SimulationView(AppCoordinator coordinator) {
        this.coordinator = coordinator;
        coordinator.addPricingObserver(this);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        chartDimension = new Dimension(Integer.parseInt(coordinator.getProperty("chart-width")), Integer.parseInt(coordinator.getProperty("chart-height")));
        //setPreferredSize(chartDimension);
        add(convergenceScrollPane, "Convergence");
        add(distributionScrollPane, "Distribution");
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
                "Par Rate",
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
        //rangeAxis.setLowerBound(0.9*minAverage);
        rangeAxis.setLowerBound(0.0);
        rangeAxis.setUpperBound(1.1*maxAverage);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(chartDimension);
        return chartPanel;
     }

    public ChartPanel buildHistogram() {
       HistogramDataset dataset = new HistogramDataset();
       dataset.setType(HistogramType.RELATIVE_FREQUENCY);
       Arrays.sort(parValues);
       int minIndex = (int) Math.floor(0.01*((double)parValues.length));
       int maxIndex = (int) Math.floor(0.99*((double)parValues.length));
       double[] values = new double[maxIndex-minIndex+1];
       for (int i=minIndex;i<=maxIndex;i++) values[i-minIndex] = parValues[i];
       //dataset.addSeries("Par Rate", values, State.NO_HISTOGRAM_BINS, values[0],values[values.length-1]);
       dataset.addSeries("Fair Spread", values, State.NO_HISTOGRAM_BINS);
       System.out.println(0.9*minParRate);
       System.out.println(1.1*maxParRate);

       String plotTitle = "";
       String xaxis = "Fair Spread";
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
        /**
     *  This method displays the chart and data text.
     */
    public void clearChart() {
        convergenceScrollPane.setViewportView(EMPTY_CHART);
     }

    public void correlationUpdated() {

    }

    public void pricingComplete(double price) {
        System.out.println(parValues.length);
        System.out.println(parValues[State.NUMBER_OF_PRICING_NOTIFICATIONS-1]);
        distributionScrollPane.setViewportView(buildHistogram());
        convergenceScrollPane.setViewportView(buildLineChart());
        //this.setSelectedIndex(0);
    }

    public void pricingStarted() {
        //simulationValues.clear();
        int noOfSimulations = coordinator.getSpecs().getSimulationSpecs().getNoOfSimulations();
        notificationStep = noOfSimulations / State.NUMBER_OF_PRICING_NOTIFICATIONS;
        convergenceScrollPane.setViewportView(EMPTY_CHART);
        distributionScrollPane.setViewportView(EMPTY_CHART);
        simulationSeries = new XYSeries("Par Rate");
        maxParRate = Double.NEGATIVE_INFINITY;
        minParRate = Double.POSITIVE_INFINITY;
        maxAverage = Double.NEGATIVE_INFINITY;
        minAverage = Double.POSITIVE_INFINITY;
        parValues = new double[noOfSimulations];
        position = 0;
    }

    private int notificationCount = 0;
    private int notificationStep = 0;
    public void dataPointAdded(int step, double average, double value) {
        notificationCount++;
        if (notificationCount==notificationStep) {
            simulationSeries.add(step, average);
            notificationCount = 0;
            if (average > maxAverage) maxAverage = average;
            if (average < minAverage) minAverage = average;
        }
        if (value > maxParRate) maxParRate = value;
        if (value < minParRate) minParRate = value;
        parValues[position] = value;
        position++;
    }

    public void dataPointAdded(int step, double approximation, double premiumLegValue, double defaultLegValue) {
        notificationCount++;
        if (notificationCount==notificationStep) {
            simulationSeries.add(step, approximation);
            notificationCount = 0;
            if (approximation > maxAverage) maxAverage = approximation;
            if (approximation < minAverage) minAverage = approximation;
        }
        if (defaultLegValue > maxParRate) maxParRate = defaultLegValue;
        if (defaultLegValue < minParRate) minParRate = defaultLegValue;
        parValues[position] = defaultLegValue;
        position++;
    }
}
