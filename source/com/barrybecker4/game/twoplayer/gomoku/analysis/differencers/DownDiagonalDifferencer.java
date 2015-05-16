// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis.differencers;

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.gomoku.analysis.Line;
import com.barrybecker4.game.twoplayer.gomoku.analysis.LineFactory;
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Determines the difference in value between the most recent move
 * and how it was before in the downward diagonal direction.
 *
 * @author Barry Becker
*/
public class DownDiagonalDifferencer extends DiagonalDifferencer {


    public DownDiagonalDifferencer(TwoPlayerBoard board, Patterns patterns,
                                   LineFactory lineFactory) {
        super(board, patterns, lineFactory);
    }

    @Override
    public int findValueDifference(int row, int col, ParameterArray weights) {

        init();
        int startc = col - winLength;
        int startr = row - winLength;
        if ( startc < 1 ) {
            startr += (1 - startc);
            startc = 1;
        }
        if ( startr < 1 ) {
            startc += (1 - startr);
            startr = 1;
        }
        int stopc = col + winLength;
        int stopr = row + winLength;
        if ( stopc > numCols ) {
            stopr += (numCols - stopc);
        }
        if ( stopr > numRows ) {
            stopr = numRows;
        }

        Line line = lineFactory_.createLine(patterns_, weights);
        for (int i = startr; i <= stopr; i++ )
            line.append( board_.getPosition( i, startc + i - startr ));

        int position = row - startr;
        return line.computeValueDifference(position);
        //line.worthDebug(Line.Direction.DOWN_DIAGONAL.name(), position, diff);
    }
}