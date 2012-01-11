package cqf.bcd.ui.specs;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;
import cqf.bcd.ui.main.AppCoordinator;
import cqf.bcd.specs.ContractSpecs;
import cqf.reference.Entity;
import cqf.ui.util.GUIUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;

/**
 *
 * @author birksworks
 */
public class ReferenceEntityManager extends SimpleInternalFrame implements Observer, ListSelectionListener, ListDataListener {
    //private static int WIDGET_WIDTH = 140;
    // Attributes
    private AppCoordinator coordinator;
    private JList entityList;
    private JScrollPane scroller;
    private boolean internalChange = false;
    private AbstractButton clearButton;
    private AbstractButton clearAllButton;
    private EntitySelectionDialog dialog;
    private ContractSpecs contractSpecs;

    /** Creates a new instance of CriteriaView */
    public ReferenceEntityManager(AppCoordinator coordinator) {
        super(GUIUtilities.readImageIcon("image/check_selected.gif"), "Reference Entities");
        this.coordinator = coordinator;
        contractSpecs = coordinator.getSpecs().getContractSpecs();
        //setLayout(new BorderLayout());
        add(buildButtonPanel(coordinator), BorderLayout.SOUTH);
        dialog = new EntitySelectionDialog(coordinator);
        entityList = new JList(contractSpecs);
        entityList.addListSelectionListener(this);
        contractSpecs.addListDataListener(this);
        JScrollPane scroller = new JScrollPane(entityList);
        Dimension panelDimension = new Dimension(Integer.parseInt(coordinator.getProperty("panel-width")), Integer.parseInt(coordinator.getProperty("panel-height")));
        scroller.setPreferredSize(panelDimension);
        scroller.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        scroller.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        add(scroller, BorderLayout.CENTER);
    }

    public void update(Observable observed, Object arg) {
    }

    public void valueChanged(TreeSelectionEvent e) {
    }

    protected JComponent buildButtonPanel(final AppCoordinator coordinator) {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        // clear selected
        clearButton = GUIUtilities.createButton("delete_obj.gif", "Clear Selected");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearSelectedNodes();
                clearButton.setEnabled(false);
            }
        });
        clearButton.setEnabled(false);
        // clear all
        clearAllButton = GUIUtilities.createButton("delete_obj.gif", "Remove All Entities");
        clearAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearAllNodes();
            }
        });
        clearAllButton.setEnabled(false);
        // Add Entity
        AbstractButton addButton = GUIUtilities.createButton("add.png", "Add Entities");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setLocationRelativeTo(coordinator.getView());
                dialog.setVisible(true);
                //if (dialog.isCommitted()) model.add(item);
            }
        });
        // Calibrate
        AbstractButton calibrateButton = GUIUtilities.createButton("run.png", "Calibrate");
        calibrateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    coordinator.callibrate();
                } catch (Exception ex) {
                    Logger.getLogger(ReferenceEntityManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        //buttonPanel.add(clearButton);
        buttonPanel.add(addButton);
        buttonPanel.add(clearAllButton);
        //buttonPanel.add(calibrateButton);

        AbstractButton resetButton = GUIUtilities.createButton("forward.gif", "Reset");
        //buttonPanel.add(Box.createHorizontalGlue());
        //buttonPanel.add(clearButton);
        //buttonPanel.add(Box.createRigidArea(new Dimension(4, 0)));
        //buttonPanel.add(clearAllButton);
        //buttonPanel.add(analyzeButton);
        //buttonPanel.add(Box.createRigidArea(new Dimension(4, 0)));
        //buttonPanel.add(resetButton);
        //buttonPanel.add(Box.createHorizontalGlue());

        Dimension widgetDimension = new Dimension(Integer.parseInt(coordinator.getProperty("widget-width")),
                Integer.parseInt(coordinator.getProperty("widget-height")));
        clearButton.setPreferredSize(widgetDimension);
        clearAllButton.setPreferredSize(widgetDimension);
        calibrateButton.setPreferredSize(widgetDimension);
        addButton.setPreferredSize(widgetDimension);

        return buttonPanel;
    }

    private void clearSelectedNodes() {
        for (Object entity:entityList.getSelectedValues()) contractSpecs.removeEntity((Entity)entity);
        entityList.clearSelection();
    }

    private void clearAllNodes() {
        contractSpecs.removeAllEntities();
    }

    public void valueChanged(ListSelectionEvent e) {
        if (entityList.isSelectionEmpty()) {
            clearButton.setEnabled(false);
        } else {
            clearButton.setEnabled(true);
        }
    }

    public void intervalAdded(ListDataEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void intervalRemoved(ListDataEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void contentsChanged(ListDataEvent e) {
        if (contractSpecs.getSize()==0) {
            clearAllButton.setEnabled(false);
        } else {
            clearAllButton.setEnabled(true);
        }
    }

}
