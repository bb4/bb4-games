/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.multiplayer.trivial.player.TrivialPlayer;

/**
 * Representation of a Trivial Game Board
 *
 * @author Barry Becker
 */
public class TrivialTable extends Board {

    /** size of a players marker */
    private static final double RADIUS = 0.65;

    /**
     * constructor
     *  @param numRows num rows
     *  @param numCols num cols
     */
    public TrivialTable( int numRows, int numCols )  {
        setSize( numRows, numCols );
    }

    /** Copy constructor */
    public TrivialTable(TrivialTable table) {
        super(table);
    }

    @Override
    public TrivialTable copy() {
        return new TrivialTable(this);
    }

    /**
     * A trivial game has no real limit so we just return a huge number.
     * @return max number of trivial rounds allowed.
     */
    @Override
    public int getMaxNumMoves() {
        return 1000;
    }

    /**
     * place the players around the trivial table
     */
    public void initPlayers(PlayerList players) {
        double angle = 0.6 * Math.PI;
        double angleIncrement = 2.0 * Math.PI / (players.size());
        double rowRad = getNumRows() >> 1;
        double colRad = getNumCols() >> 1;
        reset();

        for (Player p : players) {
            TrivialPlayer tp = (TrivialPlayer) p.getActualPlayer();

            int row = (int) (0.93 * rowRad + (RADIUS * rowRad) * (Math.sin(angle)));
            int col = (int) (0.9 * colRad + (RADIUS * colRad) * (Math.cos(angle)));

            BoardPosition position = getPosition(row, col);
            position.setPiece(tp.getPiece());
            tp.getPiece().setLocation(position.getLocation());
            angle += angleIncrement;
        }
    }

    /**
     * given a move specification, execute it on the board
     *
     * @param move the move to make, if possible.
     * @return false if the move is illegal.
     */
    @Override
    protected boolean makeInternalMove( Move move ) {
        return true;
    }

    /**
     * For Trivial, undoing a move means turning time back a round and
     * restoring the state of the game one full round earlier
     * TODO
     */
    @Override
    protected void undoInternalMove( Move move ) {
        GameContext.log(0,  "undo not implemented yet for Trivial." );
    }
}
