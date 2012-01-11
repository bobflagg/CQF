package cqf.hjm.ui.results;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;
import cqf.hjm.ui.main.AppCoordinator;
import cqf.ui.util.GUIUtilities;
import java.awt.BorderLayout;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class ResultsView extends SimpleInternalFrame {

    public ResultsView(AppCoordinator coordinator) {
        super(GUIUtilities.readImageIcon("image/spreadsheet.png"), "Callibration and Simulation Results");
        add(new ResultsManager(coordinator), BorderLayout.CENTER);
    }
}
