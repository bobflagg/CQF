package cqf.hjm.ui.specifications;

import com.jgoodies.uif_lite.component.UIFSplitPane;
import cqf.hjm.ui.main.AppCoordinator;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class SpecificationView extends JPanel {

    public SpecificationView(AppCoordinator coordinator) {
        setLayout(new BorderLayout());
        ProductManager productManager = new ProductManager(coordinator);
        SimulationManager simulationManager = new SimulationManager(coordinator);
        JSplitPane splitPane = UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, productManager, simulationManager);
        JSplitPane flushPane = UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, splitPane, new JPanel());
        splitPane.setDividerLocation(160);
        add(splitPane, BorderLayout.CENTER);
    }
}
