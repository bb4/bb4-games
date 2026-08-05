// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku;

import com.barrybecker4.game.common.GameWeights;
import com.barrybecker4.game.twoplayer.gomoku.pattern.GoMokuWeights;

/**
 * These weights will stay fixed even if GoMokuWeights change.
 *
 * @author Barry Becker
 */
public class GoMokuTestWeights extends GoMokuWeights {

    private static final double[] TEST_WEIGHTS = {
        0.0,    0.0,    0.0,    0.5,    2.0,    8.0,
        GoMokuWeights.JEOPARDY_WEIGHT(),     GoMokuWeights.JEOPARDY_WEIGHT() + 7.0, GameWeights.ASSUMED_WINNING_VALUE(),
        2.1 * GameWeights.ASSUMED_WINNING_VALUE(),  2.1 * GameWeights.ASSUMED_WINNING_VALUE(),  3 * GameWeights.ASSUMED_WINNING_VALUE()
    };

    public GoMokuTestWeights() {
        super( TEST_WEIGHTS);
    }

}
