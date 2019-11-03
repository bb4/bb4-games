/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.online.ui;

import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.game.common.online.OnlineGameTable;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.multiplayer.common.MultiGameOptions;

/**
 * Some number of players sitting around a virtual game table online.
 * @author Barry Becker
 */
public abstract class MultiPlayerOnlineTable extends OnlineGameTable {

    protected MultiPlayerOnlineTable(String name, Player initialPlayer, GameOptions options) {
        super(name, initialPlayer, options);
        int numRobots = ((MultiGameOptions) options).getNumRobotPlayers();
        for (int i=0; i<numRobots; i++) {
            addRobotPlayer(i);
        }
    }

    protected abstract void addRobotPlayer(int i);
}
