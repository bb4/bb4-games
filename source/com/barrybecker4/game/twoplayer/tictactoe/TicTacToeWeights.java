/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe;

import com.barrybecker4.game.common.GameWeights;

/**
 * These weights determine how the computer values each pattern
 * if only one computer is playing, then only one of the weights arrays is used.
 *
 * These weights determine how the computer values features of the board
 * if only one computer is playing, then only one of the weights arrays is used.
 * use these weights if no others are provided.
 *
 * @author Barry Becker
 */
public class TicTacToeWeights extends GameWeights {

    /** If greater than this threshold, then opponent is in jeopardy. */
    public static final int JEOPARDY_WEIGHT = 10;

    /** These defaults may be overridden in by the user in the UI. */
    private static final double[] DEFAULT_WEIGHTS = {
        1.0,    1.0,   JEOPARDY_WEIGHT + 1,  JEOPARDY_WEIGHT,  2 * ASSUMED_WINNING_VALUE
    };

    /** Don't allow the weights to go below these minimum values. Upper limit. */
    private static final double[] MIN_WEIGHTS = {
        0.0,    0.0,    1.0,                 1.0,             ASSUMED_WINNING_VALUE
    };

    /** Don't allow the weights to exceed these maximum values. Upper limit. */
    private static final double[] MAX_WEIGHTS = {
        50.0,   50.0,   500.0,              500.0,         3 * ASSUMED_WINNING_VALUE
    };


    private static final String[] WEIGHT_SHORT_DESCRIPTIONS = {
        "1a weight", "1b weight",  "2a weight", "2b weight", "3 weight"};

    private static final String[] WEIGHT_DESCRIPTIONS = {
        "1 in a row weight (X__)",             // X__
        "1 in a row weight (_X_)",             // _X_
        "open 2 in a row weight (XX_)",        // XX_
        "open of 2 in a row weight (X_X)",     // X_X
        "3 in a row weight (XXX)",             // XXX
    };

    public TicTacToeWeights() {
        super( DEFAULT_WEIGHTS,  MIN_WEIGHTS, MAX_WEIGHTS, WEIGHT_SHORT_DESCRIPTIONS, WEIGHT_DESCRIPTIONS );
    }

}
