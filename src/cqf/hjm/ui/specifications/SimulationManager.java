package cqf.hjm.ui.specifications;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;
import cqf.core.PricingObserver;
import cqf.core.State;
import cqf.core.State.RandomEngineType;
import cqf.hjm.state.AppState;
import cqf.hjm.state.StateManager;
import cqf.hjm.ui.main.AppCoordinator;
import cqf.status.AppStatus;
import cqf.ui.util.GUIUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class SimulationManager extends SimpleInternalFrame implements StateManager, PricingObserver {
    // Attributes
    private static final String EMPTY_LABEL_TEXT = "               ";
    private AppCoordinator coordinator;
    private AppState state;
    private JComboBox numberOfFactorsList;
    private JComboBox randomEngineList;
    private JComboBox noOfSimulationsList;
    private JTextField numberOfFactorsField;
    private JTextField priceField;
    private AbstractButton priceButton;
    private JComboBox copulaOptionsList;
    JLabel priceLabel = new JLabel(EMPTY_LABEL_TEXT, JLabel.RIGHT);


    /** Creates a new instance of View */
    public SimulationManager(AppCoordinator coordinator) {
        super(GUIUtilities.readImageIcon("image/search.png"), "Simulation Settings");
        this.coordinator = coordinator;
        this.state = coordinator.getState();
        coordinator.addPricingObserver(this);

        JPanel contentPanel = new JPanel(new GridLayout(0, 1));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel theRestPanel = new JPanel(new GridLayout(0, 2));
        theRestPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));


        //GridLayout theRestLayout = new GridLayout(0,2);
        //JPanel theRestPanel = new JPanel(theRestLayout);
        //theRestLayout.setVgap(30);
       // theRestPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        theRestPanel.add(new JLabel("# of Factors: ", JLabel.RIGHT));

        Integer[] numberOfFactorsOptions = {1,2,3,4,5,6,7,8,9,10};
        numberOfFactorsList = new JComboBox(numberOfFactorsOptions);
        numberOfFactorsList.setSelectedItem(2);
        theRestPanel.add(numberOfFactorsList);

        theRestPanel.add(new JLabel("# of Simulations: ", JLabel.RIGHT));
        noOfSimulationsList = new  JComboBox(State.NUMBER_OF_SIMULATIONS_LIST);
        theRestPanel.add(noOfSimulationsList);

        contentPanel.add(theRestPanel);
        add(contentPanel, BorderLayout.CENTER);
        add(buildButtonPanel(coordinator), BorderLayout.SOUTH);

        theRestPanel.add(new JLabel("Random Engine: ", JLabel.RIGHT));
        randomEngineList = new JComboBox(RandomEngineType.values());
        randomEngineList.setSelectedItem(0);
        theRestPanel.add(randomEngineList);

        theRestPanel.add(new JLabel("Product Price ($): ", JLabel.RIGHT));
        priceField = new JTextField(10);
        priceField.setEditable(false);
        theRestPanel.add(priceField);
    }

    protected JComponent buildButtonPanel(final AppCoordinator coordinator) {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // clear selected
        priceButton = GUIUtilities.createButton("run.png", "Price");
        priceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                priceButton.setEnabled(false);
                priceField.setText("");
                priceLabel.setText(EMPTY_LABEL_TEXT);
                state.refresh();
                state.noOfFactors = (Integer) numberOfFactorsList.getSelectedItem();
                state.noOfSimulations = (Integer)noOfSimulationsList.getSelectedItem();
                state.engine = (RandomEngineType) randomEngineList.getSelectedItem();
                NumberFormat format = NumberFormat.getInstance(Locale.US);
                Number number;
                try {
                    coordinator.price();
                } catch (Exception ex) {
                    Logger.getLogger(SimulationManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                String statusMessage = "Pricing "+state.productName()+".  Please wait...";
                AppStatus.instance().updateStatus(true, false, statusMessage);
             }
        });
        priceButton.setEnabled(true);
        Dimension widgetDimension = new Dimension(Integer.parseInt(coordinator.getProperty("widget-width")),
                Integer.parseInt(coordinator.getProperty("widget-height")));
        priceButton.setPreferredSize(widgetDimension);
        buttonPanel.add(priceButton);
        return buttonPanel;
    }

    public void refreshState() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void pricingComplete(double price) {
        priceField.setText(coordinator.formatValue(price));
        String statusMessage = "Pricing complete. The "+state.productName()+" price is "+"$"+coordinator.formatValue(price)+".";
        AppStatus.instance().updateStatus(false, false, statusMessage);
        priceButton.setEnabled(true);
    }

    public void pricingStarted() {
        String statusMessage = "Pricing "+state.productName()+".  Please wait...";
        AppStatus.instance().updateStatus(true, false, statusMessage);
        priceButton.setEnabled(false);
    }

    public void dataPointAdded(int step, double average, double value) {
    }
}
