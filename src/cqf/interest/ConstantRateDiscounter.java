package cqf.interest;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class ConstantRateDiscounter extends Discounter {
    private double rate;

    public ConstantRateDiscounter(double rate) {
        this.rate = rate/100.0;
    }

    @Override
    public double discountFactor(double time) {
        return Math.exp(-time*rate);
    }

}
