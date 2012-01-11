package cqf.bcd.ui.main;

import com.jgoodies.uif_lite.component.UIFSplitPane;
import com.jgoodies.uif_lite.panel.SimpleInternalFrame;
import cqf.bcd.ui.results.SimulationView;
import cqf.bcd.ui.specs.SpecsView;
import cqf.bcd.ui.results.CalibrationView;
import cqf.bcd.ui.results.PricingView;
import cqf.status.AppStatus;
import cqf.ui.UIConfigurator;
import cqf.ui.status.StatusBar;
import cqf.ui.util.GUIUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
/**
 *
 * @author  oracle
 */
public class AppFrame extends JFrame {

    // Attributes
    private AppCoordinator coordinator;
    private UIConfigurator configurator;
    /**
     * Constructs a <code>AppView</code>, configures the UI,
     * and builds the content.
     */
    public AppFrame(final AppCoordinator coordinator, UIConfigurator configurator) {
        this.coordinator = coordinator;
        this.configurator = configurator;
        build();
        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(
            new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    coordinator.exit();
                }
            });
        setSize(new Dimension(Integer.parseInt(coordinator.getProperty("window-width")), Integer.parseInt(coordinator.getProperty("window-height"))));
        GUIUtilities.locateOnScreen(this);
        //AppStatus.instance().showMessage("Ready to go....");
        // will only show this after successful login
        setVisible(false);
    }

    /**
     * Builds the <code>AppView</code> using Options from the Launcher.
     */
    private void build() {
        setContentPane(buildContentPane());
        setTitle(getWindowTitle());
        setIconImage(GUIUtilities.readImageIcon(coordinator.getProperty("small-logo")).getImage());
        //setJMenuBar(new AppMenuBar(coordinator, configurator));
    }

    /**
     * Builds and returns the content.
     */
    private JComponent buildContentPane() {
        JPanel appPanel = new JPanel(new BorderLayout());
        //toolBar = new AppToolBar(coordinator, configurator);
        //appPanel.add(toolBar, BorderLayout.NORTH);
        appPanel.add(buildMainPanel(), BorderLayout.CENTER);
        appPanel.add(buildStatusBar(), BorderLayout.SOUTH);
        return appPanel;
    }

    /**
     * Builds and answers the tabbed pane.
     */
    private Component buildMainPanel() {
        JPanel callibrationPanel = new SimpleInternalFrame(GUIUtilities.readImageIcon("image/spreadsheet.png"), "Callibration Results");
        callibrationPanel.add(new CalibrationView(coordinator), BorderLayout.CENTER);
        JPanel simulationPanel = new SimpleInternalFrame(GUIUtilities.readImageIcon("image/spreadsheet.png"), "Simulation Results");
        simulationPanel.add(new SimulationView(coordinator), BorderLayout.CENTER);
        JSplitPane rightBottomPane = UIFSplitPane.createStrippedSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                callibrationPanel,
                simulationPanel
        );
        JSplitPane rightPane = UIFSplitPane.createStrippedSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            new PricingView(coordinator),
            rightBottomPane
        );
        rightPane.setDividerLocation(60);
        rightBottomPane.setDividerLocation(300);
        JSplitPane mainePane = UIFSplitPane.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT, new SpecsView(coordinator), rightPane);
        mainePane.setBorder(new EmptyBorder(10, 10, 10, 10));
        //innerPanel.setDividerLocation(80);
        mainePane.setContinuousLayout(true);
        return mainePane;
    }

    /**
     * Builds and returns the status pane.
     */
    private Component buildStatusBar() {
        StatusBar statusBar = new StatusBar(
                GUIUtilities.readImageIcon(coordinator.getProperty("status-working-image")),
                GUIUtilities.readImageIcon(coordinator.getProperty("status-warning-image")),
                GUIUtilities.readImageIcon(coordinator.getProperty("status-idle-image")),
                configurator.getPreferredSize().width);
        AppStatus.instance().addObserver(statusBar);
        return statusBar;
    }

    protected String getWindowTitle() {
        return coordinator.getProperty("window-title")
                + " (Version " + coordinator.getProperty("app-version") + ") ";
    }
}

