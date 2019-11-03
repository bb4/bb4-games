/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.online;

import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;
import com.barrybecker4.game.multiplayer.common.online.ui.MultiPlayerOnlineTable;
import com.barrybecker4.game.multiplayer.poker.PokerOptions;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;
import com.barrybecker4.game.multiplayer.poker.player.PokerRobotPlayer;

import java.awt.*;

/**
 * @author Barry Becker
 */
public class OnlinePokerTable extends MultiPlayerOnlineTable {

    private static final long serialVersionUID = 1;

    public OnlinePokerTable(String name, Player initialPlayer, GameOptions options) {
        super(name, initialPlayer, options);
    }

    /**
     * add robot player i to the table.
     * Since this is on the client, robots are surrogate players.
     */
    @Override
    protected void addRobotPlayer(int i) {

        Color newColor = MultiGamePlayer.getNewPlayerColor(getPlayers());

        PokerOptions options = (PokerOptions) getGameOptions();
        String name = "Robot " + (i+1);
        PokerPlayer robot = PokerRobotPlayer.getRandomRobotPlayer(name, options.getInitialCash(), newColor);
        //SurrogateMultiPlayer sp = new SurrogateMultiPlayer(robot);
        this.addPlayer(robot);
    }

}
