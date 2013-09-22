/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.pente;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
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
public class PenteSearchable extends TwoPlayerSearchable {

    private MoveEvaluator moveEvaluator_;
    private PenteMoveGenerator generator;

    /** Constructor */
    public PenteSearchable(TwoPlayerBoard board, PlayerList players) {
        super(board, players);
        init();
    }

    public PenteSearchable(PenteSearchable searchable) {
        super(searchable);
        init();
    }

    @Override
    public PenteSearchable copy() {
        return new PenteSearchable(this);
    }

    @Override
    public PenteBoard getBoard() {
        return (PenteBoard) board_;
    }

    private void init() {
        generator = new PenteMoveGenerator(this);
        moveEvaluator_ = new MoveEvaluator(board_, createPatterns());
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
    public int worth( TwoPlayerMove lastMove, ParameterArray weights ) {
        return moveEvaluator_.worth(lastMove, weights);
    }

    /**
     * generate all possible next moves.
     */
    @Override
    public MoveList generateMoves(TwoPlayerMove lastMove,
                                  ParameterArray weights) {
        return generator.generateMoves(lastMove, weights);
    }

    /**
     * Consider both our moves and opponent moves that result in wins.
     * Opponent moves that result in a win should be blocked.
     * @return Set of moves the moves that result in a certain win or a certain loss.
     */
    @Override
    public MoveList generateUrgentMoves(TwoPlayerMove lastMove, ParameterArray weights) {
        return generator.generateUrgentMoves(lastMove, weights);
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