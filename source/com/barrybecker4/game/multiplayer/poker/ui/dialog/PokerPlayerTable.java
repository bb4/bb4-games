/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.dialog;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.multiplayer.common.ui.PlayerTable;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;

import javax.swing.table.TableModel;
import java.awt.*;

/**
 * Contains a list of players. All the cells are editable.
 * It is initialized with a list of Players and returns a list of Players.
 * @see PokerPlayer
 *
 * @author Barry Becker
 */
class PokerPlayerTable extends PlayerTable {

    private static final int CASH_INDEX = 3;

    private static final String CASH = GameContext.getLabel("CASH");

    private static String[] pokerColumnNames_ =  {
       NAME,
       COLOR,
       HUMAN,
       CASH
    };

    private static final int DEFAULT_CASH_AMOUNT = 100;


    /**
     * constructor
     * @param players to initialize the rows in the table with.
     */
    PokerPlayerTable(PlayerList players) {
        super(players, pokerColumnNames_);
    }


    /**
     * @return  the players represented by rows in the table
     */
    @Override
    public PlayerList getPlayers() {

        TableModel model = table().getModel();
        int nRows = model.getRowCount();
        PlayerList players = new PlayerList();
        for (int i = 0; i < nRows; i++) {
            players.add( PokerPlayer.createPokerPlayer(
                                    (String) model.getValueAt(i, NAME_INDEX),
                                    ((Integer) model.getValueAt(i, CASH_INDEX)),
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
    public void addRow(Object player) {
        Player p = (Player) player;
        Object d[] = new Object[getNumColumns()];
        d[NAME_INDEX] = p.getName();
        d[COLOR_INDEX] = p.getColor();
        d[CASH_INDEX] = DEFAULT_CASH_AMOUNT; //p.getCash();
        d[HUMAN_INDEX] = p.isHuman();
        getPlayerModel().addRow(d);
    }


    @Override
    protected Player createPlayer() {
        int ct = table().getRowCount();
        Color newColor = PokerPlayer.getNewPlayerColor(getPlayers());
        return PokerPlayer.createPokerPlayer(
                                             "Robot " + (ct+1), DEFAULT_CASH_AMOUNT, newColor, true);
    }
}
