/*
 * View.java
 *
 * Created on January 10, 2005, 10:38 AM
 */
package cqf.bcd.ui.specs;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import com.jgoodies.uif_lite.panel.SimpleInternalFrame;
import cqf.bcd.ui.main.AppCoordinator;
import cqf.bcd.specs.ContractSpecs;
import cqf.bcd.specs.SpecsUpdater;
import cqf.core.State.BasisType;
import cqf.core.State.Frequency;
import cqf.ui.util.GUIUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author birksworks
 */
public class ContractSpecsManager extends SimpleInternalFrame implements SpecsUpdater {
    // Attributes

    private AppCoordinator coordinator;
    private ContractSpecs contractSpecs;
    private JTextField notionalField;
    private DateDropdownLists effectiveDateDropdownLists;
    private DateDropdownLists maturityDateDropdownLists;
    private JComboBox premiumAccruedOptionsList;
    private JComboBox basisOptionsList;
    private JComboBox frequencyOptionsList;
    private JTextField kthField;
    private Dimension widgetDimension;

    /** Creates a new instance of View */
    public ContractSpecsManager(AppCoordinator coordinator) {
        super(GUIUtilities.readImageIcon("image/search.png"), "Contract Specifications");
        this.coordinator = coordinator;
        coordinator.addSpecsUpdater(this);
        contractSpecs = coordinator.getSpecs().getContractSpecs();
        widgetDimension = new Dimension(Integer.parseInt(coordinator.getProperty("widget-width")),Integer.parseInt(coordinator.getProperty("widget-height")));
        JPanel contentPanel = new JPanel(new GridLayout(0,2,5,5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Recovery Rate
        contentPanel.add(new JLabel("Notional ($): ", JLabel.RIGHT));
        notionalField = new JTextField(12);
        notionalField.setText("100,000,000");
        notionalField.setToolTipText("Enter the notional amount for the BCD contract here.");
        notionalField.setPreferredSize(widgetDimension);
        contentPanel.add(notionalField);
        // 2. kth to Default
        contentPanel.add(new JLabel("kth to Default: ", JLabel.RIGHT));
        kthField = new JTextField(4);
        kthField.setText("1");
        kthField.setPreferredSize(widgetDimension);
        contentPanel.add(kthField);
        // 5.Basis
        contentPanel.add(new JLabel("Basis: ", JLabel.RIGHT));
        basisOptionsList = new JComboBox(BasisType.values());
        basisOptionsList.setPreferredSize(widgetDimension);
        contentPanel.add(basisOptionsList);
        // 6. Frequency
        contentPanel.add(new JLabel("Frequency: ", JLabel.RIGHT));
        frequencyOptionsList = new JComboBox(Frequency.values());
        frequencyOptionsList.setSelectedIndex(1);
        frequencyOptionsList.setPreferredSize(widgetDimension);
        contentPanel.add(frequencyOptionsList);

        // 4. Premium Accrued
        contentPanel.add(new JLabel("Premium Accrued: ", JLabel.RIGHT));
        JPanel panePremiumAccrued = new JPanel(new FlowLayout(), false);
        String[] premiumAccruedOptions = {"Yes", "No"};
        premiumAccruedOptionsList = new JComboBox(premiumAccruedOptions);
        premiumAccruedOptionsList.setSelectedIndex(0);
        premiumAccruedOptionsList.setPreferredSize(widgetDimension);
        contentPanel.add(premiumAccruedOptionsList);

        JPanel datePanel = new JPanel(new GridLayout(0, 1, 5, 5));
        datePanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        // 2. Effective Date
        JPanel effectiveDatePanel = new JPanel(new GridLayout(1, 4, 5, 5));
        effectiveDatePanel.add(new JLabel("Effective Date: ", JLabel.RIGHT));
        effectiveDateDropdownLists = new DateDropdownLists(
                2010,
                2020,
                2010,
                6,
                20);
        effectiveDatePanel.add(effectiveDateDropdownLists.getMonthComboBox());
        effectiveDatePanel.add(effectiveDateDropdownLists.getDateComboBox());
        effectiveDatePanel.add(effectiveDateDropdownLists.getYearComboBox());
        datePanel.add(effectiveDatePanel);
        // 3 Maturity Date
        JPanel maturityDatePanel = new JPanel(new GridLayout(1, 4, 5, 5));
        maturityDatePanel.add(new JLabel("Maturity Date: ", JLabel.RIGHT));
        maturityDateDropdownLists = new DateDropdownLists(
                2010,
                2020,
                2015,
                9,
                20);
        maturityDatePanel.add(maturityDateDropdownLists.getMonthComboBox());
        maturityDatePanel.add(maturityDateDropdownLists.getDateComboBox());
        maturityDatePanel.add(maturityDateDropdownLists.getYearComboBox());
        datePanel.add(maturityDatePanel);

        add(contentPanel, BorderLayout.CENTER);
        add(datePanel, BorderLayout.SOUTH);
    }
    
    public void updateSpecs() {
        NumberFormat format = NumberFormat.getInstance(Locale.US);
        Number number;
        try {
            number = format.parse(notionalField.getText());
            contractSpecs.setNotional(number.doubleValue());
        } catch (ParseException ex) {
            Logger.getLogger(ContractSpecsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            number = format.parse(kthField.getText());
            contractSpecs.setK(number.intValue());
        } catch (ParseException ex) {
            Logger.getLogger(ContractSpecsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        contractSpecs.setEffectiveDate(effectiveDateDropdownLists.getSelectedCalendar().getTime());
        contractSpecs.setMaturityDate(maturityDateDropdownLists.getSelectedCalendar().getTime());
        if (premiumAccruedOptionsList.getSelectedIndex() == 0) contractSpecs.setPremiumAccrued(true);
        contractSpecs.setBasisType((BasisType) basisOptionsList.getSelectedItem());
        contractSpecs.setFrequency((Frequency) frequencyOptionsList.getSelectedItem());
    }
}
