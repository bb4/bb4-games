/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.pattern;

/**
 * Encapsulates the domain knowledge for n in a row game.
 * These are key patterns that can occur in the game and are weighted
 * by importance to let the computer play better.
 *
 * Do not add duplicate patterns or patterns that are the reverse of other patterns.
 *
 * @author Barry Becker
 */
public abstract class Patterns {

    /** a blank space on the game board. */
    public static final char UNOCCUPIED = '_';

    /**
     * This table provides a quick way to look up a weight for a pattern.
     */
    private PatternTable patternTable_;

    /**
     * Constructor.
     */
    public Patterns() {
        patternTable_ = new PatternTable();
        initializePatterns();
    }

    /**
     * @param pattern  pattern to get the weight index for.
     * @param minpos index of first character in pattern
     * @param maxpos index of last character position in pattern.
     * @return weight index
     */
    public int getWeightIndexForPattern(CharSequence pattern, int minpos, int maxpos) {
        return patternTable_.getWeightIndexForPattern(pattern, minpos, maxpos);
    }

    /**
     * @param pattern  pattern to get the weight index for.
     * @return weight index
     */
    public int getWeightIndexForPattern(CharSequence pattern) {
        return patternTable_.getWeightIndexForPattern(pattern, 0, pattern.length()-1);
    }

    /**
     * @return how many in a row are needed to win. If M is five then the game is gomoku
     */
    public abstract int getWinRunLength();

    /**
     * @return patterns shorter than this are not interesting and have weight 0
     */
    public abstract int getMinInterestingLength();

    /**
     * @return total number of patterns represented
     */
    protected abstract int getNumPatterns();

    /**
     * Initialize all the gomoku patterns.
     */
    protected void initializePatterns() {

        for ( int i = 0; i < getNumPatterns(); i++ ) {
            setPatternWeightInTable(getPatternString(i), getWeightIndex(i));
        }
    }

    protected abstract String getPatternString(int i);

    protected abstract int getWeightIndex(int i);

    protected void setPatternWeightInTable( String pattern, int wtIndex )  {
        patternTable_.setPatternWeightInTable(pattern, wtIndex);
    }

}
