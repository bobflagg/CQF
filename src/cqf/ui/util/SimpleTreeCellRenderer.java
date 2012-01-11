/*
 * SimpleTreeCellRenderer.java
 *
 * Created on November 18, 2005, 10:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cqf.ui.util;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 *
 * @author birksworks
 */
public class SimpleTreeCellRenderer implements TreeCellRenderer {
        
        private JLabel renderer;
        private boolean leafSelectable;
        public SimpleTreeCellRenderer() {
            this(true);
        }
        public SimpleTreeCellRenderer(boolean leafSelectable) {
            super();
            renderer = new JLabel();
            renderer.setOpaque(true);
            this.leafSelectable = leafSelectable;
        }
        
        public Component  getTreeCellRendererComponent(JTree tree,
                Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            
            // Change background color based on selected state
            Color background = (selected ? Color.lightGray : Color.white);
            renderer.setBackground(background);
            
            if (leaf) {
                renderer.setText(value.toString());
                if (leafSelectable) renderer.setIcon(GUIUtilities.LEAF_TREE_NODE_ICON);
                else  renderer.setIcon(GUIUtilities.LEAF_NO_SELECT_TREE_NODE_ICON);
            } else {
                renderer.setText(value.toString());
                if (expanded) renderer.setIcon(GUIUtilities.NON_LEAF_EXPANDED_TREE_NODE_ICON);
                else renderer.setIcon(GUIUtilities.NON_LEAF_TREE_NODE_ICON);
            }
            return renderer;
        }
    }