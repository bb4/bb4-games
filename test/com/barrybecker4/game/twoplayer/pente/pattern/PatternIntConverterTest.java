// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente.pattern;

import junit.framework.TestCase;

/**
 * @author Barry Becker
 */
public class PatternIntConverterTest extends TestCase  {

    /** instance under test. */
    PatternToIntConverter converter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        converter = new PatternToIntConverter();
    }

    /** converts to 1 in binary because we always have a leading 1 .*/
    public void testGetIntForEmptyPattern() {
        assertEquals(1, converter.convertPatternToInt(""));
    }

    /** "_" converts to 10 in binary (i.e. 2 in decimal) */
    public void testGetIntForUnoccupied() {

        verify(2, Character.toString(Patterns.UNOCCUPIED));
    }

    /** Converts to 11 in binary */
    public void testGetIntForX() {
        verify(3, "X");
    }

    public void testGetIntForO() {
        verify(3, "O");
    }

    /** The character in the pattern does not matter. we just check that it is not the UNOCCUPIED char. */
    public void testGetIntForW() {
        verify(3, "W");
    }

    /** Converts to 100 in binary */
    public void testGetIntFor__() {
        verify(4, "__");
    }

    /** Converts to 101 in binary */
    public void testGetIntFor_X() {
        verify(5, "_X");
    }

    /** Converts to 110 in binary */
    public void testGetIntForX_() {
        verify(6, "X_");
    }

    /** Converts to 1000 in binary */
    public void testGetIntFor___() {
        verify(8, "___");
    }

    /** Converts to 1100 in binary */
    public void testGetIntForX__() {
        verify(12, "X__");
    }

    /** Converts to 1010 in binary */
    public void testGetIntFor_X_() {
        verify(10, "_X_");
    }

    /** Converts to 1001 in binary */
    public void testGetIntFor__X() {
        verify(9, "__X");
    }

    /** Converts to 1110 in binary */
    public void testGetIntForXX_() {
        verify(14, "XX_");
    }

    /** Converts to 1011 in binary */
    public void testGetIntFor_XX() {
        verify(11, "_XX");
    }

    /** Converts to 1111 in binary */
    public void testGetIntForXXX() {
        verify(15, "XXX");
    }

    /** The character in the patter does not matter. we just check that it is not the UNOCCUPIED char. */
    public void testGetIntForNonXCharsInPatter() {
        verify(15, "Y X");
    }

    /** Converts to 110101 in binary */
    public void testGetIntForX_X_X() {
        verify(53, "X_X_X");
    }

    /** Converts to 110101 in binary */
    public void testGetIntForO_O_O() {
        verify(53, "O_O_O");
    }

    /** Converts to 110101 in binary */
    public void testGettIntForO_X_O() {
        verify(53, "O_X_O");
    }

    /** Converts to 110101 in binary given the range */
    public void testGetIntForOOX_X_XO() {
        String pattern = "OOX_X_XO";
        assertEquals("Unexpected integer for pattern.",
                        53, converter.convertPatternToInt(pattern, 2, 6));
    }

    private void verify(int expectedInt, String pattern) {
        assertEquals("Unexpected integer for pattern.",
                expectedInt, converter.convertPatternToInt(pattern));
    }
}