package cqf.bcd.ui.specs;

import com.jgoodies.uif_lite.component.UIFSplitPane;
import cqf.bcd.ui.main.AppCoordinator;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class SpecsView extends JPanel {

    public SpecsView(AppCoordinator coordinator) {
        setLayout(new BorderLayout());
        ReferenceEntityManager referenceEntityManager = new ReferenceEntityManager(coordinator);
        ContractSpecsManager contractSpecsManager = new ContractSpecsManager(coordinator);
        SimulationSpecsManager simulationSpecsManager = new SimulationSpecsManager(coordinator);

        JSplitPane bottomSplitPane = UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, contractSpecsManager, simulationSpecsManager);
        JSplitPane mainSplitPane = UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, referenceEntityManager, bottomSplitPane);
        mainSplitPane.setDividerLocation(180);
        //bottomSplitPane.setDividerLocation(300);
        add(mainSplitPane, BorderLayout.CENTER);
    }

}
