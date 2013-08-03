// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common;

import com.barrybecker4.game.twoplayer.pente.pattern.SimpleWeights;
import junit.framework.TestCase;

/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
public class GameWeightsTest extends TestCase  {

    /** instance under test */
    private GameWeights weights;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        weights = new SimpleWeights();
    }

    public void testConstruction() {
        assertEquals("unexpected name ",
                "Weight 1", weights.getName(1));
        assertEquals("unexpected description ",
                "The weighting coefficient for the 1th term of the evaluation polynomial", weights.getDescription(1));
        assertEquals("unexpected param 2 value ",
                10.0, weights.getPlayer1Weights().get(2).getValue());
    }

}