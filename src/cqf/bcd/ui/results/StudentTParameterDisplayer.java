package cqf.bcd.ui.results;

import cqf.copula.Copula;
import cqf.copula.StudentTCopula;
import cqf.core.State;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class StudentTParameterDisplayer extends GaussianParameterDisplayer {
    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);
    public static DecimalFormat DATA_FORMAT = null;
    static {
        // get a NumberFormat object and cast it to
        // a DecimalFormat object
        try {
            DATA_FORMAT = (DecimalFormat) NUMBER_FORMAT;
            DATA_FORMAT.applyPattern("##.00");
        } catch (ClassCastException e) {
            System.err.println(e);
        }
    }

    @Override
    public void displayParameters(Copula copula, String[] headers) {
        double[][] correlationData = ((StudentTCopula)copula).getCovariance();
        displayCorrelationMatrix(correlationData, headers);
        descriptionLabel.setText("Student T copula. Degrees of freedom =  "+ DATA_FORMAT.format(((StudentTCopula)copula).getNu())+"; Correlation matrix shown below.");
    }

    public StudentTParameterDisplayer() {
        //add(dfLabel, BorderLayout.SOUTH);
    }
}

