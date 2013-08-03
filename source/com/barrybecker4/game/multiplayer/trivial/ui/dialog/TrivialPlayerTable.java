/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui.dialog;

import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.multiplayer.common.ui.PlayerTable;
import com.barrybecker4.game.multiplayer.trivial.player.TrivialPlayer;

import javax.swing.table.TableModel;
import java.awt.*;

/**
 * contains a list of players.
 * All the cells are editable.
 * It is initialized with a list of Players and returns a list of Players
 *
 * @author Barry Becker
 */
class TrivialPlayerTable extends PlayerTable {

    private static String[] trivialColumnNames_ =  {
       NAME,
       COLOR,
       HUMAN
    };

    /**
     * constructor
     * @param players to initialize the rows in the table with.
     */
    TrivialPlayerTable(PlayerList players) {
        super(players, trivialColumnNames_);
    }

    /**
     * @return  the players represented by rows in the table
     */
    @Override
    public PlayerList getPlayers() {
        TableModel model = table_.getModel();
        int nRows = model.getRowCount();
        PlayerList players = new PlayerList();
        for (int i = 0; i < nRows; i++) {
            players.add( TrivialPlayer.createTrivialPlayer(
                                    (String) model.getValueAt(i, NAME_INDEX),
                                    (Color) model.getValueAt(i, COLOR_INDEX),
                                    ((Boolean) model.getValueAt(i, HUMAN_INDEX))));
        }
        return players;
    }

    /**
     * add a row based on a player object
     * @param player to add
     */
    @Override
    protected void addRow(Object player) {
        Player p = (Player) player;
        Object d[] = new Object[getNumColumns()];
        d[NAME_INDEX] = p.getName();
        d[COLOR_INDEX] = p.getColor();
        d[HUMAN_INDEX] = p.isHuman();
        getPlayerModel().addRow(d);
    }


    @Override
    protected Player createPlayer() {
        int ct = table_.getRowCount();
        Color newColor = TrivialPlayer.getNewPlayerColor(getPlayers());
        return TrivialPlayer.createTrivialPlayer("Robot "+(ct+1), newColor, true);
    }
}
