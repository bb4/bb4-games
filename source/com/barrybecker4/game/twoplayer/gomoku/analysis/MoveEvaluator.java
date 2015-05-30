/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.analysis;

import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.gomoku.GoMokuBoard;
import com.barrybecker4.game.twoplayer.gomoku.analysis.differencers.ValueDifferencer;
import com.barrybecker4.game.twoplayer.gomoku.analysis.differencers.ValueDifferencerFactory;
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Evaluates the value a recent move.
 * It does this by comparing the change in worth of the line patterns
 * vertically, horizontally, and diagonally through the new move's position.
 *
 * @author Barry Becker
*/
public class MoveEvaluator  {

    Patterns patterns;
    ValueDifferencerFactory differencerFactory;
    private ValueDifferencer vertDifferencer;
    private ValueDifferencer horzDifferencer;
    private ValueDifferencer upDiagDifferencer;
    private ValueDifferencer downDiagDifferencer;

    /**
     * Constructor
     */
    public MoveEvaluator(Patterns patterns) {
        this.patterns = patterns;
        this.differencerFactory = new ValueDifferencerFactory(patterns, new LineFactory());
    }

    /**
     * Used for testing to inject something that will create mock differencers.
     */
    public void setValueDifferencerFactory(ValueDifferencerFactory differencerFactory) {
         this.differencerFactory = differencerFactory;
    }

    private void createDifferencers(GoMokuBoard board) {

        vertDifferencer = differencerFactory.createValueDifferencer(board, Direction.VERTICAL);
        horzDifferencer = differencerFactory.createValueDifferencer(board, Direction.HORIZONTAL);
        upDiagDifferencer = differencerFactory.createValueDifferencer(board, Direction.UP_DIAGONAL);
        downDiagDifferencer = differencerFactory.createValueDifferencer(board, Direction.DOWN_DIAGONAL);
    }

    /**
     * Statically evaluate the board position from player1's point of view. Larger values are good for player1.
     * @return the lastMove's value modified by the value add of the new move.
     *  a large positive value means that the move is good from player1's viewpoint.
     */
    public int worth(GoMokuBoard board, TwoPlayerMove lastMove, ParameterArray weights ) {

        int row = lastMove.getToRow();
        int col = lastMove.getToCol();
        createDifferencers(board);

        GamePiece piece = board.getPosition(row, col).getPiece();
        if (piece == null) {
            throw new IllegalStateException(
                    "There must be a piece where the last move was played (" + row+", " + col + ")");
        }
            if (lastMove.isPlayer1() != piece.isOwnedByPlayer1()) {
            throw new IllegalStateException("The last move played must be for the same player found on the board.");
        }

        // look at every string that passes through this new move to see how the value is effected.
        int diff;
        diff = horzDifferencer.findValueDifference(row, col, weights);
        diff += vertDifferencer.findValueDifference(row, col, weights);
        diff += upDiagDifferencer.findValueDifference(row, col, weights);
        diff += downDiagDifferencer.findValueDifference(row, col, weights);

        return (lastMove.getValue() + diff);
    }
}
