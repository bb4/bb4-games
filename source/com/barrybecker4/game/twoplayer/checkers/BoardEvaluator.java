/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers;

import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.optimization.parameter.ParameterArray;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;

/**
 * Determine the worth of a specific board position.
 *
 * @author Barry Becker
 */
public class BoardEvaluator {

    TwoPlayerBoard board;
    ParameterArray weights;

    /**
     * Constructor
     */
    public BoardEvaluator(TwoPlayerBoard board, ParameterArray weights) {
        this.board = board;
        this.weights = weights;
    }

    /**
     *  The primary way of computing the score for checkers is to just add up the pieces
     *  Kings should count more heavily. How much more is determined by the weights.
     *  We also give a slight bonus for advancement of non-kings to incentivize them to
     *  become kings.
     *  note: lastMove is not used
     *  @return the value of the current board position
     *   a positive value means that player1 has the advantage.
     *   A big negative value means a good move for p2.
     */
    public int calculateWorth() {
        int row, col, odd;
        Score score = new Score();

        for ( row = 1; row <= CheckersBoard.SIZE; row++ ) {
            odd = row % 2;
            for ( int j = 1; j <= CheckersBoard.SIZE/2; j++ ) {
                col = 2 * j - odd;
                accumulateScore(row, score, board.getPosition(row, col));
            }
        }

        return score.finalScore();
    }

    private void accumulateScore(int row, Score score, BoardPosition p) {
        if ( p.isOccupied() ) {
            CheckersPiece piece = (CheckersPiece) p.getPiece();
            boolean isPlayer1 = piece.isOwnedByPlayer1();
            int advancement = isPlayer1? row : CheckersBoard.SIZE - row;
            int pieceScore = calcPieceScore(piece.isKing(), advancement);
            if (isPlayer1) {
                score.positive += pieceScore;
            }
            else {
                score.negative -= pieceScore;
            }
        }
    }

    /**
     * @return the score for a particular piece.
     */
    private int calcPieceScore(boolean isKing, int advancement) {
        int score = 0;
        if (isKing) {
               score += weights.get(CheckersWeights.KINGED_WEIGHT_INDEX).getValue();
        }
        else { // REGULAR_PIECE
               score += weights.get(CheckersWeights.PIECE_WEIGHT_INDEX).getValue();
               score += weights.get(CheckersWeights.ADVANCEMENT_WEIGHT_INDEX).getValue() * advancement;
        }
        return score;
    }

    /** Keeps track of positive and negative scores for each player. */
    private static final class Score {
        float positive;
        float negative;

        private int sum() {
            return (int)(positive + negative);
        }

        int finalScore() {
            if (positive == 0) {
                // then there are no more of player 1's pieces
                return -SearchStrategy.WINNING_VALUE;
            }
            if ( negative == 0 ) {
                // then there is no more of player 2's pieces
                return SearchStrategy.WINNING_VALUE;
            }
            return sum();
        }
    }
}
