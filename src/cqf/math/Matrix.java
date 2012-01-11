package cqf.math;

/**
 * Some static methods for matrix related computations.
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class Matrix {

    /** Computes the product of a matrix and a vector.
     *
     * @param   A   a matrix represented as a double[][]
     * @param   x   a vector represented as a double[]
     */
    public static double[] multiply(double[][] A, double[] x) throws IllegalArgumentException {
        int m = A[0].length;
        if (m != x.length)
            throw new IllegalArgumentException("multiply requires arguments have appropriate dimensions.");
        double[] y = new double[m];

        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < m; j++) y[i] += A[i][j]*x[j];
        }
        return y;
    } //end Cholesky

    /** <p>Computes a Cholesky factorization for a symmetric positive definite
     * matrix.</p>
     *
     * @param   C   a symmetric positive definite matrix to be factored
     * @param   L   a matrix to store the Cholesky factorization of C
     */
    public static void CholeskyFactorization(double[][] C, double[][] L) throws IllegalArgumentException {
        int n = C[0].length;    // dimension
        double S;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) // lower triangular
            {
                S = 0;
                for (int k = 0; k < j; k++) {
                    S += L[i][k] * L[j][k];
                }
                //now S=sum_{k=0}^{j-1}L_ik*L_jk=r_i(L)r_j(L)-L_ij*L_jj,
                //and so S+L_ij*L_jj=r_i(L)r_j(L)=C[i][j]

                if (j < i) { // L[j][j] already computed, compute L[i][j]
                    L[i][j] = (C[i][j] - S) / L[j][j];
                } else { // j=i and so S+L^2_jj=C[j][j]
                    if (C[j][j] - S <= 0) {
                        throw new IllegalArgumentException("CholeskyFactorization requires a symmetric positive definite matrix.");
                    }
                    L[j][j] = Math.sqrt(C[j][j] - S);
                }

            } //end for i
        }
    }
}
