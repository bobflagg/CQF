package cqf.hjm.model;

import org.ujmp.core.Matrix;

/**
 * Information holder containing an eigenvalue and corresponding eigenvector.
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class EigenData implements Comparable<EigenData> {
    private double eigenvalue;
    private Matrix eigenVector;

    public EigenData(double eigenvalue, Matrix eigenVector) {
        this.eigenvalue = eigenvalue;
        this.eigenVector = eigenVector;
    }

    public Matrix getEigenVector() {
        return eigenVector;
    }

    public double getEigenvalue() {
        return eigenvalue;
    }

    public int compareTo(EigenData o) {
        if (eigenvalue < o.eigenvalue) return 1;
        if (eigenvalue > o.eigenvalue) return -1;
        return 0;
    }

    public String toString() {
        return eigenvalue+"\n"+eigenVector.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EigenData other = (EigenData) obj;
        if (this.eigenvalue != other.eigenvalue) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.eigenvalue) ^ (Double.doubleToLongBits(this.eigenvalue) >>> 32));
        return hash;
    }

}
