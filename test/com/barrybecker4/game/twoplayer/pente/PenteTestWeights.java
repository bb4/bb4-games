// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente;

import com.barrybecker4.game.twoplayer.pente.pattern.PenteWeights;

/**
 * These weights will stay fixed even if PenteWeights change.
 *
 * @author Barry Becker
 */
public class PenteTestWeights extends PenteWeights {

    private static final double[] TEST_WEIGHTS = {
        0.0,    0.0,    0.0,    0.5,    2.0,    8.0,
        JEOPARDY_WEIGHT,     JEOPARDY_WEIGHT + 7.0, ASSUMED_WINNING_VALUE,
        2.1 * ASSUMED_WINNING_VALUE,  2.1 * ASSUMED_WINNING_VALUE,  3 * ASSUMED_WINNING_VALUE
    };

    public PenteTestWeights() {
        super( TEST_WEIGHTS);
    }

}
