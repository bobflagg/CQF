package cqf.reference;

import au.com.bytecode.opencsv.CSVReader;
import cqf.distribution.Distribution;
import cqf.distribution.StepwiseHazardDistribution;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class Entity {
    /* *************************************************************************
     * Static elements                                                         *
     ************************************************************************ */
    private static Map<String, Entity> registry = new HashMap<String, Entity>();

    private static void register(Entity entity) {
        if (registry.containsValue(entity)) return;
        registry.put(entity.getTicker(), entity);
    }

    public static Entity lookup(String ticker) throws IllegalArgumentException {
        Entity entity = registry.get(ticker);
        if (entity == null) throw new IllegalArgumentException("Can't find entity for ticker "+ticker);
        return entity;
    }
    // build survival distributions
    private static final double[] DEFAULT_TENOR = {0.5, 1.0, 2.0, 3.0, 5.0, 7.0, 10.0};
    private static final double[] DEFAULT_ALPHA = {0.016832, 0.020203, 0.021950, 0.030838, 0.028126, 0.032054, 0.030386};
    /* *************************************************************************
     * Instance elements                                                       *
     ************************************************************************ */
    private String ticker;
    private String name;
    private Sector sector;
    private Distribution survivalDistribution;

    public Distribution getSurvivalDistribution() {
        if (survivalDistribution == null) calibrate();
        return survivalDistribution;
    }

    public void calibrate() {
        if (survivalDistribution != null) return;
        double[] tenor;
        double[] alpha;
        String dataFilePath = "/data/survival"+ticker+".csv";
        InputStream in = getClass().getResourceAsStream(dataFilePath);
        if (in != null) {
            CSVReader reader = new CSVReader(new InputStreamReader(in));
            List<Double> tenorList = new ArrayList<Double>();
            List<Double> alphaList = new ArrayList<Double>();
            String[] nextLine;
            try {
                while ((nextLine = reader.readNext()) != null) {
                    tenorList.add(Double.parseDouble(nextLine[0]));
                    alphaList.add(Double.parseDouble(nextLine[1]));
                }
                int n = tenorList.size();
                tenor = new double[n];
                alpha = new double[n];
                for (int i=0;i<n;i++) {
                    tenor[i] = tenorList.get(i).doubleValue();
                    alpha[i] = alphaList.get(i).doubleValue();
                }
            } catch (IOException ex) {
                    tenor = DEFAULT_TENOR;
                    alpha = DEFAULT_ALPHA;
            }
        } else {
            tenor = DEFAULT_TENOR;
            alpha = DEFAULT_ALPHA;
        }
        survivalDistribution = new StepwiseHazardDistribution(tenor, alpha);
    }

    public Entity(String ticker, String name, Sector sector) {
        this.ticker = ticker;
        this.name = name;
        this.sector = sector;
        register(this);
    }

    public void priceData(double[][] data, int position) throws Exception {
        //ArrayList<Double> data = new ArrayList<Double>();
        String dataFilePath = "/data/price"+ticker+".csv";
        InputStream in = getClass().getResourceAsStream(dataFilePath);
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(in));
            List<String[]> dataList = reader.readAll();
            Iterator<String[]> iterator = dataList.iterator();
            int i = 0;
            while(iterator.hasNext()) {
                data[position][i] = Double.parseDouble(iterator.next()[0]);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Price data not available for "+ticker);
        }
    }

    public String getName() {
        return name;
    }

    public Sector getSector() {
        return sector;
    }

    public String getTicker() {
        return ticker;
    }

    @Override
    public String toString() {
        return name+" ["+ticker+"]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Entity other = (Entity) obj;
        if ((this.ticker == null) ? (other.ticker != null) : !this.ticker.equals(other.ticker)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (this.ticker != null ? this.ticker.hashCode() : 0);
        return hash;
    }
}
