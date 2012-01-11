package cqf.hjm.product;

/**
 *
 * @author birksworks
 */
public abstract class Product {
    protected double maturity;

    public Product(double maturity) {
        this.maturity = maturity;
    }

    public double getMaturity() {
        return maturity;
    }

    public double initialCashflow() {
        return 0.0;
    }
    public abstract double cashflow(double shortRate, double lowerLimit, double upperLimit);


}
