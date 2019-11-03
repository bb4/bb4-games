/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis;

/**
 * Verify that we calculate the expected worth for a given board position.
 * @author Barry Becker
 */
public class TestWorthCalculator3 extends WorthCalculatorBase {

    /**
     * If we arrive at the same exact board position from two different paths,
     * we should calculate the same worth value.
     */
    public void testSamePositionFromDifferentPathsEqual() throws Exception {

        compareWorths("worth3x3_A", "worth3x3_B", -151); // -163
    }
}
