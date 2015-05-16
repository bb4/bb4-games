/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.pattern;

/**
 *  Simple patterns for use with unit tests.
 *
 *  @author Barry Becker
 */
public final class SimplePatterns extends Patterns
{
    private static String[] patternString = {
        "_X", "X_X", "_X_", "XX", "_XX", "_XX_"
    };

    /** index into weights array. */
    private static int[] weightIndex = {
        0, 0, 1, 2, 2, 2
    };

    public SimplePatterns() {
        initializePatterns();
    }

     /**
      * This is how many in a row are needed to win
      */
    @Override
    public int getWinRunLength() {
        return 2;
    }

    @Override
    protected int getNumPatterns() {
        return 6;
    }

    @Override
    public int getMinInterestingLength() {
        return 2;
    }


    @Override
    protected String getPatternString(int i) {
        return patternString[i];
    }

    @Override
    protected int getWeightIndex(int i) {
        return weightIndex[i];
    }
}