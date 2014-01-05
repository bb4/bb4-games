/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala;

import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.player.PlayerOptions;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerOptions;

import java.awt.Color;
import java.awt.Dimension;

/**
 * Defines everything the computer needs to know to play Mancala.
 *
 * @author Barry Becker
*/
public class MancalaController extends TwoPlayerController {

    private static final int DEFAULT_NUM_ROWS = 20;

    private Dimension size;

    /**
     *  Constructor
     */
    public MancalaController() {
        size = new Dimension(DEFAULT_NUM_ROWS, DEFAULT_NUM_ROWS);
        initializeData();
    }

    /**
     * Construct the mancala game controller given an initial board size
     * @param nrows number of rows on the board
     * @param ncols number of columns on the board.
     */
    public MancalaController(int nrows, int ncols) {
        size = new Dimension( nrows, ncols );
        initializeData();
    }

    @Override
    protected MancalaBoard createBoard() {
        return new MancalaBoard(size.width, size.height);
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new TwoPlayerOptions();
    }

    @Override
    protected PlayerOptions createPlayerOptions(String playerName, Color color) {
        return new MancalaPlayerOptions(playerName, color);
    }

    /**
     * This gets the mancala specific patterns and weights
     */
    @Override
    protected void initializeData() {
        weights_ = new MancalaWeights();
    }

    /**
     * the first move of the game (made by the computer)
     */
    @Override
    public void computerMovesFirst() {
        TwoPlayerMove move = TwoPlayerMove.createMove( 1, 1, 0, new GamePiece(true) );
        makeMove( move );
    }

    public synchronized MancalaSearchable getSearchable() {
        return (MancalaSearchable) super.getSearchable();
    }

    @Override
    protected MancalaSearchable createSearchable(TwoPlayerBoard board, PlayerList players) {
        return new MancalaSearchable(board, players);
    }
}