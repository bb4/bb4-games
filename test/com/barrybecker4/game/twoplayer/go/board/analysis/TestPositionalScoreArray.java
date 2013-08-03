// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.analysis;

import junit.framework.TestCase;

/**
 * Test that we create the expected positional array.
 *
 * @author Barry Becker
 */
public class TestPositionalScoreArray extends TestCase {

    /** instance under test. */
    private PositionalScoreArray scoreArray;


    public void testPositionalArrayOfSize5() {

        scoreArray = PositionalScoreArray.getArray(5);

        float[][] expectedValues = new float[][] {
                {-0.75f, -0.5f, -0.5f, -0.5f, -0.75f},
                {-0.5f, 0.15f, 0.1f, 0.15f, -0.5f},
                {-0.5f, 0.1f, 1.5f, 0.1f, -0.5f},
                {-0.5f, 0.15f, 0.1f, 0.15f, -0.5f},
                {-0.75f, -0.5f, -0.5f, -0.5f, -0.75f}
        };
        checkEqual(expectedValues, 5);
    }

    public void testPositionalArrayOfSize9() {

            scoreArray = PositionalScoreArray.getArray(9);

            float[][] expectedValues = new float[][] {
                    {-0.75f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.75f},
                    {-0.5f, 0.15f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.15f, -0.5f},
                    {-0.5f, 0.1f, 1.5f, 1.0f, 1.0f, 1.0f, 1.5f, 0.1f, -0.5f},
                    {-0.5f, 0.1f, 1.0f, 1.3499999f, 0.9f, 1.3499999f, 1.0f, 0.1f, -0.5f},
                    {-0.5f, 0.1f, 1.0f, 0.9f, 0.0f, 0.9f, 1.0f, 0.1f, -0.5f},
                    {-0.5f, 0.1f, 1.0f, 1.3499999f, 0.9f, 1.3499999f, 1.0f, 0.1f, -0.5f},
                    {-0.5f, 0.1f, 1.5f, 1.0f, 1.0f, 1.0f, 1.5f, 0.1f, -0.5f},
                    {-0.5f, 0.15f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.15f, -0.5f},
                    {-0.75f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.75f}
            };

            checkEqual(expectedValues, 9);
        }

    private void checkEqual(float[][] expectedValues, int size) {
        boolean equal = true;
        for (int i=1; i<=size; i++) {
            for (int j=1; j<=size; j++) {
                if (expectedValues[i-1][j-1] != scoreArray.getValue(i, j)) {
                    equal = false;
                    break;
                }
            }
        }
        assertTrue("The array was not what we expected. Got:" + scoreArray, equal);
    }

}
