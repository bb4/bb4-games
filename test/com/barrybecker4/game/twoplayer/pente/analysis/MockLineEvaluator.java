// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente.analysis;

import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.pente.pattern.PentePatterns;

/**
 * Represents a run of symbols to be evaluated on the board.
 * @author Barry Becker
 */
public class MockLineEvaluator extends LineEvaluator {

    int minInteresting;

    /**
     * Constructor
     */
    public MockLineEvaluator(int minInteresting) {
        super(null, null);
        this.minInteresting = minInteresting;
    }


    @Override
    public int getMinInterestingLength() {
        return minInteresting;
    }

    /**
     * Return one if the move just placed is our symbol, else return -1.
     */
    @Override
    public int evaluate(CharSequence line, boolean player1Perspective, int pos, int minpos, int maxpos) {

        char symb = line.charAt( pos );
        if (symb == PentePatterns.UNOCCUPIED)  {
            return 0;
        }
        char opponentSymb = player1Perspective ? GamePiece.P2_SYMB : GamePiece.P1_SYMB;
        int sign =  (symb == opponentSymb) ? -1 : 1;
        return player1Perspective? sign: -sign;
    }

}
