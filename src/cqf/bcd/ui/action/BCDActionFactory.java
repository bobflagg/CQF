package cqf.bcd.ui.action;

import cqf.ui.action.ActionFactory;
import cqf.ui.util.GUIUtilities;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;

/**
 *
 * @author birksworks
 */
public class BCDActionFactory implements ActionFactory {
    private ActionMethods actionMethods;
    private Map<String, AbstractAction> actionMap = new HashMap<String, AbstractAction>();

    /** Creates a new instance of ISightActionFactory */
    public BCDActionFactory(ActionMethods actionMethods) {
        this.actionMethods = actionMethods;
        buildActions();
    }
    public AbstractAction getAction(String actionName) {
        AbstractAction action = actionMap.get(actionName);
        if (action == null) action = actionMap.get("Exit");
        return action;
    }
    private void buildActions() {
        // Exit
        String actionName = "Exit";
        AbstractAction action = new AbstractAction(actionName, GUIUtilities.readImageIcon("image/exit.gif")) {
            public void actionPerformed(ActionEvent e) {
                actionMethods.exit();
            }
        };
        actionMap.put(actionName, action);
        // About-ProSight
        actionName = "About BCD Pricer";
        action = new AbstractAction(actionName, GUIUtilities.readImageIcon("image/help.gif")) {
            public void actionPerformed(ActionEvent e) {
                actionMethods.exit();
            }
        };
        actionMap.put(actionName, action);
    }
}
