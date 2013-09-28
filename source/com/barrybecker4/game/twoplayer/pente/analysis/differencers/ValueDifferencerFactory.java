// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente.analysis.differencers;

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.pente.analysis.Direction;
import com.barrybecker4.game.twoplayer.pente.analysis.LineFactory;
import com.barrybecker4.game.twoplayer.pente.pattern.Patterns;

/**
 * Determines the difference in value between the most recent move
 * and how it was before in one specific direction
 *
 * @author Barry Becker
*/
public class ValueDifferencerFactory {

    protected Patterns patterns_;
    protected LineFactory lineFactory_;

    /**
     * Constructor
     */
    public ValueDifferencerFactory( Patterns patterns, LineFactory factory) {
        patterns_ = patterns;
        lineFactory_ = factory;
    }

    /**
     * @param dir the direction used to decide which differencer to create.
     * @return differencer for the specified direction.
     */
    public ValueDifferencer createValueDifferencer(TwoPlayerBoard board, Direction dir) {
        ValueDifferencer differencer = null;
        switch (dir) {
            case VERTICAL:
            case HORIZONTAL:
                differencer = new StraightDifferencer(board, patterns_, lineFactory_, dir);
                break;
            case UP_DIAGONAL:
                differencer = new UpDiagonalDifferencer(board, patterns_, lineFactory_);
                break;
            case DOWN_DIAGONAL:
                differencer = new DownDiagonalDifferencer(board, patterns_, lineFactory_);
                break;
        }
        return differencer;
    }
}
