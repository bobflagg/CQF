package cqf.interest;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class SimpleDiscounter extends Discounter {
    protected int noOfPeriods;
    protected double[] discounts;
    protected double[] rates;
    protected double[] tenor;

    public SimpleDiscounter(double[] tenor, double[] discounts) {
        noOfPeriods = tenor.length;
        this.discounts = discounts;
        this.tenor = tenor;
        rates = new double[noOfPeriods];
        for (int i = 0; i < noOfPeriods; i++) {
            rates[i] = -Math.log(discounts[i])/tenor[i];
        }
    }
    
    @Override
    public double discountFactor(double time) {
        double discount = 1.0, t=0.0;
        for (int i = 0; i < noOfPeriods; i++) {
            if (time <= tenor[i]) return discount+(time-t)*(discounts[i]-discount)/(tenor[i]-t);
            discount = discounts[i];
            t = tenor[i];
        }
        return discounts[noOfPeriods-1];
    }

    /*
    @Override
    public double discountFactor(double time) {
        double r = 0.0, t=0.0;
        for (int i = 0; i < noOfPeriods; i++) {
            if (time <= tenor[i]) {
                double rate = r+(time-t)*(rates[i]-r)/(tenor[i]-t);
                return Math.exp(-rate*time);
            }
            r = rates[i];
            t = tenor[i];
        }
        return Math.exp(-rates[noOfPeriods-1]*time);
    }
    */
}
