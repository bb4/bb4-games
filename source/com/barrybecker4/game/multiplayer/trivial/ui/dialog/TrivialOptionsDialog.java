/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui.dialog;

import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.game.multiplayer.common.ui.MultiGameOptionsDialog;
import com.barrybecker4.game.multiplayer.trivial.TrivialOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
public class TrivialOptionsDialog extends MultiGameOptionsDialog
                         implements ActionListener, ItemListener {

    /**
     * Constructor
     */
    public TrivialOptionsDialog(Component parent, GameController controller ) {
        super( parent, controller);
    }

    /**
     * @return an array of panels to put in the parent controller param panel.
     */
    @Override
    protected JComponent[] getControllerParamComponents() {

        TrivialOptions options = (TrivialOptions)controller_.getOptions();

        initMultiControllerParamComponents(options);

        JPanel spacer = new JPanel();

        return new JComponent[] { maxNumPlayers_, numRobotPlayers_, spacer};
    }


    @Override
    public GameOptions getOptions() {
        return new TrivialOptions(maxNumPlayers_.getIntValue(),
                                numRobotPlayers_.getIntValue());
    }

}