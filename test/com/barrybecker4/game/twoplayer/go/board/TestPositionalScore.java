// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board;

import com.barrybecker4.math.MathUtil;
import com.barrybecker4.game.twoplayer.go.GoTestCase;

/**
 * Check positional score accumulation.
 * @author Barry Becker
 */
public class TestPositionalScore extends GoTestCase {

    private static final double TOLERANCE = MathUtil.EPS_MEDIUM();

    /** instance under test. */
    private PositionalScore score;


    public void testZeroPositionalScoreConstruction() {
        score = PositionalScore.createZeroScore();

        assertEquals(0.0, score.getDeadStoneScore(), TOLERANCE);
        assertEquals(0.0, score.getEyeSpaceScore(), TOLERANCE);
        assertEquals(0.0, score.getBadShapeScore(), TOLERANCE);
        assertEquals(0.0, score.getPosScore(), TOLERANCE);
        assertEquals(0.0, score.getHealthScore(), TOLERANCE);
        assertEquals(0.0, score.getPositionScore(), TOLERANCE);
    }

    public void testOccupiedPositionalScoreConstruction() {
        score = PositionalScore.createOccupiedScore(0.1, 0.2, 0.3);

        assertEquals(0.0, score.getDeadStoneScore(), TOLERANCE);
        assertEquals(0.0, score.getEyeSpaceScore(), TOLERANCE);
        assertEquals(0.1, score.getBadShapeScore(), TOLERANCE);
        assertEquals(0.2, score.getPosScore(), TOLERANCE);
        assertEquals(0.3, score.getHealthScore(), TOLERANCE);
        assertEquals(0.6, score.getPositionScore(), TOLERANCE);
    }

    public void testEyePointPositionalScoreConstruction() {
        score = PositionalScore.createEyePointScore(0.1, 0.2);

        assertEquals(0.1, score.getDeadStoneScore(), TOLERANCE);
        assertEquals(0.2, score.getEyeSpaceScore(), TOLERANCE);
        assertEquals(0.3, score.getPositionScore(), TOLERANCE);
    }

    public void testPositionalScoreAccumulation() {
        score = PositionalScore.createOccupiedScore(0.1, 0.2, 0.3);
        PositionalScore score2 = PositionalScore.createEyePointScore(0.4, 0.5);

        score.incrementBy(score2);

        assertEquals(0.4, score.getDeadStoneScore(), TOLERANCE);
        assertEquals(0.5, score.getEyeSpaceScore(), TOLERANCE);
        assertEquals(0.1, score.getBadShapeScore(), TOLERANCE);
        assertEquals(0.2, score.getPosScore(), TOLERANCE);
        assertEquals(0.3, score.getHealthScore(), TOLERANCE);
        assertEquals(1.5, score.getPositionScore(), TOLERANCE);
    }


}