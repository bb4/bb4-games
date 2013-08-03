// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.ui;

import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;


/**
 *  The end of game message.
 *
 *  @author Barry Becker
 */
public class GameOverMessage {

    StringBuilder buf;

    /**
     * Construct the application
     */
    public GameOverMessage(PlayerList players) {
        buf = new StringBuilder("Game Over\n");

        // find the player with the most money. That's the winner.
        int max = -1;
        PokerPlayer winner = null;
        for (final Player p : players) {
            PokerPlayer pp = (PokerPlayer) p;
            if (pp.getCash() > max) {
                max = pp.getCash();
                winner = pp;
            }
        }
        assert winner != null;
        buf.append(winner.getName()).append(" won the game with $").append(winner.getCash()).append('.');
    }

    /**
     * @return  the message to display at the completion of the game.
     */
    public String toString() {
        return buf.toString();
    }
}
