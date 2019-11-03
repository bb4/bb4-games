/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.dialog;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.game.multiplayer.common.ui.MultiGameOptionsDialog;
import com.barrybecker4.game.multiplayer.poker.PokerOptions;
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
public class PokerOptionsDialog extends MultiGameOptionsDialog
                                implements ActionListener, ItemListener {
    private NumberInput ante_;
    private NumberInput initialChips_;
    private NumberInput maxAbsoluteRaise_;

    /**
     * Constructor
     */
    public PokerOptionsDialog(Component parent, GameController controller ) {
        super(parent, controller);
    }

    /**
     * @return an array of panels to put in the parent controller param panel.
     */
    @Override
    protected JComponent[] getControllerParamComponents() {

        PokerOptions options = (PokerOptions)controller_.getOptions();
        ante_ = new NumberInput( GameContext.getLabel("ANTE"), options.getAnte(),
                                 GameContext.getLabel("ANTE_TIP"),
                                 1, 100 * PokerOptions.DEFAULT_ANTE, true);
        initialChips_ =
                new NumberInput(GameContext.getLabel("INITIAL_CASH"), options.getInitialCash(),
                                GameContext.getLabel("INITIAL_CASH_TIP"),
                                1, 1000 * PokerOptions.DEFAULT_INITIAL_CASH, true);
        maxAbsoluteRaise_ =
                new NumberInput(GameContext.getLabel("MAX_RAISE"), options.getMaxAbsoluteRaise(),
                                GameContext.getLabel("MAX_RAISE_TIP"),
                                1, 100 * PokerOptions.DEFAULT_MAX_ABS_RAISE, true);

        initMultiControllerParamComponents(options);

        JPanel spacer = new JPanel();

        return new JComponent[] {ante_, initialChips_, maxAbsoluteRaise_, maxNumPlayers, numRobotPlayers, spacer};
    }


    @Override
    public GameOptions getOptions() {
        return new PokerOptions(maxNumPlayers.getIntValue(),
                                numRobotPlayers.getIntValue(),
                                ante_.getIntValue(),
                                maxAbsoluteRaise_.getIntValue(),
                                initialChips_.getIntValue());

    }

}
