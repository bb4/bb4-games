/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.game.multiplayer.common.ui.MultiGameOptionsDialog;
import com.barrybecker4.game.multiplayer.set.SetOptions;
import com.barrybecker4.ui.components.NumberInput;

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
class SetOptionsDialog extends MultiGameOptionsDialog
        implements ActionListener, ItemListener {

    private NumberInput initialNumCards_;

    /**
     * Constructor
     */
    SetOptionsDialog(Component parent, GameController controller ) {
        super( parent, controller);
    }


    /**
     * @return Set game options tab panel.
     */
    @Override
    protected JComponent[] getControllerParamComponents() {
        SetOptions options = (SetOptions) controller_.getOptions();

        initialNumCards_ =
                new NumberInput(GameContext.getLabel("INITIAL_NUM_CARDS"), options.getInitialNumCardsShown(),
                                GameContext.getLabel("INITIAL_NUM_CARDS_TIP"), 8, 81, true);

        initMultiControllerParamComponents(options);

        return new JComponent[] {initialNumCards_, maxNumPlayers_, numRobotPlayers_};
    }


    @Override
    public GameOptions getOptions() {
        return new SetOptions(maxNumPlayers_.getIntValue(),
                              numRobotPlayers_.getIntValue(),
                              initialNumCards_.getIntValue());
    }


}