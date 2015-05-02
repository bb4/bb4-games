/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.pente;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerSearchable;
import com.barrybecker4.game.twoplayer.pente.analysis.MoveEvaluator;
import com.barrybecker4.game.twoplayer.pente.pattern.Patterns;
import com.barrybecker4.game.twoplayer.pente.pattern.PentePatterns;
import com.barrybecker4.game.twoplayer.pente.pattern.PenteWeights;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Defines everything the computer needs to know to search for the next pente move.
 *
 * @author Barry Becker
*/
public class PenteSearchable<B extends PenteBoard> extends TwoPlayerSearchable<TwoPlayerMove, B> {

    private MoveEvaluator moveEvaluator;
    private PenteMoveGenerator generator;

    /** Constructor */
    public PenteSearchable(B board, PlayerList players) {
        super(board, players);
        init();
    }

    public PenteSearchable(PenteSearchable<B> searchable) {
        super(searchable);
        init();
    }

    private void init() {
        generator = new PenteMoveGenerator();
        moveEvaluator = new MoveEvaluator(createPatterns());
    }

    @Override
    public PenteSearchable<B> copy() {
        return new PenteSearchable<>(this);
    }

    @Override
    public B getBoard() {
        return board_;
    }

    protected Patterns createPatterns() {
        return new PentePatterns();
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
    public MoveList<TwoPlayerMove> generateMoves(TwoPlayerMove lastMove,
                                  ParameterArray weights) {
        return generator.generateMoves(this, lastMove, weights);
    }

    /**
     * Consider both our moves and opponent moves that result in wins.
     * Opponent moves that result in a win should be blocked.
     * @return Set of moves the moves that result in a certain win or a certain loss.
     */
    @Override
    public MoveList<TwoPlayerMove> generateUrgentMoves(TwoPlayerMove lastMove, ParameterArray weights) {
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
        return PenteWeights.JEOPARDY_WEIGHT;
    }
}