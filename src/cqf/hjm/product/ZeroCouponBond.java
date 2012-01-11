package cqf.hjm.product;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class ZeroCouponBond extends Product {
    private double principal;

    public ZeroCouponBond(double principal, double maturity) {
        super(maturity);
        this.principal = principal;
    }

    @Override
    public double cashflow(double shortRate, double lowerLimit, double upperLimit) {
        if (lowerLimit<maturity && maturity<=upperLimit) return principal;
        return 0.0;
    }

}
