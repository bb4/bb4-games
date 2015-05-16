// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.pattern;

/**
 * Stores the key patterns that can occur in the game.
 * Acts as a fact hash lookup to find an index given a pattern.
 * Do not add duplicate patterns or patterns that are the reverse of other patterns.
 *
 * @author Barry Becker
 */
public class PatternTable {

    /**
     * This table provides a quick way to look up a weight for a pattern.
     * it acts as a hash map to a weight index. The pattern can be converted to
     * a lookup index using convertPatternToInt. There is a leading 1 in front of
     * the binary hash - that's why we need 2^12 rather than 2^11.
     * If the Pattern length can be longer than 11, then we need to make this bigger.
     */
    private static final int TABLE_SIZE = 4096;
    private int weightIndexTable_[];

    /** Converts from patterns to number keys. */
    private PatternToIntConverter converter;

    /**
     * Constructor.
     */
    public PatternTable() {
        initTable();
        converter = new PatternToIntConverter();
    }

    protected void initTable() {
        weightIndexTable_ = new int[TABLE_SIZE];
        for ( int i = 0; i < TABLE_SIZE; i++ ) {
            weightIndexTable_[i] = -1;
        }
    }

    /**
     * @param pattern  pattern to get the weight index for.
     * @param minpos index of first character in pattern
     * @param maxpos index of last character position in pattern.
     * @return weight index
     */
    public int getWeightIndexForPattern(CharSequence pattern, int minpos, int maxpos) {
        return weightIndexTable_[converter.convertPatternToInt(pattern, minpos, maxpos)];
    }

    public void setPatternWeightInTable( String pattern, int wtIndex ) {

        int hash = converter.convertPatternToInt( pattern );
        weightIndexTable_[hash] = wtIndex;

        // also add the reversed pattern
        StringBuilder reverse = new StringBuilder( pattern );
        reverse.reverse();

        hash = converter.convertPatternToInt(reverse.toString());
        weightIndexTable_[hash] = wtIndex;
    }
}
