package com.jgoodies.plaf.plastic;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.ToolTipManager;
import javax.swing.ImageIcon;
import javax.swing.Icon;

import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.plaf.basic.BasicComboPopup;



public class JTreeComboBoxPopup extends BasicComboPopup implements
TreeSelectionListener, PopupMenuListener {
    private static final Dimension POPUP_BUTTON_DIM = new Dimension(280, 160);
    
    private JTree optionTree;
    private JPopupMenu popup;
    private Font treeFont;
    private Color treeTextColor;
    private Color treeLineColor;
    private Color treeIconBackground;
    private Color treeIconForeground;
    private Dimension dimension;
    protected boolean mouseInside = false;
    
    /**
     * Constructs new JLightTreeComboBoxPopup instance. 
     *
     * @param   comboBox    combobox controller for this popup.
     */
    public JTreeComboBoxPopup(JComboBox comboBox) {
        super(comboBox);
        initializePopup();
    }
    
    /**
     * Displays this popup.
     */
    public void show() {
        popup.show(comboBox, 0, -comboBox.getHeight());
    }
    
    /**
     * Hides this popup.
     */
    public void hide() {
        popup.setVisible(false);
    }
    
    /*
     * Methods of PopupMenuListener
     */
    public void mouseEntered(MouseEvent e) {
        mouseInside = true;
    }
    public void mouseExited(MouseEvent e) {
        mouseInside = false;
    }
    public void popupMenuCanceled(PopupMenuEvent e) {}
    protected boolean hideNext = false;
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        hideNext = mouseInside;
    }
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
    
    
    /*
     * Create an empty popup menu to house option tree component.
     */
    protected void initializePopup() {
        popup = new JPopupMenu();
        popup.setPreferredSize(POPUP_BUTTON_DIM);
        popup.addPopupMenuListener(this);
    }
    
    /*
     * Attempt to select the given item in option tree 
     *
     * @param   item    the item to be selected.
    */
    public void setSelectedItem(Object item) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)optionTree.getModel().getRoot();
        setSelectedItem(root, item);
    }
    
    // Helper method to recursively implement setSelectedItem.
    private boolean setSelectedItem(DefaultMutableTreeNode node, Object item) {
        if (node.isLeaf()) {
            Object value = node.getUserObject();
            if (item.equals(value)) {
                optionTree.setSelectionPath(getPath(node));
                return true;
            }
        } else {
            Enumeration childEnumeration = node.children();
            while (childEnumeration.hasMoreElements()) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)
                childEnumeration.nextElement();
                if (setSelectedItem(childNode, item)) return true;
            }
        }
        return false;
    }
    
    // Returns a TreePath containing the specified node.
    private TreePath getPath(TreeNode node) {
        List list = new ArrayList();
        
        // Add all nodes to list
        while (node != null) {
            list.add(node);
            node = node.getParent();
        }
        Collections.reverse(list);
        
        // Convert array of nodes to TreePath
        return new TreePath(list.toArray());
    }
    
    /*
     * Methods of TreeSelectionListener
     */    
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
        optionTree.getLastSelectedPathComponent();
        
        if (node == null) return;
        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) {
            //comboBox.setSelectedItem(node.toString());
            comboBox.setSelectedItem(node);
            hide();
        }
    }
    
    // update the Popup when new option tree is specified
    private void updatePopup() {
        popup.removeAll();
        if (optionTree != null)    {
            
            
            optionTree.setRootVisible(false);
            optionTree.setShowsRootHandles(true);
            optionTree.putClientProperty("line.horiz_color", treeLineColor);
            optionTree.putClientProperty("line.vert_color", treeLineColor);
            optionTree.putClientProperty("icon.background", treeIconBackground);
            optionTree.putClientProperty("icon.foreground", treeIconForeground);
            optionTree.setFont(treeFont);
            DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) optionTree.getCellRenderer();
            renderer.setTextNonSelectionColor(treeTextColor);
            
            
            JScrollPane treeView = new JScrollPane(optionTree);
            treeView.setOpaque(true);
            treeView.setBackground(comboBox.getBackground());
            treeView.setMinimumSize(dimension);
            treeView.setMaximumSize(dimension);
            treeView.setPreferredSize(dimension);
            popup.add(BorderLayout.CENTER, treeView);
            popup.pack();
            optionTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            
            //Listen for when the selection changes.
            optionTree.addTreeSelectionListener(this);
            
            comboBox.removeAllItems();
            addMenuItems((DefaultMutableTreeNode) optionTree.getModel().getRoot());
            // why don't the node labels appear before the node is expanded?!?
            for (int i = 0; i < 10 ; i++) {
                try {
                    optionTree.expandRow(i);
                    optionTree.collapseRow(i);
                } catch (Exception ex) {
                    break;                   
                }
            }
        } else {
            //JMenuItem menuItem = new JMenuItem("MEASURES");
            //popup.add(menuItem);
        }
    }
    
    // add the leaf nodes to the parent comboBox.
    private void addMenuItems(DefaultMutableTreeNode node ) {
        if (node.isLeaf()) {
            //comboBox.addItem(node.toString());
            comboBox.addItem(node);
        } else {
            Enumeration childEnumeration = node.children();
            while (childEnumeration.hasMoreElements()) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)
                childEnumeration.nextElement();
                addMenuItems(childNode);
            }
        }
    }
    
    
    /**
     * Setter for property optionTree.
     * @param optionTree New value of property optionTree.
     */
    public void setOptionTree(JTree optionTree) {
        this.optionTree = optionTree;
        updatePopup();
    }
    
    
    
    /**
     * Setter for property dimension.
     * @param dimension New value of property dimension.
     */
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }
    
    
    /**
     * Setter for property treeTextColor.
     * @param treeTextColor New value of property treeTextColor.
     */
    public void setTreeTextColor(Color treeTextColor) {
        this.treeTextColor = treeTextColor;
    }
    
    
    /**
     * Setter for property treeLineColor.
     * @param treeLineColor New value of property treeLineColor.
     */
    public void setTreeLineColor(Color treeLineColor) {
        this.treeLineColor = treeLineColor;
    }
    
    
    /**
     * Setter for property treeIconBackground.
     * @param treeIconBackground New value of property treeIconBackground.
     */
    public void setTreeIconBackground(Color treeIconBackground) {
        this.treeIconBackground = treeIconBackground;
    }
    
    /**
     * Setter for property treeIconForeground.
     * @param treeIconForeground New value of property treeIconForeground.
     */
    public void setTreeIconForeground(Color treeIconForeground) {
        this.treeIconForeground = treeIconForeground;
    }
    
    /**
     * Setter for property treeIconForeground.
     * @param treeIconForeground New value of property treeIconForeground.
     */
    public void setTreeFont(Font treeFont) {
        this.treeFont = treeFont;
    }
    
}

