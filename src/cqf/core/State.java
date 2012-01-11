package cqf.core;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class State {
    public static final int NUMBER_OF_PRICING_NOTIFICATIONS = 100;
    public static final int NO_HISTOGRAM_BINS = 100;
    public static final Integer[] NUMBER_OF_SIMULATIONS_LIST = new Integer[] {500, 1000, 2000, 5000, 10000};
    public static final Integer[] NUMBER_OF_SIMULATIONS_LIST_LARGE = new Integer[] {5000, 10000, 20000, 50000, 100000, 200000};

    public enum SimulationSteps {
        s5k(5000),
        s10k(10000),
        s20k(20000),
        s50k(50000),
        s100k(100000),
        s200k(200000),
        s500k(500000),
        s1m(1000000);
        private int noOfSteps;

        private SimulationSteps(int noOfSteps) {
            this.noOfSteps = noOfSteps;
        }

        public int getNoOfSteps() {
            return noOfSteps;
        }

        @Override
        public String toString() {
            return INTEGER_DATA_FORMAT.format(noOfSteps);
        }
    }

    public static final Locale DEFAULT_LOCALE = Locale.US;
    public static final NumberFormat RATE_NUMBER_FORMAT = NumberFormat.getInstance(DEFAULT_LOCALE);
    public static final NumberFormat VIEW_NUMBER_FORMAT = NumberFormat.getInstance(DEFAULT_LOCALE);
    public static final NumberFormat INTEGER_NUMBER_FORMAT = NumberFormat.getInstance(DEFAULT_LOCALE);
    public static DecimalFormat RATE_DATA_FORMAT = null;
    public static DecimalFormat VIEW_DATA_FORMAT = null;
    public static DecimalFormat INTEGER_DATA_FORMAT = null;
    static {
        // get a NumberFormat object and cast it to
        // a DecimalFormat object
        try {
            RATE_DATA_FORMAT = (DecimalFormat) RATE_NUMBER_FORMAT;
            RATE_DATA_FORMAT.applyPattern("##.0000");
            VIEW_DATA_FORMAT = (DecimalFormat) VIEW_NUMBER_FORMAT;
            VIEW_DATA_FORMAT.applyPattern("#0.0000");
            INTEGER_DATA_FORMAT = (DecimalFormat) INTEGER_NUMBER_FORMAT;
            INTEGER_DATA_FORMAT.applyPattern("#,###,###");
        } catch (ClassCastException e) {
            System.err.println(e);
        }
    }

    public static String formatRate(double rate) {
        return RATE_NUMBER_FORMAT.format(rate);
    }

    public static String formatValue(double value) {
        return VIEW_NUMBER_FORMAT.format(value);
    }

    public enum BasisType {
        ACT360, ACTACT, HIST, AFB, Thirty360("30360"), ThirtyE360("30E360"), ACT365;
        private final String name;
        private BasisType() {name = null;}
        private BasisType(String name) {
            this.name = name;
        }
        @Override
        public String toString() {
            if (this.name == null) return super.toString();
            return name;
        }
    }
    public enum CopulaType {Gaussian, StudentT}
    public enum RandomEngineType {DRand, MersenneTwister}
    public enum Frequency {
        Monthly(0.08333333),
        Quarterly(0.25),
        Semiannually(0.5),
        Yearly(1.0);
        private double delta;

        private Frequency(double delta) {
            this.delta = delta;
        }

        public double getDelta() {
            return delta;
        }
        

    }
}
