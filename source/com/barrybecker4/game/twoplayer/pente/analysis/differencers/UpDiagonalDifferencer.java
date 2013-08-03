// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente.analysis.differencers;

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.pente.analysis.Line;
import com.barrybecker4.game.twoplayer.pente.analysis.LineFactory;
import com.barrybecker4.game.twoplayer.pente.pattern.Patterns;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Determines the difference in value between the most recent move
 * and how it was before in the upward diagonal direction.
 *
 * @author Barry Becker
*/
public class UpDiagonalDifferencer extends DiagonalDifferencer {


    public UpDiagonalDifferencer(TwoPlayerBoard board, Patterns patterns,
                                 LineFactory lineFactory) {
        super(board, patterns, lineFactory);
    }

    @Override
    public int findValueDifference(int row, int col, ParameterArray weights) {

        init();
        int startc = col - winLength;
        int startr = row + winLength;
        if ( startc < 1 ) {
            startr += startc - 1;
            startc = 1;
        }
        if ( startr > numRows ) {
            startc += startr - numRows;
            startr = numRows;
        }
        int stopc = col + winLength;
        int stopr = row - winLength;
        if ( stopc > numCols ) {
            stopr +=  + stopc - numCols;
            stopc = numCols;
        }
        if ( stopr < 1 ) {
            stopc += stopr - 1;
        }
        Line line = lineFactory_.createLine(patterns_, weights);
        for (int i = startc; i <= stopc; i++ )
            line.append( board_.getPosition( startr - i + startc, i ) );

        int position = col - startc;
        return line.computeValueDifference(position);
        //line.worthDebug(Direction.UP_DIAGONAL.name(), position, diff);
    }
}