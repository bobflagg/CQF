package cqf.rv;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class ChiSquareRandomVariable extends RandomVariable {
    private double nu;

    public ChiSquareRandomVariable(double nu) {
        this.nu = nu;
    }

    @Override
    public double sample() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
