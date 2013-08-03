/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.online.ui;

import com.barrybecker4.game.common.online.OnlineGameTable;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.multiplayer.common.MultiGameOptions;
import com.barrybecker4.game.multiplayer.common.online.ui.MultiPlayerOnlineGameTablesTable;
import com.barrybecker4.game.multiplayer.trivial.online.OnlineTrivialTable;
import com.barrybecker4.game.multiplayer.trivial.player.TrivialHumanPlayer;
import com.barrybecker4.ui.table.TableButtonListener;

/**
 * Show Trivial specific game options in the table row.
 *
 * @author Barry Becker
 */
public class TrivialOnlineGameTablesTable extends MultiPlayerOnlineGameTablesTable {

    private static final String[] TRIVIAL_COLUMN_NAMES = {JOIN, MIN_NUM_PLAYERS, PLAYER_NAMES};

    /**
     * @param tableButtonListener  that gets called when the player selects a different table to join.
     */
    public TrivialOnlineGameTablesTable(TableButtonListener tableButtonListener) {
         super(TRIVIAL_COLUMN_NAMES, tableButtonListener);
    }


    @Override
    protected Object[] getRowObject(OnlineGameTable onlineTable, boolean localPlayerAtTable) {

        Object d[] = new Object[getNumColumns()];

        // false if active player is in this table.
        // You cannot join a table that you are already at
        d[JOIN_INDEX] = localPlayerAtTable ? "Leave" : "Join";
        d[NUM_PLAYERS_INDEX] = onlineTable.getNumPlayersNeeded();
        d[PLAYER_NAMES_INDEX] = onlineTable.getPlayerNames();
        return d;
    }

    @Override
    public OnlineGameTable createOnlineTable(String ownerPlayerName, MultiGameOptions options) {
        Player player = createPlayerForName(ownerPlayerName);
        return new OnlineTrivialTable(getUniqueName(), player, options);
    }

    @Override
    public Player createPlayerForName(String playerName) {
        return new TrivialHumanPlayer(playerName,  getRandomColor());
    }

}
