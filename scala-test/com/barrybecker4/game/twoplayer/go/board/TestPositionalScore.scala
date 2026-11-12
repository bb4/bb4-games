/* Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import com.barrybecker4.math.MathUtil
import com.barrybecker4.game.twoplayer.go.GoTestCase

class TestPositionalScore extends GoTestCase {


    private val TOLERANCE: Double = MathUtil.EPS_MEDIUM

    /** instance under test. */
    private var score: PositionalScore = scala.compiletime.uninitialized


    @Test
    def testZeroPositionalScoreConstruction(): Unit = {
        score = PositionalScore.createZeroScore()

        Assertions.assertEquals(0.0, score.getDeadStoneScore, TOLERANCE)
        Assertions.assertEquals(0.0, score.getEyeSpaceScore, TOLERANCE)
        Assertions.assertEquals(0.0, score.getBadShapeScore, TOLERANCE)
        Assertions.assertEquals(0.0, score.getPosScore, TOLERANCE)
        Assertions.assertEquals(0.0, score.getHealthScore, TOLERANCE)
        Assertions.assertEquals(0.0, score.getPositionScore, TOLERANCE)
    }

    @Test
    def testOccupiedPositionalScoreConstruction(): Unit = {
        score = PositionalScore.createOccupiedScore(0.1, 0.2, 0.3)

        Assertions.assertEquals(0.0, score.getDeadStoneScore, TOLERANCE)
        Assertions.assertEquals(0.0, score.getEyeSpaceScore, TOLERANCE)
        Assertions.assertEquals(0.1, score.getBadShapeScore, TOLERANCE)
        Assertions.assertEquals(0.2, score.getPosScore, TOLERANCE)
        Assertions.assertEquals(0.3, score.getHealthScore, TOLERANCE)
        Assertions.assertEquals(0.6, score.getPositionScore, TOLERANCE)
    }

    @Test
    def testEyePointPositionalScoreConstruction(): Unit = {
        score = PositionalScore.createEyePointScore(0.1, 0.2)

        Assertions.assertEquals(0.1, score.getDeadStoneScore, TOLERANCE)
        Assertions.assertEquals(0.2, score.getEyeSpaceScore, TOLERANCE)
        Assertions.assertEquals(0.3, score.getPositionScore, TOLERANCE)
    }

    @Test
    def testPositionalScoreAccumulation(): Unit = {
        score = PositionalScore.createOccupiedScore(0.1, 0.2, 0.3)
        var score2: PositionalScore = PositionalScore.createEyePointScore(0.4, 0.5)

        score.incrementBy(score2)

        Assertions.assertEquals(0.4, score.getDeadStoneScore, TOLERANCE)
        Assertions.assertEquals(0.5, score.getEyeSpaceScore, TOLERANCE)
        Assertions.assertEquals(0.1, score.getBadShapeScore, TOLERANCE)
        Assertions.assertEquals(0.2, score.getPosScore, TOLERANCE)
        Assertions.assertEquals(0.3, score.getHealthScore, TOLERANCE)
        Assertions.assertEquals(1.5, score.getPositionScore, TOLERANCE)
    }



}
