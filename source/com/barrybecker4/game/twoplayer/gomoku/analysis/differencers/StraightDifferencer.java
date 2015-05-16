// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis.differencers;

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.gomoku.analysis.Direction;
import com.barrybecker4.game.twoplayer.gomoku.analysis.Line;
import com.barrybecker4.game.twoplayer.gomoku.analysis.LineFactory;
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Determines the difference in value between the most recent move
 * and how it was before in the up/down or left right direction.
 *
 * @author Barry Becker
*/
public class StraightDifferencer extends ValueDifferencer {

    private boolean isVertical;


    public StraightDifferencer(TwoPlayerBoard board, Patterns patterns,
                               LineFactory lineFactory, Direction dir) {
        super(board, patterns, lineFactory);
        assert (dir == Direction.VERTICAL || dir == Direction.HORIZONTAL) : "unexpected direction" ;
        isVertical = (dir == Direction.VERTICAL);
    }


    @Override
    public int findValueDifference(int row, int col, ParameterArray weights) {

        int numMax = isVertical ? board_.getNumRows() : board_.getNumCols();
        int currentPos = isVertical ? row :  col;
        int start = currentPos - winLength;
        if ( start < 1 ) {
            start = 1;
        }
        int stop = currentPos + winLength;
        if ( stop > numMax ) {
            stop = numMax;
        }

        Line line = lineFactory_.createLine(patterns_, weights);
        for (int i = start; i <= stop; i++ ) {
            if (isVertical) {
                line.append(board_.getPosition(i, col));
            } else {
                line.append(board_.getPosition(row, i));
            }
        }

        int position = currentPos - start;
        return line.computeValueDifference(position);
    }
}