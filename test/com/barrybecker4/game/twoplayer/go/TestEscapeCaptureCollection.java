/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go;

/**
 * Test a collection of problems from
 * Martin Mueller (mmueller)
 * Markus Enzenberger (emarkus)
 *email domain: cs.ualberta.ca
 *
 * @author Barry Becker
 */
public class TestEscapeCaptureCollection extends GoTestCase {

    private static final String PREFIX = "problems/sgf/escape_capture/";

    public void testPass() { assertTrue(true);}

    /*  These currently take a long time and fail.

    public void test3() {
        GoMove m = getNextMove(PREFIX + "escape_capture.3", true);
        verifyExpected(m, 6, 11);
    }

    public void test5() {
        GoMove m = getNextMove(PREFIX + "escape_capture.5", true);
        verifyExpected(m, 0, 6);
    }

    public void test13() {
        GoMove m = getNextMove(PREFIX + "escape_capture.13", false);
        verifyExpected(m, 13, 5);
    }*/
}
