/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerSearchable;
import com.barrybecker4.optimization.parameter.ParameterArray;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;

/**
 * Defines how the computer should play checkers.
 *
 * @author Barry Becker
 */
public class CheckersSearchable extends TwoPlayerSearchable {

    /**
     * Constructor
     */
    public CheckersSearchable(TwoPlayerBoard board, PlayerList players) {
        super(board, players);
    }

    public CheckersSearchable(CheckersSearchable searchable) {
        super(searchable);
    }

    @Override
    public CheckersSearchable copy() {
        return new CheckersSearchable(this);
    }

    /**
     * Generate all possible next moves
     */
    @Override
    public MoveList generateMoves(TwoPlayerMove lastMove, ParameterArray weights) {

        MoveGenerator generator = new MoveGenerator(this, weights);
        boolean player1 = (lastMove == null) || !lastMove.isPlayer1();

        MoveList moveList = generator.generateMoves(lastMove);

        return bestMoveFinder_.getBestMoves( player1, moveList);
    }

    /**
     * given a move determine whether the game is over.
     * If we are at maxMoves, the one with a greater value of pieces wins.
     * If the count of pieces is the same, then it is a draw.
     *
     * @param move the move to check. If null that implies there was no last move because we are out of moves.
     * @param recordWin if true then the controller state will record wins
     * @return true if the game is over.
     */
    @Override
    public boolean done( TwoPlayerMove move, boolean recordWin ) {

        if (move == null)  {
            // for checkers this means that the other player won.
            if (recordWin)  {
                TwoPlayerMove lastMove = (TwoPlayerMove) getMoveList().getLastMove();
                recordPlayerWin(lastMove.isPlayer1());
            }
            return true;
        }

        boolean won = (Math.abs( move.getValue() ) >= SearchStrategy.WINNING_VALUE);

        if ( won && recordWin ) {
            recordPlayerWin(move.isPlayer1());
        }

        if ( getNumMoves() >= getBoard().getMaxNumMoves() ) {
            won = true;
            if ( recordWin ) {
                recordPlayerWin(Math.abs(move.getValue()) >= 0);
            }
        }
        return (won);
    }

    /**
     * @param p1Won true if player 1 should be marked the winner
     */
    private void recordPlayerWin(boolean p1Won) {
        if (p1Won) {
            players_.getPlayer1().setWon(true);
        } else {
            players_.getPlayer2().setWon(true);
        }
    }

    /**
     * lastMove not used.
     * @return the value of the current board position
     */
    @Override
    public int worth( TwoPlayerMove lastMove, ParameterArray weights ) {
        return new BoardEvaluator(board_, weights).calculateWorth();
    }

    /**
     * Quiescent search not yet implemented for checkers
     * Probably we should return all moves that capture opponent pieces.
     *
     * @return list of urgent moves
     */
    @Override
    public MoveList generateUrgentMoves(
            TwoPlayerMove lastMove, ParameterArray weights) {
        return new MoveList();
    }
}
