/* Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex;

import com.barrybecker4.game.common.GameWeights;

/**
 * These weights determine how the computer values each pattern.
 * These weights determine how the computer values features of the board relative to each other.
 * If only one computer is playing, then only one of the weights arrays is used.
 * Use these weights if no others are provided.
 */
public class HexWeights extends GameWeights {

    public static final int CENTRICITY_INDEX = 0;
    public static final int PATH_COST_INDEX = 1;

    /** If greater than this threshold, then opponent is in jeopardy. */
    public static final int JEOPARDY_WEIGHT = 5;

    /** These defaults may be overridden in by the user in the UI. */
    private static final double[] DEFAULT_WEIGHTS = {
         0.1,   JEOPARDY_WEIGHT,  2 * ASSUMED_WINNING_VALUE
    };

    /** Don't allow the weights to go below these minimum values. Upper limit. */
    private static final double[] MIN_WEIGHTS = {
         0.0,       1.0,             ASSUMED_WINNING_VALUE
    };

    /** Don't allow the weights to exceed these maximum values. Upper limit. */
    private static final double[] MAX_WEIGHTS = {
        50.0,      500.0,         3 * ASSUMED_WINNING_VALUE
    };


    private static final String[] WEIGHT_SHORT_DESCRIPTIONS = {
        "centricity", "linkProximity",  "fullPath"};

    private static final String[] WEIGHT_DESCRIPTIONS = {
        "Control of the center",
        "Number of blank spaces need to make full path",
        "Has a path that spsns the board"
    };

    public HexWeights() {
        super( DEFAULT_WEIGHTS,  MIN_WEIGHTS, MAX_WEIGHTS, WEIGHT_SHORT_DESCRIPTIONS, WEIGHT_DESCRIPTIONS );
    }

}
