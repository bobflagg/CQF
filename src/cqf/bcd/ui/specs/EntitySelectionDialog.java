/*
 * ConditionDialog.java
 *
 * Created on November 5, 2005, 1:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cqf.bcd.ui.specs;

import cqf.bcd.ui.main.AppCoordinator;
import cqf.bcd.specs.BCDContract;
import cqf.bcd.specs.ContractSpecs;
import cqf.reference.Entity;
import cqf.reference.Sector;
import cqf.ui.util.GUIUtilities;
import cqf.ui.util.SimpleTreeCellRenderer;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;


/**
 *
 * @author birksworks
 */
public class EntitySelectionDialog extends JDialog implements TreeSelectionListener {
    protected AppCoordinator coordinator;
    private ContractSpecs contractSpecs;
    protected JLabel alertLabel;    
    protected boolean committed = false;
    protected String errorMessage = "";
    protected JPanel internalPanel = new JPanel(new BorderLayout());
    private JTree displayTree = null;
    TreePath[] selectedPaths = null;
    private TreePath[] previousSelectedPaths = null;
    
    /** Creates a new instance of ConditionDialog */
    public EntitySelectionDialog(AppCoordinator coordinator) {
        super(coordinator.getView(), true);   // modal dialog with no owner.
        this.coordinator = coordinator;
        contractSpecs = coordinator.getSpecs().getContractSpecs();
        displayTree = new JTree(Sector.getReferenceTreeModel());
        displayTree.setCellRenderer(new SimpleTreeCellRenderer());
        displayTree.addTreeSelectionListener(this);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        // add intructions label
        JPanel instructionsPanel = new JPanel(new BorderLayout());
        instructionsPanel.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
        JLabel instructionsLabel = new JLabel("Select Reference Entities");
        instructionsPanel.add(instructionsLabel, BorderLayout.WEST);
        contentPane.add(instructionsPanel, BorderLayout.NORTH);
        // build inner panel
        // add intructions label
        JPanel alertPanel = new JPanel(new BorderLayout());
        alertPanel.setBorder(BorderFactory.createEmptyBorder(2,5,2,5));
        alertLabel = new JLabel("");
        alertLabel.setIcon(GUIUtilities.EMPTY_ICON);
        alertLabel.setForeground(Color.RED);
        alertPanel.add(alertLabel, BorderLayout.WEST);
        internalPanel.add(alertPanel, BorderLayout.NORTH);
        // add content
        internalPanel.add(buildContentPanel(), BorderLayout.CENTER);
        // add inner panel to content pane
        contentPane.add(internalPanel, BorderLayout.CENTER);
        // add button panel
        contentPane.add(buildButtonPanel(), BorderLayout.SOUTH);
        pack();
    }

    protected JComponent buildContentPanel() {
        JScrollPane treePane = new JScrollPane(displayTree);
        treePane.setPreferredSize(new Dimension(320, 360));
        return treePane;
    }
    
    protected void clearErrorMessage() {
        alertLabel.setText("");
        alertLabel.setIcon(GUIUtilities.EMPTY_ICON);
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        alertLabel.setText("");
        alertLabel.setIcon(GUIUtilities.EMPTY_ICON);
    }
    
    protected JComponent buildButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,5,5,5));
        // Cancel
        AbstractButton cancelButton = GUIUtilities.createButton("delete_obj.gif", " Cancel ");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                committed = false;
                setVisible(false);
            }
        });
        Dimension widgetDimension = new Dimension((int)cancelButton.getPreferredSize().getWidth(), (int)cancelButton.getPreferredSize().getHeight());
        // ok
        AbstractButton okButton = GUIUtilities.createButton("check_selected.gif", " OK ");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                alertLabel.setText("");
                alertLabel.setIcon(GUIUtilities.EMPTY_ICON);
                if (commitChanges()) {
                    committed = true;
                    setVisible(false);
                } else {
                    alertLabel.setText(errorMessage);
                    alertLabel.setIcon(GUIUtilities.ALERT_ICON);
                }
            }
        });
        okButton.setPreferredSize(widgetDimension);
        buttonPanel.add(Box.createHorizontalGlue()); 
        buttonPanel.add(okButton);
        buttonPanel.add(Box.createHorizontalStrut(5)); 
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalGlue()); 
        return buttonPanel;
    }
    protected boolean commitChanges() {
        boolean somethingSelected = false;
        selectedPaths = displayTree.getSelectionPaths();
        if (selectedPaths != null) {
            for (int i = 0; i < selectedPaths.length; i++) {
                TreePath currentTreePath = selectedPaths[i];
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) currentTreePath.getLastPathComponent();
                try {
                    Entity value = (Entity)node.getUserObject();
                    if (node.isLeaf()) {
                        contractSpecs.addEntity(value);
                        somethingSelected = true;
                    }
                } catch (ClassCastException e) {
                    // this can only be because the root node was selected,
                    // which we want to ignore.
                }
            }
        }
        return somethingSelected;
    }
    /**
     *  Keeps a copy of the current selections.
     *
     *	<p>
     *	This is used to revert the state of this dialog box if the user
     *	cancel the modification to the selection.
     *	</p>
     *
     */
    public void recordCurrentSelection() {
        previousSelectedPaths = displayTree.getSelectionPaths();
    }

    protected void reset() {
        //selector.reset();
    }
    

    public boolean isCommitted() {
        return committed;
    }

    public void valueChanged(TreeSelectionEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
