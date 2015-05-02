/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.pente;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.player.PlayerOptions;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerOptions;
import com.barrybecker4.game.twoplayer.pente.pattern.PentePatterns;
import com.barrybecker4.game.twoplayer.pente.pattern.PenteWeights;

import java.awt.Color;
import java.awt.Dimension;

/**
 * Defines everything the computer needs to know to play Pente.
 *
 * @author Barry Becker
*/
public class PenteController<B extends PenteBoard> extends TwoPlayerController<TwoPlayerMove, B> {

    private static final int DEFAULT_NUM_ROWS = 20;

    private Dimension size;

    /**
     *  Constructor
     */
    public PenteController() {
        size = new Dimension(DEFAULT_NUM_ROWS, DEFAULT_NUM_ROWS);
        initializeData();
    }

    /**
     * Construct the Pente game controller given an initial board size
     * @param nrows number of rows on the board
     * @param ncols number of columns on the board.
     */
    public PenteController(int nrows, int ncols ) {
        size = new Dimension( nrows, ncols );
        initializeData();
    }

    @Override
    protected B createBoard() {
        return (B)new PenteBoard(size.width, size.height);
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new TwoPlayerOptions();
    }

    @Override
    protected PlayerOptions createPlayerOptions(String playerName, Color color) {
        return new PentePlayerOptions(playerName, color);
    }

    /**
     * This gets the pente specific patterns and weights
     */
    @Override
    protected void initializeData() {
        weights_ = new PenteWeights();
    }

    /**
     * the first move of the game (made by the computer)
     */
    @Override
    public void computerMovesFirst() {
        int delta = getWinRunLength() - 1;
        Board board = getBoard();
        int col = (int) (GameContext.random().nextFloat() * (board.getNumCols() - 2 * delta) + delta + 1);
        int row = (int) (GameContext.random().nextFloat() * (board.getNumRows() - 2 * delta) + delta + 1);
        TwoPlayerMove move = TwoPlayerMove.createMove( row, col, 0, new GamePiece(true) );
        makeMove( move );
    }

    protected int getWinRunLength() {
        return PentePatterns.WIN_RUN_LENGTH;
    }

    public synchronized PenteSearchable<B> getSearchable() {
        return (PenteSearchable<B>) super.getSearchable();
    }

    @Override
    protected PenteSearchable<B> createSearchable(B board, PlayerList players) {
        return new PenteSearchable<>(board, players);
    }
}