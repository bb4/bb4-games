// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente.analysis.differencers;

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.pente.analysis.LineFactory;
import com.barrybecker4.game.twoplayer.pente.pattern.Patterns;

/**
 * Determines the difference in value between the most recent move
 * and how it was before in the up/down or left right direction.
 *
 * @author Barry Becker
*/
abstract class DiagonalDifferencer extends ValueDifferencer {

    protected int numRows;
    protected int numCols;


    DiagonalDifferencer(TwoPlayerBoard board, Patterns patterns,
                               LineFactory lineFactory) {
        super(board, patterns, lineFactory);
    }

    protected void init() {
        numRows = board_.getNumRows();
        numCols = board_.getNumCols();
    }
}