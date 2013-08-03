/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.pente.analysis;

import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.pente.analysis.differencers.ValueDifferencer;
import com.barrybecker4.game.twoplayer.pente.analysis.differencers.ValueDifferencerFactory;
import com.barrybecker4.game.twoplayer.pente.pattern.Patterns;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Evaluates the value a recent move.
 * It does this by comparing the change in worth of the line patterns
 * vertically, horizontally, and diagonally through the new move's position.
 *
 * @author Barry Becker
*/
public class MoveEvaluator  {

    private TwoPlayerBoard board_;

    private ValueDifferencer vertDifferencer;
    private ValueDifferencer horzDifferencer;
    private ValueDifferencer upDiagDifferencer;
    private ValueDifferencer downDiagDifferencer;

    /**
     * Constructor
     */
    public MoveEvaluator(TwoPlayerBoard board, Patterns patterns) {
        board_ = board;
        setValueDifferencerFactory(
                new ValueDifferencerFactory(board_, patterns, new LineFactory()));
    }

    /**
     * Used for testing to inject something that will create mock differencers.
     */
    public void setValueDifferencerFactory(ValueDifferencerFactory differencerFactory) {

        vertDifferencer = differencerFactory.createValueDifferencer(Direction.VERTICAL);
        horzDifferencer = differencerFactory.createValueDifferencer(Direction.HORIZONTAL);
        upDiagDifferencer = differencerFactory.createValueDifferencer(Direction.UP_DIAGONAL);
        downDiagDifferencer = differencerFactory.createValueDifferencer(Direction.DOWN_DIAGONAL);
    }

    /**
     * Statically evaluate the board position.
     * @return the lastMove's value modified by the value add of the new move.
     *  a large positive value means that the move is good from player1's viewpoint.
     */
    public int worth( TwoPlayerMove lastMove, ParameterArray weights ) {

        int row = lastMove.getToRow();
        int col = lastMove.getToCol();
        GamePiece piece = board_.getPosition(row, col).getPiece();
        assert piece != null :
                "There must be a piece where the last move was played (" + row+", " + col + ")";
        assert (lastMove.isPlayer1() == piece.isOwnedByPlayer1()) :
                "The last move played must be for the same player found on the board.";

        // look at every string that passes through this new move to see how the value is effected.
        int diff;
        diff = horzDifferencer.findValueDifference(row, col, weights);
        diff += vertDifferencer.findValueDifference(row, col, weights);
        diff += upDiagDifferencer.findValueDifference(row, col, weights);
        diff += downDiagDifferencer.findValueDifference(row, col, weights);

        return (lastMove.getValue() + diff);
    }
}
