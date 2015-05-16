// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis;

import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Test that we can correctly evaluate a run of symbols on the board.
 * @author Barry Becker
 */
public class LineEvaluator {

    protected Patterns patterns_;
    private ParameterArray weights_;

    /**
     * Constructor
     * @param patterns patterns to lookout for.
     * @param weights weights amount to weight different patterns found in line.
     */
    public LineEvaluator(Patterns patterns, ParameterArray weights) {
        patterns_ = patterns;
        weights_ = weights;
    }

    public int getMinInterestingLength() {
        return patterns_.getMinInterestingLength();
    }

    /**
     * Evaluate a line (vertical, horizontal, or diagonal) from the
     * specified player point of view.
     *
     * @param line the line to evaluate
     * @param player1Perspective if true, then the first player just moved, else the second player.
     *   Note: this value does not guarantee anything about the symbol at position pos.
     *   It can be either players symbol or unoccupied.
     * @param pos the position that was just played (symbol).
     * @param minpos starting pattern index in line (usually 0).
     * @param maxpos last pattern index position in line (usually one less than the line magnitude).
     * @return the worth of a (vertical, horizontal, left diagonal, or right diagonal) line.
     */
    public int evaluate(CharSequence line, boolean player1Perspective, int pos, int minpos, int maxpos) {

        assert pos >= minpos && pos <= maxpos;
        int length = maxpos - minpos + 1;
        if ( length < patterns_.getMinInterestingLength() )  {
            return 0; // not an interesting pattern.
        }

        char opponentSymb = player1Perspective ? GamePiece.P2_SYMB : GamePiece.P1_SYMB;

        if ( (line.charAt( pos ) == opponentSymb)
                && pos != minpos && pos != maxpos ) {
            // First check for a special case where there was a blocking move in the
            // middle. In this case we break the string into an upper and lower
            // half and evaluate each separately.
            //System.out.println("eval sep " + line + " " + minpos + "-" + pos +  "   " + pos + "-" + maxpos);
            return (evaluate(line, player1Perspective, pos-1, minpos, pos-1)
                    + evaluate(line, player1Perspective, pos+1, pos+1, maxpos));
        }
        return getWeight(line, opponentSymb, pos, minpos, maxpos);
    }

    /**
     * @return the weight for the pattern if its a recognizable pattern, else return 0.
     */
    private int getWeight(CharSequence line, char opponentSymb, int pos, int minpos, int maxpos) {

        int index = getWeightIndex(line, opponentSymb, pos, minpos, maxpos);

        if (index >= 0) {
            int weight = (int)weights_.get(index).getValue();
            return (opponentSymb == GamePiece.P2_SYMB) ? weight : -weight;
        } else {
            return 0;
        }
    }

    /**
     * In general, we march from the position pos in the middle towards the ends of the
     * string. Marching stops when we encounter one of the following
     * conditions:
     *  - 2 blanks in a row (@@ we may want to allow this)
     *  - an opponent's blocking piece
     *  - the end of a line.
     * @param minpos first symbol in the sting to evaluate
     * @param maxpos last symbol in the sting to evaluate
     * @return the index to use for getting the weight based on the pattern formed by this line.
     */
    protected int getWeightIndex(CharSequence line, char opponentSymb, int pos, int minpos, int maxpos) {

        CharSequence pattern = getPattern(line, opponentSymb, pos, minpos, maxpos);
        //System.out.println("pattern=" + pattern);
        return patterns_.getWeightIndexForPattern(pattern);
    }

    protected CharSequence getPattern(CharSequence line, char opponentSymb, int pos, int minpos, int maxpos) {
        PatternExtractor extractor = new PatternExtractor(line);
        return extractor.getPattern(opponentSymb, pos, minpos, maxpos);
    }
}
