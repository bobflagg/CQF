package cqf.bcd.ui.results;

import cqf.copula.Copula;
import cqf.copula.GaussianCopula;
import cqf.ui.table.JScrollPaneAdjuster;
import cqf.ui.table.JTableRowHeaderResizer;
import cqf.ui.table.RowHeaderRenderer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class GaussianParameterDisplayer extends ParameterDisplayer {
    protected JScrollPane correlationMatrixScrollPane = new JScrollPane();
//                                                   "Gaussian Copula with the following correlation matrix."
    protected JLabel descriptionLabel = new JLabel("                                                                  ", JLabel.LEFT);

    public GaussianParameterDisplayer() {
        setLayout(new BorderLayout());
        add(correlationMatrixScrollPane,BorderLayout.CENTER);
        //descriptionLabel.setFont(new Font("Serif", Font.BOLD, 10));
        add(descriptionLabel, BorderLayout.NORTH);
    }

    public void displayParameters(Copula copula, String[] headers) {
        double[][] correlationData = ((GaussianCopula)copula).getCovariance();
        displayCorrelationMatrix(correlationData, headers);
        descriptionLabel.setText("Gaussian Copula.  Correlation matrix shown below.");
    }

    protected void displayCorrelationMatrix(double[][] correlationData, String[] headers) {
        int n = headers.length;
        DefaultTableModel headerData = new DefaultTableModel(0, 1);
        DefaultTableModel data = new DefaultTableModel(headers, 0);

        for (int i = 0; i < n; i++) {
            headerData.addRow(new Object[]{headers[i]});
            Vector v = new Vector();

            for (int j = 0; j < n; j++) {
                v.add(new Double(correlationData[i][j]));
            }

            data.addRow(v);
        }
        JTable table = new JTable(data);
        JTable rowHeader = new JTable(headerData);
        LookAndFeel.installColorsAndFont(rowHeader, "TableHeader.background", "TableHeader.foreground", "TableHeader.font");
        rowHeader.setIntercellSpacing(new Dimension(0, 0));
        Dimension d = rowHeader.getPreferredScrollableViewportSize();
        d.width = rowHeader.getPreferredSize().width;
        rowHeader.setPreferredScrollableViewportSize(d);
        rowHeader.setRowHeight(table.getRowHeight());
        rowHeader.setDefaultRenderer(Object.class, new RowHeaderRenderer());

        correlationMatrixScrollPane.setViewportView(table);
        correlationMatrixScrollPane.setRowHeaderView(rowHeader);

        JTableHeader corner = rowHeader.getTableHeader();
        corner.setReorderingAllowed(false);
        corner.setResizingAllowed(false);
        correlationMatrixScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, corner);
        new JScrollPaneAdjuster(correlationMatrixScrollPane);
        new JTableRowHeaderResizer(correlationMatrixScrollPane).setEnabled(true);
    }
}
