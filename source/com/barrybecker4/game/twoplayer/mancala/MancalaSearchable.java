/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerSearchable;
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard;
import com.barrybecker4.game.twoplayer.mancala.move.MancalaMoveGenerator;
import com.barrybecker4.game.twoplayer.mancala.move.MoveEvaluator;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Defines everything the computer needs to know to search for the next mancala move.
 *
 * @author Barry Becker
*/
public class MancalaSearchable extends TwoPlayerSearchable {

    private MoveEvaluator moveEvaluator;
    private MancalaMoveGenerator generator;

    /** Constructor */
    public MancalaSearchable(TwoPlayerBoard board, PlayerList players) {
        super(board, players);
        init();
    }

    public MancalaSearchable(MancalaSearchable searchable) {
        super(searchable);
        init();
    }

    private void init() {
        generator = new MancalaMoveGenerator();
        moveEvaluator = new MoveEvaluator();
    }

    @Override
    public MancalaSearchable copy() {
        return new MancalaSearchable(this);
    }

    @Override
    public MancalaBoard getBoard() {
        return (MancalaBoard) board_;
    }


    /**
     * Statically evaluate the board position.
     * @return the lastMoves value modified by the value add of the new move.
     *  a large positive value means that the move is good from player1's viewpoint
     */
    @Override
    public int worth(TwoPlayerMove lastMove, ParameterArray weights ) {
        return moveEvaluator.worth(getBoard(), lastMove, weights);
    }

    /**
     * generate all possible next moves.
     */
    @Override
    public MoveList generateMoves(TwoPlayerMove lastMove, ParameterArray weights) {
        return generator.generateMoves(this, lastMove, weights);
    }

    /**
     * Consider both our moves and opponent moves that result in wins.
     * Opponent moves that result in a win should be blocked.
     * @return Set of moves the moves that result in a certain win or a certain loss.
     */
    @Override
    public MoveList generateUrgentMoves(TwoPlayerMove lastMove, ParameterArray weights) {
        return generator.generateUrgentMoves(this, lastMove, weights);
    }

    /**
     * Consider the delta big if >= w. Where w is the value of a near win.
     * @return true if the last move created a big change in the score
     */
    @Override
    public boolean inJeopardy( TwoPlayerMove move, ParameterArray weights ) {
        if (move == null)
            return false;
        double newValue = worth(move, weights);
        double diff = newValue - move.getValue();
        return (diff > getJeopardyWeight());
    }

    protected int getJeopardyWeight()  {
        return MancalaWeights.JEOPARDY_WEIGHT;
    }
}