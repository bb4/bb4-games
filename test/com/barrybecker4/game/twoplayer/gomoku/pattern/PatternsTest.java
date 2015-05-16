/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.pattern;

import junit.framework.TestCase;

/**
 * @author Barry Becker
 */
public class PatternsTest extends TestCase  {

    Patterns patterns;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /** Verify that we get -1 for patterns that are not in the set. */
    public void testGetWeightIndexForNonExistantSimplePatterns() {

        patterns = new SimplePatterns();

        // not a recognizable pattern. -1 means not found.
        verify(-1, "_");
        verify(-1, "X");
        verify(-1, "X__X");
        verify(-1, "__X");
        verify(-1, "X__");
        verify(-1, "X");
    }

    /** Verify that we get -1 for patterns that are not in the set. */
    public void testGetWeightIndexForNonExistantEmbeddedPattern() {

        patterns = new SimplePatterns();

        int wtIndex = patterns.getWeightIndexForPattern("OX__O", 1, 3);
        assertEquals(-1, wtIndex);
    }

    /** Verify that we get the correct index for patterns that are in the set. */
    public void testGetWeightIndexForSimplePatterns() {

        patterns = new SimplePatterns();

        verify(0, "_X");
        verify(0, "X_");
        verify(1, "_X_");
        verify(2, "XX");
        verify(2, "_XX_");
    }

    /** Verify that we get the correct index for patterns that are in the set. */
    public void testGetWeightIndexForEmbeddedPattern() {

        patterns = new SimplePatterns();

        int wtIndex = patterns.getWeightIndexForPattern("OO_X_O", 2, 4);
        assertEquals(1, wtIndex);
    }

    public void testGetWeightIndexForGoMokuPatternX_XX_XXX() {

        patterns = new GoMokuPatterns();
        String pattern = "X_XX_XXX";

        assertEquals(-1, patterns.getWeightIndexForPattern(pattern, 0, 1));
        assertEquals(0, patterns.getWeightIndexForPattern(pattern, 1, 3));
        assertEquals(0, patterns.getWeightIndexForPattern(pattern, 2, 4));
        assertEquals(2, patterns.getWeightIndexForPattern(pattern, 0, 3));
        assertEquals(4, patterns.getWeightIndexForPattern(pattern, 0, 4));
        assertEquals(4, patterns.getWeightIndexForPattern(pattern, 0, 5));
        assertEquals(6, patterns.getWeightIndexForPattern(pattern, 0, 6));
        assertEquals(6, patterns.getWeightIndexForPattern(pattern, 0, 7));
    }

    public void testGetWeightIndexForGoMokuPatternXX_XX() {

        patterns = new GoMokuPatterns();
        String pattern = "XX_XX";

        assertEquals(-1, patterns.getWeightIndexForPattern(pattern, 0, 0));
        assertEquals(-1, patterns.getWeightIndexForPattern(pattern, 0, 1));
        assertEquals(0, patterns.getWeightIndexForPattern(pattern, 0, 2));
        assertEquals(2, patterns.getWeightIndexForPattern(pattern, 0, 3));
        assertEquals(6, patterns.getWeightIndexForPattern(pattern, 0, 4));
        assertEquals(6, patterns.getWeightIndexForPattern(pattern));
    }


    private void verify(int expIndex, String pattern) {
        int wtIndex = patterns.getWeightIndexForPattern(pattern, 0, pattern.length()-1);
        assertEquals(expIndex, wtIndex);
    }
}