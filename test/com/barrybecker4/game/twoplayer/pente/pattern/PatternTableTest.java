// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente.pattern;

import junit.framework.TestCase;

/**
 * @author Barry Becker
 */
public class PatternTableTest extends TestCase  {

    PatternTable patternTable;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testGetWeightIndexForUninitializedTable() {

        patternTable = new PatternTable();
        StringBuilder pattern;
        int wtIndex;

        // not a recognizable pattern.
        pattern = new StringBuilder("XX");
        wtIndex = patternTable.getWeightIndexForPattern(pattern, 0, 0);
        assertEquals(-1, wtIndex);
    }

    public void testGetWeightIndexForXXPattern() {

        patternTable = new PatternTable();
        patternTable.setPatternWeightInTable("XX", 0);

        assertEquals(0, patternTable.getWeightIndexForPattern("XX", 0, 1));
    }

    public void testGetWeightIndexForXXXPattern() {

        patternTable = new PatternTable();
        patternTable.setPatternWeightInTable("XXX", 7);

        assertEquals(7, patternTable.getWeightIndexForPattern("XXX", 0, 2));
        assertEquals(-1, patternTable.getWeightIndexForPattern("XXX", 0, 1));
    }

    /** Check that the reverse was also added. */
    public void testGetWeightIndexFor_XXPattern() {

        patternTable = new PatternTable();
        patternTable.setPatternWeightInTable("_XX", 3);

        assertEquals(3, patternTable.getWeightIndexForPattern("_XX", 0, 2));
        assertEquals(3, patternTable.getWeightIndexForPattern("XX_", 0, 2));
        assertEquals(-1, patternTable.getWeightIndexForPattern("X_X", 0, 2));
    }

    /** Check that the reverse was also added. */
    public void testGetWeightIndexForOOX_XXPattern() {

        patternTable = new PatternTable();
        patternTable.setPatternWeightInTable("OOX_XX", 99);

        assertEquals(99, patternTable.getWeightIndexForPattern("OOX_XX", 0, 5));
        assertEquals(99, patternTable.getWeightIndexForPattern("XX_XOO", 0, 5));
        assertEquals(99, patternTable.getWeightIndexForPattern("XXX_XX", 0, 5));
        assertEquals(-1, patternTable.getWeightIndexForPattern("XOOO_X", 0, 5));
        assertEquals(-1, patternTable.getWeightIndexForPattern("XO_X", 0, 3));
    }
}