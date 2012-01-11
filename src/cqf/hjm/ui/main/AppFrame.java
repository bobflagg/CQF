package cqf.hjm.ui.main;

import com.jgoodies.uif_lite.component.UIFSplitPane;
import cqf.hjm.ui.results.ResultsView;
import cqf.hjm.ui.specifications.SpecificationView;
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
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new EmptyBorder(0, 5, 0, 0));
        rightPanel.add(new ResultsView(coordinator), BorderLayout.CENTER);
        JSplitPane innerPanel = UIFSplitPane.createStrippedSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, new SpecificationView(coordinator), rightPanel);
        innerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        innerPanel.setDividerLocation(260);
        innerPanel.setContinuousLayout(true);
        return innerPanel;
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

