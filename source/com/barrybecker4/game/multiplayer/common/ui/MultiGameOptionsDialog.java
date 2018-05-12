/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.ui.dialogs.GameOptionsDialog;
import com.barrybecker4.game.multiplayer.common.MultiGameOptions;
import com.barrybecker4.ui.components.NumberInput;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Barry Becker
 */
public abstract class MultiGameOptionsDialog extends GameOptionsDialog
                                             implements KeyListener {
    protected NumberInput maxNumPlayers;
    protected NumberInput numRobotPlayers;

    private static final int ABS_MAX_NUM_PLAYERS = 30;

    protected MultiGameOptionsDialog(Component parent, GameController controller ) {
        super(parent, controller);
    }

    protected void initMultiControllerParamComponents(MultiGameOptions options) {
        maxNumPlayers =
            new NumberInput(GameContext.getLabel("MAX_NUM_PLAYERS"), options.getMaxNumPlayers(),
                            GameContext.getLabel("MAX_NUM_PLAYERS_TIP"), options.getMinNumPlayers(),
                            ABS_MAX_NUM_PLAYERS, true);
        maxNumPlayers.addKeyListener(this);

        numRobotPlayers =
                new NumberInput(GameContext.getLabel("NUM_ROBOTS"), options.getNumRobotPlayers(),
                                GameContext.getLabel("NUM_ROBOTS_TIP"), 0, ABS_MAX_NUM_PLAYERS, true);
    }

    @Override
    public void keyTyped(KeyEvent e) {
         if (maxNumPlayers.getIntValue() > 0) {
             numRobotPlayers.setMaxAllowed(maxNumPlayers.getValue());
         }
    }

    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}

