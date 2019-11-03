/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers;

import com.barrybecker4.game.common.GameWeights;

/**
 * These weights determine how the computer values features of the board
 * if only one computer is playing, then only one of the weights arrays is used.
 *
 * @author Barry Becker
 */
class CheckersWeights extends GameWeights {

    /**  Use these weights if no others are provided. */
    private static final double[] DEFAULT_WEIGHTS = {21.0, 29.0, 1.5};

    /** don't allow the weights to exceed these maximum values */
    private static final double[] MAX_WEIGHTS = {80.0, 200.0, 20.0};

    /** don't allow the weights to go below these minimum values */
    private static final double[] MIN_WEIGHTS = {1.0, 10.0, 0.0};

    private static final String[] WEIGHT_SHORT_DESCRIPTIONS = {
        "PawnMove weight",
        "King weight",
        "Advancement weight"
    };

    private static final String[] WEIGHT_DESCRIPTIONS = {
        "Weight to associate with the number of remaining pieces",
        "Weight to associate with the number of kings that a side has",
        "Weight to give associate with piece advancement"
    };

    static final int PIECE_WEIGHT_INDEX = 0;
    static final int KINGED_WEIGHT_INDEX = 1;
    static final int ADVANCEMENT_WEIGHT_INDEX = 2;


    public CheckersWeights() {
        super(DEFAULT_WEIGHTS, MIN_WEIGHTS, MAX_WEIGHTS, WEIGHT_SHORT_DESCRIPTIONS, WEIGHT_DESCRIPTIONS );
    }
}
