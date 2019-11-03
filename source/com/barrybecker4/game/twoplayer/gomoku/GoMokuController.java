/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.player.PlayerOptions;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerOptions;
import com.barrybecker4.game.twoplayer.gomoku.pattern.GoMokuPatterns;
import com.barrybecker4.game.twoplayer.gomoku.pattern.GoMokuWeights;

import java.awt.Color;
import java.awt.Dimension;

/**
 * Defines everything the computer needs to know to play GoMoku.
 *
 * @author Barry Becker
*/
public class GoMokuController<B extends GoMokuBoard> extends TwoPlayerController<TwoPlayerMove, B> {

    private static final int DEFAULT_NUM_ROWS = 20;

    private Dimension size;

    /**
     *  Constructor
     */
    public GoMokuController() {
        size = new Dimension(DEFAULT_NUM_ROWS, DEFAULT_NUM_ROWS);
        initializeData();
    }

    /**
     * Construct the GoMoku game controller given an initial board size
     * @param nrows number of rows on the board
     * @param ncols number of columns on the board.
     */
    public GoMokuController(int nrows, int ncols) {
        size = new Dimension( nrows, ncols );
        initializeData();
    }

    @Override
    protected B createBoard() {
        return (B)new GoMokuBoard(size.width, size.height);
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new TwoPlayerOptions();
    }

    @Override
    protected PlayerOptions createPlayerOptions(String playerName, Color color) {
        return new GoMokuPlayerOptions(playerName, color);
    }

    /**
     * This gets the gomoku specific patterns and weights
     */
    @Override
    protected void initializeData() {
        weights_ = new GoMokuWeights();
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
        return GoMokuPatterns.WIN_RUN_LENGTH;
    }

    public synchronized GoMokuSearchable<B> getSearchable() {
        return (GoMokuSearchable<B>) super.getSearchable();
    }

    @Override
    protected GoMokuSearchable<B> createSearchable(B board, PlayerList players) {
        return new GoMokuSearchable<>(board, players);
    }
}
