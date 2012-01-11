package cqf.bcd.ui.specs;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;
import cqf.bcd.pricing.PricingObserver;
import cqf.bcd.ui.main.AppCoordinator;
import cqf.bcd.specs.SimulationSpecs;
import cqf.bcd.specs.SpecsUpdater;
import cqf.core.State;
import cqf.core.State.CopulaType;
import cqf.core.State.RandomEngineType;
import cqf.core.State.SimulationSteps;
import cqf.ui.util.GUIUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
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
public class SimulationSpecsManager extends SimpleInternalFrame implements SpecsUpdater, PricingObserver {
    // Attributes

    private AppCoordinator coordinator;
    private SimulationSpecs simulationSpecs;
    private JTextField recoveryRateField;
    private JComboBox noOfSimulationsList;
    private AbstractButton priceButton;
    private AbstractButton calibrateButton;
    private JComboBox copulaOptionsList;
    private JComboBox randomEngineList;
    Dimension widgetDimension;
    private final JTextField fairSpreadField = null;

    /** Creates a new instance of View */
    public SimulationSpecsManager(AppCoordinator coordinator) {
        super(GUIUtilities.readImageIcon("image/run.png"), "Pricing Simulation Parameters");
        widgetDimension = new Dimension(Integer.parseInt(coordinator.getProperty("widget-width")),Integer.parseInt(coordinator.getProperty("widget-height")));
        this.coordinator = coordinator;
        coordinator.addSpecsUpdater(this);
        coordinator.addPricingObserver(this);
        simulationSpecs = coordinator.getSpecs().getSimulationSpecs();
        JPanel contentPanel = new JPanel(new GridLayout(0,2,5,5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Recovery Rate
        contentPanel.add(new JLabel("Recovery Rate: ", JLabel.RIGHT));
        recoveryRateField = new JTextField(6);
        recoveryRateField.setText("0.40");
        recoveryRateField.setToolTipText("Enter the recovery for the BCD pricer here.");
        recoveryRateField.setPreferredSize(widgetDimension);
        contentPanel.add(recoveryRateField);

        // Number of simulations
        contentPanel.add(new JLabel("# of Simulations: ", JLabel.RIGHT));
        noOfSimulationsList = new  JComboBox(State.SimulationSteps.values());
        noOfSimulationsList.setToolTipText("Enter the number of simulations for the BCD pricer here.");
        noOfSimulationsList.setSelectedItem(State.SimulationSteps.s100k);
        noOfSimulationsList.setPreferredSize(widgetDimension);
        contentPanel.add(noOfSimulationsList);

        // Number of simulations
        contentPanel.add(new JLabel("Copula: ", JLabel.RIGHT));
        copulaOptionsList = new JComboBox(CopulaType.values());
        copulaOptionsList.setToolTipText("Enter the type of copula for the BCD pricer here.");
        copulaOptionsList.setPreferredSize(widgetDimension);
        contentPanel.add(copulaOptionsList);

        contentPanel.add(new JLabel("Random Engine: ", JLabel.RIGHT));
        randomEngineList = new JComboBox(RandomEngineType.values());
        randomEngineList.setSelectedItem(0);
        randomEngineList.setPreferredSize(widgetDimension);
        contentPanel.add(randomEngineList);

        //contentPanel.add(new JLabel("Fair Spread (bps): ", JLabel.RIGHT));
        //fairSpreadField = new JTextField(10);
        //fairSpreadField.setEditable(false);
        //contentPanel.add(fairSpreadField);

        add(contentPanel, BorderLayout.CENTER);
        add(buildButtonPanel(coordinator), BorderLayout.SOUTH);

    }

    protected JComponent buildButtonPanel(final AppCoordinator coordinator) {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // Calibrate
        calibrateButton = GUIUtilities.createButton("run.png", "Calibrate");
        calibrateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    coordinator.callibrate();
                } catch (Exception ex) {
                    Logger.getLogger(SimulationSpecsManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        calibrateButton.setPreferredSize(widgetDimension);
        buttonPanel.add(calibrateButton);

        priceButton = GUIUtilities.createButton("run.png", "Price");
        priceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    coordinator.price();
                } catch (Exception ex) {
                    Logger.getLogger(SimulationSpecsManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        priceButton.setEnabled(true);
        priceButton.setPreferredSize(widgetDimension);
        buttonPanel.add(priceButton);
        return buttonPanel;
    }

    public void updateSpecs() {
        NumberFormat format = NumberFormat.getInstance(Locale.US);
        Number number;
        try {
            number = format.parse(recoveryRateField.getText());
            simulationSpecs.setRecoveryRate(number.doubleValue());
        } catch (ParseException ex) {
            Logger.getLogger(SimulationSpecsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        simulationSpecs.setNoOfSimulations(((SimulationSteps)noOfSimulationsList.getSelectedItem()).getNoOfSteps());
        simulationSpecs.setCopulaType((CopulaType) copulaOptionsList.getSelectedItem());
        simulationSpecs.setEngineType((RandomEngineType) randomEngineList.getSelectedItem());
    }

    public void pricingComplete(double price) {
        //fairSpreadField.setText(coordinator.formatRate(price));
        calibrateButton.setEnabled(true);
        priceButton.setEnabled(true);
    }

    public void pricingStarted() {
        //fairSpreadField.setText("");
        calibrateButton.setEnabled(false);
        priceButton.setEnabled(false);
    }

    public void dataPointAdded(int step, double premiumLegAverage, double defaultLegAverage, double value) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
