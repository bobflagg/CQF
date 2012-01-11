package cqf.hjm.product;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class Cap extends Product {
    private double principal;
    private double fixedRate;
    private double delta;
    private double[] tenor;
    private int noOfPayments;

    public Cap(double principal, double maturity, double fixedRate, double delta) {
        super(maturity);
        this.principal = principal;
        this.fixedRate = fixedRate;
        this.delta = delta;
        noOfPayments = (int) Math.ceil(maturity/delta);
        tenor = new double[noOfPayments];
        for (int i=0;i<noOfPayments;i++) {
            tenor[i] = (i+1)*delta;
        }
    }

    @Override
    public double cashflow(double floatingRate, double lowerLimit, double upperLimit) {
        for (int i=0;i<noOfPayments;i++) {
            if (lowerLimit<tenor[i] && tenor[i]<=upperLimit && floatingRate>fixedRate)
                return (floatingRate-fixedRate)*delta*principal;
        }
        return 0.0;
    }

}
