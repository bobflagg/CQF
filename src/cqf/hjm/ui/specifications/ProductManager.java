package cqf.hjm.ui.specifications;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;
import cqf.core.State.Frequency;
import cqf.hjm.state.AppState;
import cqf.hjm.state.AppState.ProductType;
import cqf.hjm.state.StateManager;
import cqf.hjm.ui.main.AppCoordinator;
import cqf.ui.util.GUIUtilities;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class ProductManager extends SimpleInternalFrame implements StateManager {
    // Attributes

    private AppCoordinator coordinator;
    private AppState state;
    private JTextField interestRateField;
    private JTextField maturityField;
    private JComboBox frequencyOptionsList;
    private JRadioButton productZCB;
    private JRadioButton productCap;
    private JRadioButton productFloor;
    JLabel priceLabel = new JLabel("                ");

    /** Creates a new instance of View */
    public ProductManager(AppCoordinator coordinator) {
        super(GUIUtilities.readImageIcon("image/check_selected.gif"), "Product Specification");
        this.coordinator = coordinator;
        this.state = coordinator.getState();
        state.addManager(this);
        JPanel contentPanel = new JPanel(new GridLayout(0, 1));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));


        JPanel theRestPanel = new JPanel(new GridLayout(0, 2));
        theRestPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // The radio buttons
        productZCB = new JRadioButton("Zero Coupon Bond");
        productCap = new JRadioButton("Cap");
        productFloor = new JRadioButton("Floor");
        // The button group
        ButtonGroup productGroup = new ButtonGroup();
        productGroup.add(productZCB);
        productGroup.add(productCap);
        productGroup.add(productFloor);
        productZCB.setSelected(true);

        theRestPanel.add(new JLabel("Product: ", JLabel.RIGHT));
        theRestPanel.add(productZCB);

        theRestPanel.add(new JLabel("     "));
        theRestPanel.add(productCap);

        theRestPanel.add(new JLabel("     "));
        theRestPanel.add(productFloor);

        theRestPanel.add(new JLabel("Maturity: ", JLabel.RIGHT));
        maturityField = new JTextField(6);
        maturityField.setText("10");
        maturityField.setToolTipText("Enter the maturity of the product here.");
        theRestPanel.add(maturityField);
        //theRestPanel.add(new JLabel("     "));

        theRestPanel.add(new JLabel("Interest Rate (%): ", JLabel.RIGHT));
        interestRateField = new JTextField(6);
        interestRateField.setText("4.0");
        interestRateField.setToolTipText("Enter the short term interest rate here.");
        theRestPanel.add(interestRateField);
        //theRestPanel.add(new JLabel("     "));


        //String[] frequencyOptions = {"Monthly", "Quarterly", "Semiannually", "Annually"};
        frequencyOptionsList = new JComboBox(Frequency.values());
        frequencyOptionsList.setSelectedItem(Frequency.Quarterly);
        theRestPanel.add(new JLabel("Frequency: ", JLabel.RIGHT));
        theRestPanel.add(frequencyOptionsList);
        //theRestPanel.add(new JLabel("     "));



        contentPanel.add(theRestPanel);
        add(contentPanel, BorderLayout.CENTER);

    }

    public void refreshState() {
        if(productZCB.isSelected()) state.productType = ProductType.ZCB;
        if(productCap.isSelected()) state.productType = ProductType.Cap;
        if(productFloor.isSelected()) state.productType = ProductType.Floor;
        NumberFormat format = NumberFormat.getInstance(Locale.US);
        Number number;
        try {
            number = format.parse(maturityField.getText());
            state.maturity = number.doubleValue();
        } catch (ParseException ex) {
            Logger.getLogger(ProductManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            number = format.parse(interestRateField.getText());
            state.interestRate = number.doubleValue()/100.0;
        } catch (ParseException ex) {
            Logger.getLogger(ProductManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        state.frequency = (Frequency) frequencyOptionsList.getSelectedItem();
    }
}
