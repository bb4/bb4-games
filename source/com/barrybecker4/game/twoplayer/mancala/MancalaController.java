/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.player.PlayerOptions;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerOptions;
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard;
import com.barrybecker4.game.twoplayer.mancala.move.MancalaMove;

import java.awt.Color;
import java.awt.Dimension;

/**
 * Defines everything the computer needs to know to play Mancala.
 *
 * @author Barry Becker
*/
public class MancalaController extends TwoPlayerController {

    private static final int DEFAULT_NUM_ROWS = 2;
    private static final int DEFAULT_NUM_COLS = 8;

    private Dimension size;

    /**
     *  Constructor
     */
    public MancalaController() {
        size = new Dimension(DEFAULT_NUM_COLS, DEFAULT_NUM_ROWS);
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
        return new MancalaBoard(size.width);
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
     * If the players storage bin got the last stone, then they go again.
     * @param m the move to play.
     */
    @Override
    public void makeMove( Move m ) {
        MancalaMove move = (MancalaMove) m;
        getSearchable().makeInternalMove(move);

        MancalaBoard board = (MancalaBoard) getBoard();
        if (!board.moveAgainAfterMove(move)) {
           player1sTurn_ = !((TwoPlayerMove) move).isPlayer1();
        }

        if (board.isEmpty())  {
            determineWinner(board);
        }
    }

    /**
     * After one players side is empty, the other players side is also cleared (with stones moved to their store)
     * now it's time to see who's store has more stones.
     * @param board the board
     */
    private void determineWinner(MancalaBoard board) {
        int p1Stones = board.getHomeBin(true).getNumStones();
        int p2Stones = board.getHomeBin(false).getNumStones();
        if (p1Stones > p2Stones) {
            getPlayers().get(0).setWon(true);
        }
        else {
            getPlayers().get(1).setWon(true);
        }
    }

    /**
     * the first move of the game (made by the computer)
     */
    @Override
    public void computerMovesFirst() {
        Location loc = new ByteLocation(1, 1);
        MancalaBoard board = (MancalaBoard) getBoard();
        TwoPlayerMove move = MancalaMove.createMove(true, loc, 0, board.getBin(loc));
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