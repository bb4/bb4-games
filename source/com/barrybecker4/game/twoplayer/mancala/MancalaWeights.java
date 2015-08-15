/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala;

import com.barrybecker4.game.common.GameWeights;

/**
 * These weights determine how the computer values each board position.
 * If only one computer is playing, then only one of the weights arrays is used.
 *
 * @author Barry Becker
 */
public class MancalaWeights extends GameWeights {

    public static final int STORAGE_WEIGHT_INDEX = 0;

    /** If greater than this threshold, then opponent is in jeopardy. */
    public static final int JEOPARDY_WEIGHT = 80;

    /**
     * These defaults may be overridden in by the user in the UI.
     */
    private static final double[] DEFAULT_WEIGHTS = {
        1.0
    };

    /** Don't allow the weights to exceed these maximum values. Upper limit. */
    private static final double[] MAX_WEIGHTS = {
        10.0
    };

     /** Don't allow the weights to go below these minimum values. Upper limit. */
    private static final double[] MIN_WEIGHTS = {
        0.0
    };

    private static final String[] WEIGHT_SHORT_DESCRIPTIONS = {
        "storage weight"
    };

    private static final String[] WEIGHT_DESCRIPTIONS = {
        "The difference between player one's stones in storage versus player two's"
    };

    public MancalaWeights() {
        this(DEFAULT_WEIGHTS);
    }

    public MancalaWeights(double[] weights) {
        super( weights, MIN_WEIGHTS, MAX_WEIGHTS, WEIGHT_SHORT_DESCRIPTIONS, WEIGHT_DESCRIPTIONS );
    }

}
