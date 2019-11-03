/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.pattern;

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
public class GoMokuWeights extends GameWeights {

    /** If greater than this threshold, then opponent is in jeopardy. */
    public static final int JEOPARDY_WEIGHT = 80;

    /**
     * These defaults may be overridden in by the user in the UI.
     *
     * I ran an optimization at lookahead=6 and these were the optimized weights found:
     * parameter[0] = 1a weight = 0.035 [0, 40.0]
       parameter[1] = 1b weight = 0 [0, 40.0];
       parameter[2] = 1c weight = 0 [0, 40.0];
       parameter[3] = 2a weight = 2.0 [0, 80.0];
       parameter[4] = 2b weight = 8.0 [0, 160.0];
       parameter[5] = 3a weight = 32.0 [4.0, 320.0];
       parameter[6] = 3b weight = 320.0 [4.0, 800.0];
       parameter[7] = 4a weight = 348.0 [4.0, 800.0];
       parameter[8] = 4b weight = 4,915 [40.0, 8,192];
       parameter[9] = 5 weight = 8,602 [4,096, 12,288];
       parameter[10] = 6 weight =8,602 [4,096, 16,384];
       parameter[11] = 7 weight =12,288 [4,096, 20,480];
     */
    private static final double[] DEFAULT_WEIGHTS = {
        0.0,    0.0,    0.0,    0.5,    2.0,    8.0,
        JEOPARDY_WEIGHT,     JEOPARDY_WEIGHT + 7.0, 1.2 * ASSUMED_WINNING_VALUE,
        2.1 * ASSUMED_WINNING_VALUE,  2.1 * ASSUMED_WINNING_VALUE,  3 * ASSUMED_WINNING_VALUE
    };

    /** Don't allow the weights to exceed these maximum values. Upper limit. */
    private static final double[] MAX_WEIGHTS = {
        10.0,   10.0,   10.0,    20.0,    JEOPARDY_WEIGHT/2,  JEOPARDY_WEIGHT,
        2*JEOPARDY_WEIGHT,         2*JEOPARDY_WEIGHT,     2*ASSUMED_WINNING_VALUE,
        3 * ASSUMED_WINNING_VALUE,  4 * ASSUMED_WINNING_VALUE,  5 * ASSUMED_WINNING_VALUE
    };

     /** Don't allow the weights to go below these minimum values. Upper limit. */
    private static final double[] MIN_WEIGHTS = {
        0.0,    0.0,    0.0,    0.0,     0.0,     1.0,       1.0,        1.0,        10.0,
        ASSUMED_WINNING_VALUE,  ASSUMED_WINNING_VALUE,  ASSUMED_WINNING_VALUE
    };

    private static final String[] WEIGHT_SHORT_DESCRIPTIONS = {
        "1a weight", "1b weight", "1c weight", "2a weight",
        "2b weight", "3a weight", "3b weight", "4a  weight",
        "5 weight", "6 weight", "7 weight", "8 weight"};


    private static final String[] WEIGHT_DESCRIPTIONS = {
        "Open ended two in a row (_XX)",            // 0
        "Closed three in a row (XXX)",                    // 1
        "Three in a row with chance to make 4 (_XXX or X_XX)",   // 2
        "Can be blocked after next move (_X_X, X_X_X)",               // 3
        "Chance to have win after next move (_XX_, _X_X_, _X_XX, _XX_X)",  // 4
        "Likely win if play next (_XXX_, _X_XX_, _X_XX_X ...)",                   // 5
        "Guaranteed to win on next move (_XXXX, X_XXX, XX_XX, _X_XXX, _XX_XX, _XXX_X, ...)",  // 6
        "Guaranteed win even if not moving next (_XXXX_, _XXXX_X, ...)",              // 7
        "Already won. Arrangements of 5 in a row weight",                        // 8
        "Already won. Arrangements of 6 in a row weight",                    // 9
        "Already won. Arrangements of 7 in a row weight",                // 10
        "Already won. Arrangements of 8 in a row weight"            // 11
    };

    public GoMokuWeights() {
        this(DEFAULT_WEIGHTS);
    }

    public GoMokuWeights(double[] weights) {
        super( weights,  MIN_WEIGHTS, MAX_WEIGHTS, WEIGHT_SHORT_DESCRIPTIONS, WEIGHT_DESCRIPTIONS );
    }

}
