package cqf.bcd.ui.results;

import cqf.copula.Copula;
import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 *
 * @author birksworks
 */
public abstract class ParameterDisplayer extends JPanel {


    public abstract void displayParameters(Copula copula, String[] headers);

}
