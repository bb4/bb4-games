/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex;

import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.player.PlayerOptions;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerOptions;
import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.game.twoplayer.gomoku.GoMokuController;
import com.barrybecker4.game.twoplayer.tictactoe.TicTacToeBoard;
import com.barrybecker4.game.twoplayer.tictactoe.TicTacToePatterns;

import java.awt.Color;

/**
 * Defines everything the computer needs to know to play TicTacToe.
 *
 * Without taking symmetries into account, the number of possible games is 255,168 (Henry Bottomley, 2001)
 * Accounting for board symmetries, the number of games in these conditions is 26,830 (Schaeffer 2002)
 * See http://en.wikipedia.org/wiki/Tic-tac-toe
 *
 * @author Barry Becker
 */
public class HexController<B extends HexBoard> extends TwoPlayerController<TwoPlayerMove, HexBoard> {

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



    protected HexSearchable createSearchable(B board, PlayerList players) {
        return new HexSearchable(board, players);
    }

    @Override
    public void computerMovesFirst() {

    }
}
