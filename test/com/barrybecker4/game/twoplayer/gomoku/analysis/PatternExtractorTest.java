// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Verify that we correctly extract patterns from rows or columns on the board.
 *
 * @author Barry Becker
 */
public class PatternExtractorTest  {

    /** instance under test. */
    private PatternExtractor extractor;
    CharSequence pattern;

    @Test (expected = StringIndexOutOfBoundsException.class)
    public void testEmpty() {

        extractor = new PatternExtractor("");
        extractor.getPattern('O', 0, 0, 0);
    }

    @Test
    public void testGetPatternFromX() {

        extractor = new PatternExtractor("X");

        assertEquals("X", extractor.getPattern('O', 0, 0, 0));
        assertEquals("", extractor.getPattern('X', 0, 0, 0));
    }

    @Test
    public void testGetPatterFromXX() {

        extractor = new PatternExtractor("XX");

        assertEquals("XX", extractor.getPattern('O', 0, 0, 1));
        assertEquals("", extractor.getPattern('X', 0, 0, 1));
    }

    @Test
    public void testGetPatternFromXXX() {

        extractor = new PatternExtractor("XXX");

        assertEquals("XXX", extractor.getPattern('O', 0, 0, 2));
        assertEquals("XXX", extractor.getPattern('O', 1, 0, 2));
        assertEquals("XXX", extractor.getPattern('O', 2, 0, 2));
        assertEquals("", extractor.getPattern('X', 2, 0, 2));
    }

    @Test
    public void testGetPatternFromX_X() {

        extractor = new PatternExtractor("X_X");

        assertEquals("X_X", extractor.getPattern('O', 0, 0, 2));
        assertEquals("X_X", extractor.getPattern('O', 1, 0, 2));
        assertEquals("X_X", extractor.getPattern('O', 2, 0, 2));
        assertEquals("", extractor.getPattern('X', 2, 0, 2));
    }

    @Test
    public void testGetPatternFromX_X_XX_() {

        extractor = new PatternExtractor("X_X_XX_");

        assertEquals("X_X_XX_", extractor.getPattern('O', 0, 0, 6));
        assertEquals("X_X_XX_", extractor.getPattern('O', 1, 0, 6));
        assertEquals("X_X_XX_", extractor.getPattern('O', 2, 0, 6));
        assertEquals("X_X_XX_", extractor.getPattern('O', 5, 0, 6));
        assertEquals("X_X_XX_", extractor.getPattern('O', 6, 0, 6));
        assertEquals("", extractor.getPattern('X', 2, 0, 6));
    }

    @Test
    public void testGetPatternFrom_XO() {

        extractor = new PatternExtractor("_XO");

        assertEquals("_X", extractor.getPattern('O', 0, 0, 2));
        assertEquals("_X", extractor.getPattern('O', 1, 0, 2));
        assertEquals("", extractor.getPattern('O', 2, 0, 2));
        assertEquals("_", extractor.getPattern('X', 0, 0, 2));
        assertEquals("", extractor.getPattern('X', 1, 0, 2));
        assertEquals("O", extractor.getPattern('X', 2, 0, 2));
    }

    @Test
    public void testGetPatternFrom_XO_XX_O_fromXPersp() {

        extractor = new PatternExtractor("_XO_XX_O");

        assertEquals("_X", extractor.getPattern('O', 0, 0, 7));
        assertEquals("_X", extractor.getPattern('O', 1, 0, 7));
        assertEquals("", extractor.getPattern('O', 2, 0, 7));
        assertEquals("_XX_", extractor.getPattern('O', 3, 0, 7));
        assertEquals("_XX_", extractor.getPattern('O', 4, 0, 7));
        assertEquals("_XX_", extractor.getPattern('O', 5, 0, 7));
        assertEquals("_XX_", extractor.getPattern('O', 6, 0, 7));
        assertEquals("", extractor.getPattern('O', 7, 0, 7));
    }

    @Test
    public void testGetPatternFrom_XO_XX_O_fromOPersp() {

        extractor = new PatternExtractor("_XO_XX_O");

        assertEquals("_", extractor.getPattern('X', 0, 0, 7));
        assertEquals("", extractor.getPattern('X', 1, 0, 7));
        assertEquals("O_", extractor.getPattern('X', 2, 0, 7));
        assertEquals("O_", extractor.getPattern('X', 3, 0, 7));
        assertEquals("", extractor.getPattern('X', 4, 0, 7));
        assertEquals("", extractor.getPattern('X', 5, 0, 7));
        assertEquals("_O", extractor.getPattern('X', 6, 0, 7));
        assertEquals("_O", extractor.getPattern('X', 7, 0, 7));
    }

    @Test
    public void testGetPatternFromX__O_XXO__O_O_DoubleBlanksO() {

        extractor = new PatternExtractor("X__O_XXO__O_O__");

        assertEquals("", extractor.getPattern('X', 0, 0, 14));
        assertEquals("_", extractor.getPattern('X', 1, 0, 14));
        assertEquals("_O_", extractor.getPattern('X', 3, 0, 14));
        assertEquals("_O_", extractor.getPattern('X', 4, 0, 14));
        assertEquals("", extractor.getPattern('X', 6, 0, 14));
        assertEquals("O_", extractor.getPattern('X', 7, 0, 14));
        assertEquals("_O_O_", extractor.getPattern('X', 9, 0, 14));
        assertEquals("_O_O_", extractor.getPattern('X', 11, 0, 14));
        assertEquals("_O_O_", extractor.getPattern('X', 12, 0, 14));
        assertEquals("_O_O_", extractor.getPattern('X', 13, 0, 14));
        assertEquals("_", extractor.getPattern('X', 14, 0, 14));
    }

    @Test
    public void testGetPatternFromX__O_XXO__O_O_DoubleBlanksX() {

        extractor = new PatternExtractor("X__O_XXO__O_O__");

        assertEquals("X_", extractor.getPattern('O', 0, 0, 14));
        assertEquals("_", extractor.getPattern('O', 2, 0, 14));
        assertEquals("", extractor.getPattern('O', 3, 0, 14));
        assertEquals("_XX", extractor.getPattern('O', 4, 0, 14));
        assertEquals("_XX", extractor.getPattern('O', 6, 0, 14));
        assertEquals("_", extractor.getPattern('O', 8, 0, 14));
        assertEquals("", extractor.getPattern('O', 10, 0, 14));
        assertEquals("_", extractor.getPattern('O', 11, 0, 14));
        assertEquals("_", extractor.getPattern('O', 13, 0, 14));
        assertEquals("_", extractor.getPattern('O', 14, 0, 14));
    }
}