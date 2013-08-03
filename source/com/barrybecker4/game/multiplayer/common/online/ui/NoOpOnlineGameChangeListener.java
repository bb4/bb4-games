// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.common.online.ui;

import com.barrybecker4.game.common.online.GameCommand;
import com.barrybecker4.game.common.online.OnlineChangeListener;
import com.barrybecker4.game.common.player.PlayerAction;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;

/**
 * Use this when you want the command handled, but nothing needs to happen.
 * @author Barry Becker
 */
public class NoOpOnlineGameChangeListener implements OnlineChangeListener {

    private MultiGamePlayer player;

    NoOpOnlineGameChangeListener(MultiGamePlayer player) {
        this.player = player;
    }

    /**
     * Simple returns if the command is for the player passed to the constructor.
     * @return true if the command is for the player passed to the constructor.
     */
    @Override
    public boolean handleServerUpdate(GameCommand cmd) {

         if (cmd.getName() == GameCommand.Name.DO_ACTION) {
            PlayerAction action = (PlayerAction) cmd.getArgument();
            if (action.getPlayerName().equals(player.getName())) {
                return true;
            }
        }
        return true;
    }
}
