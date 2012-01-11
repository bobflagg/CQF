package cqf.rv;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class NormalRandomVariable extends RandomVariable {
    private double mean, standardDeviation;

    public NormalRandomVariable(double mean, double standardDeviation) {
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }

    public NormalRandomVariable() {
        this(0,1);
    }

    @Override
    public double sample() {
        return mean+standardDeviation*random.nextGaussian();
    }


    /**
     * <p>Returns the cdf of the standard normal distribution evaluated at
     * the given argument. See:
     *      Hart, J.F. et al, 'Computer Approximations', Wiley 1968
     *
     * @param   z   real value at which to compute cdf.
     */
    public static double Phi(double z) {

        double zabs;
        double p;
        double expntl, pdf;

        final double p0 = 220.2068679123761;
        final double p1 = 221.2135961699311;
        final double p2 = 112.0792914978709;
        final double p3 = 33.91286607838300;
        final double p4 = 6.373962203531650;
        final double p5 = .7003830644436881;
        final double p6 = .3526249659989109E-01;

        final double q0 = 440.4137358247522;
        final double q1 = 793.8265125199484;
        final double q2 = 637.3336333788311;
        final double q3 = 296.5642487796737;
        final double q4 = 86.78073220294608;
        final double q5 = 16.06417757920695;
        final double q6 = 1.755667163182642;
        final double q7 = .8838834764831844E-1;

        final double cutoff = 7.071;
        final double root2pi = 2.506628274631001;

        zabs = Math.abs(z);

        //  |z| > 37
        if (z > 37.0) return 1.0;
        if (z < -37.0) return 0.0;

        //  |z| <= 37.
        expntl = Math.exp(-.5 * zabs * zabs);
        pdf = expntl / root2pi;
        //  |z| < cutoff = 10/sqrt(2).
        if (zabs < cutoff) {

            p = expntl * ((((((p6 * zabs + p5) * zabs + p4) * zabs + p3) * zabs
                    + p2) * zabs + p1) * zabs + p0) / (((((((q7 * zabs + q6) * zabs
                    + q5) * zabs + q4) * zabs + q3) * zabs + q2) * zabs + q1) * zabs
                    + q0);

        } else p = pdf / (zabs + 1.0 / (zabs + 2.0 / (zabs + 3.0 / (zabs + 4.0/ (zabs + 0.65)))));
        if (z < 0.0) return p;
        else return 1.0 - p;
    }

    /**
     * <p>Returns the Inverse of the cdf of the standard normal distribution at
     * the given argument. See: (Peter J. Acklam)
     * http://www.math.uio.no/~jacklam/notes/invnorm.</p>
     *
     * <p>Adapted from: Peter Jaeckel, "Monte Carlom Methods in Finance",
     * Wiley, ISBN 0-471-497-41-X</p>
     *
     * @param x must be in the interval (0,1).
     */
    public static double PhiInverse(double x) {
        final double SQRT_TWO_PI = 2.5066282746310;

        // Coefficients for the rational approximation.
        final double e_1 = -3.969683028665376e+01,
                e_2 = 2.209460984245205e+02,
                e_3 = -2.759285104469687e+02,
                e_4 = 1.383577518672690e+02,
                e_5 = -3.066479806614716e+01,
                e_6 = 2.506628277459239e+00;

        final double f_1 = -5.447609879822406e+01,
                f_2 = 1.615858368580409e+02,
                f_3 = -1.556989798598866e+02,
                f_4 = 6.680131188771972e+01,
                f_5 = -1.328068155288572e+01;

        final double g_1 = -7.784894002430293e-03,
                g_2 = -3.223964580411365e-01,
                g_3 = -2.400758277161838e+00,
                g_4 = -2.549732539343734e+00,
                g_5 = 4.374664141464968e+00,
                g_6 = 2.938163982698783e+00;

        final double h_1 = 7.784695709041462e-03,
                h_2 = 3.224671290700398e-01,
                h_3 = 2.445134137142996e+00,
                h_4 = 3.754408661907416e+00;

        // Limits of the approximation region: ]-oo,x_l[, [x_l,x_u], ]x_u,+oo[
        final double x_l = 0.02425, x_u = 1.0 - x_l;

        double z, r;

        // Lower region: 0 < x < x_l
        if (x < x_l) {
            z = Math.sqrt(-2.0 * Math.log(x));
            z = (((((g_1 * z + g_2) * z + g_3) * z + g_4) * z + g_5) * z + g_6)
                    / ((((h_1 * z + h_2) * z + h_3) * z + h_4) * z + 1.0);
        } else if (x <= x_u) { // Central region: x_l <= x <= x_u

            z = x - 0.5;
            r = z * z;
            z = (((((e_1 * r + e_2) * r + e_3) * r + e_4) * r + e_5) * r + e_6) * z
                    / (((((f_1 * r + f_2) * r + f_3) * r + f_4) * r + f_5) * r + 1.0);
        } else { // Upper region. ( x_u < x < 1 )

            z = Math.sqrt(-2.0 * Math.log(1.0 - x));
            z = -(((((g_1 * z + g_2) * z + g_3) * z + g_4) * z + g_5) * z + g_6)
                    / ((((h_1 * z + h_2) * z + h_3) * z + h_4) * z + 1.0);
        }

        // Now |relative error| < 1.15e-9.  One iteration of Halley's third
        // order zero finder gives full machine precision:
        /*
            r = (N(z) - x) * SQRT_TWO_PI * exp( 0.5 * z * z );	//	f(z)/df(z)
            z -= r/(1+0.5*z*r);
        */
        return z;

    }
}
