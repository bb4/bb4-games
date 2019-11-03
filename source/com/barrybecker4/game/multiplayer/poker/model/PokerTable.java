/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.model;

import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;

/**
 * Representation of a Poker Game Board
 *
 * @author Barry Becker
 */
public class PokerTable extends Board {

    /** size of a players marker  */
    private static final double RADIUS = 0.65;

    private PokerRound round;


    /**
     * Constructor
     * @param numRows num rows
     * @param numCols num cols
     */
    public PokerTable( int numRows, int numCols )  {
        setSize( numRows, numCols );
        round = new PokerRound();
    }

    /** Copy constructor */
    public PokerTable(PokerTable table) {
        super(table);
        round = new PokerRound();
        round.addToPot(table.getPotValue());
    }

    @Override
    public PokerTable copy() {
        return new PokerTable(this);
    }

    public PokerRound getRound() {
        return round;
    }

    public int getPotValue() {
        return round.getPotValue();
    }
    /**
     * A poker game has no real limit so we just return a huge number.
     * @return max number of poker rounds allowed.
     */
    @Override
    public int getMaxNumMoves() {
        return 100000;
    }

    /**
     * place the players around the poker table
     * @param players players to initialize
     */
    public void initPlayers(PlayerList players) {
        double angle = 0.6 * Math.PI;
        double angleIncrement = 2.0 * Math.PI / (players.size());
        double rowRad = getNumRows() >> 1;
        double colRad = getNumCols() >> 1;
        reset();
        for (final Player p : players) {

            PokerPlayer pp = (PokerPlayer) p.getActualPlayer();

            int row = (int) (0.93 * rowRad + (RADIUS * rowRad) * (Math.sin(angle)));
            int col = (int) (0.9 * colRad + (RADIUS * colRad) * (Math.cos(angle)));

            BoardPosition position = getPosition(row, col);
            position.setPiece(pp.getPiece());
            pp.getPiece().setLocation(position.getLocation());
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
     * For Poker, undoing a move means turning time back a round and
     * restoring the state of the game one full round earlier
     */
    @Override
    protected void undoInternalMove( Move move ) {
        throw new UnsupportedOperationException("undo no implemented yet for poker." );
    }

}
