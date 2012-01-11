/*
 * PlasticTreeComboBoxUI.java
 *
 * Created on November 19, 2005, 9:40 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.jgoodies.plaf.plastic;

import java.awt.*;

import javax.swing.UIManager;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.JComboBox;
import javax.swing.plaf.ComboBoxUI;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import javax.swing.plaf.metal.MetalComboBoxUI;
import com.jgoodies.plaf.plastic.PlasticComboBoxUI;


/**
 *
 * @author birksworks
 */
public class PlasticTreeComboBoxUI extends MetalComboBoxUI {
    
        private JTreeComboBoxPopup popup;
	protected ComboPopup createPopup() {
            popup =  new JTreeComboBoxPopup( comboBox );
	    return popup;
	}
        public void setFont(Font font) {
            popup.setTreeFont(font);
        }
        public void setSelectedItem(Object item) {
            popup.setSelectedItem(item);
        }
	protected void setTreeLineColor(Color treeLineColor) {
            popup.setTreeLineColor(treeLineColor);
	}
	protected void setTreeTextColor(Color treeTextColor) {
            popup.setTreeTextColor(treeTextColor);
	}
	protected void setTreeIconForeground(Color treeIconForeground) {
            popup.setTreeIconForeground(treeIconForeground);
	}
	protected void setTreeIconBackground(Color treeIconBackground) {
            popup.setTreeIconBackground(treeIconBackground);
	}
	protected void setOptionTree(JTree optionTree) {
            popup.setOptionTree(optionTree);
	}
    }
    
    

