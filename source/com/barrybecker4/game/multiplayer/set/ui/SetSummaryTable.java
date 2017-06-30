/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.multiplayer.common.ui.SummaryTable;
import com.barrybecker4.game.multiplayer.set.SetPlayer;

import javax.swing.*;


/**
 * Shows a list of the players stats.
 * None of the cells are editable.
 * @see SetPlayer
 *
 * @author Barry Becker
 */
class SetSummaryTable extends SummaryTable {

    private static final int NUM_SETS_INDEX = 2;

    private static final String NUM_SETS = GameContext.getLabel("NUM_SETS");

    private static final String[] COLUMN_NAMES =  {
                                                  NAME,
                                                  COLOR,
                                                  NUM_SETS};

    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    public SetSummaryTable(PlayerList players) {
        super(players, COLUMN_NAMES);
        getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * add a row based on a player object
     * @param player to add
     */
    @Override
    protected void addRow(Object player) {
        SetPlayer p = (SetPlayer)player;
        Object d[] = new Object[getNumColumns()];

        d[NAME_INDEX] = p.getName();
        d[COLOR_INDEX ] = p.getColor();
        d[NUM_SETS_INDEX] = "" + p.getNumSetsFound();

        getPlayerModel().addRow(d);
    }
}
