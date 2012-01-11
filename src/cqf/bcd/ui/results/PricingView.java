package cqf.bcd.ui.results;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;
import cqf.bcd.pricing.PricingObserver;
import cqf.bcd.ui.main.AppCoordinator;
import cqf.core.State;
import cqf.ui.util.GUIUtilities;
import java.awt.BorderLayout;
import javax.swing.JLabel;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class PricingView extends SimpleInternalFrame implements PricingObserver {
    protected JLabel descriptionLabel = new JLabel("                                                                  ", JLabel.LEFT);

        /** Creates a new instance of CriteriaView */
    public PricingView(AppCoordinator coordinator) {
        super(GUIUtilities.readImageIcon("image/check_selected.gif"), "Pricing Results");
        coordinator.addPricingObserver(this);
        add(descriptionLabel, BorderLayout.CENTER);
    }

    public void pricingComplete(double price) {
        descriptionLabel.setText("  Fair Spread (bps): "+State.formatValue(price));
    }

    public void pricingStarted() {
        descriptionLabel.setText("");
    }

    public void dataPointAdded(int step, double premiumLegAverage, double defaultLegAverage, double value) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

}
