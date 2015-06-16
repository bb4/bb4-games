/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex;

import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.player.PlayerOptions;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerOptions;
import com.barrybecker4.game.twoplayer.common.search.Searchable;

import java.awt.Color;

/**
 * Defines everything the computer needs to know to play Hex.
 *
 * @author Barry Becker
 */
public class HexController extends TwoPlayerController<TwoPlayerMove, HexBoard> {

    /**
     *  Constructor
     */
    public HexController() {
        initializeData();
    }

    @Override
    protected HexBoard createBoard() {
        return new HexBoard();
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new TwoPlayerOptions();
    }

    @Override
    protected PlayerOptions createPlayerOptions(String playerName, Color color) {
        return new HexPlayerOptions(playerName, color);
    }

    /**
     *  this gets the game specific patterns and weights
     */
    @Override
    protected void initializeData() {
        weights_ = new HexWeights();
    }

    @Override
    public synchronized HexSearchable getSearchable() {
        return (HexSearchable) super.getSearchable();
    }

    @Override
    protected Searchable<TwoPlayerMove, HexBoard> createSearchable(HexBoard board, PlayerList players) {
        return new HexSearchable(board, players);
    }

    @Override
    public void computerMovesFirst() {
    }
}
