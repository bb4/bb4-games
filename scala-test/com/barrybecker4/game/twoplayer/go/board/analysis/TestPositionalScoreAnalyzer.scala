/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.PositionalScore
import com.barrybecker4.game.twoplayer.go.options.GoWeights
import com.barrybecker4.optimization.parameter.ParameterArray

class TestPositionalScoreAnalyzer extends GoTestCase {


    /** we can just reuse one of the other file sets */
    private val PREFIX: String = "board/analysis/scoring/"

    private var goWeights: GoWeights = scala.compiletime.uninitialized

    private var scoreAnalyzer_ : PositionalScoreAnalyzer = scala.compiletime.uninitialized

    private val TOLERANCE: Double = 0.001

    @Before
    override def setUp(): Unit = {
        super.setUp()
        goWeights = new GoWeights()
        var params: ParameterArray = goWeights.getPlayer1Weights
        for (i <- 0 until params.size) {
            params.get(i).setValue(2.0)
        }

        goWeights.setPlayer1Weights(params)
    }

    /**
     *  | XO
     *  | X
     *  |
     *  |     O
     */
    @Test
    def testOccupiedPositionalScoreNoEye(): Unit = {

        initializeBoard("positional_score_no_eye")

        var totalScore: PositionalScore = PositionalScore.createZeroScore()
                                                                            //    badShp  posScore health
        verifyPositionalScore(new ByteLocation(2,2), PositionalScore.createOccupiedScore(0.0, 0.0211, 0.0), totalScore)
        verifyPositionalScore(new ByteLocation(2,3), PositionalScore.createOccupiedScore(0.0, -0.014, 0.0), totalScore)
        verifyPositionalScore(new ByteLocation(3,3), PositionalScore.createOccupiedScore(0.0, 0.0, 0.0), totalScore)
        verifyPositionalScore(new ByteLocation(3,2), PositionalScore.createOccupiedScore(0.0, 0.0141, 0.0), totalScore)

        verifyScoresEqual(PositionalScore.createOccupiedScore(0.0, 0.0211, 0.0), totalScore)
        Assert.assertEquals("Unexpected position score.  ",
                0.0211, totalScore.getPositionScore, TOLERANCE)
    }

    @Test
    def testOverallPositionalScoreNoEye(): Unit = {

        initializeBoard("positional_score_no_eye")
        verifyExpectedOverallScore( 0.021f) //0.025f)
    }

    /**
     *  |   X
     *  |  OX
     *  |XXXX
     *  |
     *  |                  O
     */
    @Test
    def testOccupiedPositionalScoreStoneInEye(): Unit = {

        initializeBoard("positional_score_stone_in_eye")

        var totalScore: PositionalScore = PositionalScore.createZeroScore()
                                                                         //    badShp  posScore health
        verifyPositionalScore(new ByteLocation(2,2), PositionalScore.createOccupiedScore(0.0, 0.0, 0.0), totalScore)
        verifyPositionalScore(new ByteLocation(2,3), PositionalScore.createOccupiedScore(0.0, -0.014f, 0.0), totalScore)
        verifyPositionalScore(new ByteLocation(3,2), PositionalScore.createOccupiedScore(0.0, 0.0141f, 0.0), totalScore)
        verifyPositionalScore(new ByteLocation(3,3), PositionalScore.createOccupiedScore(0.0, 0.211, 0.0), totalScore)

        verifyScoresEqual(PositionalScore.createOccupiedScore(0.0, 0.211, 0.0), totalScore)
        Assert.assertEquals("Unexpected position score.  ",
                0.211, totalScore.getPositionScore, TOLERANCE)
    }

    @Test
    def testOverallPositionalScoreInEye(): Unit = {

        initializeBoard("positional_score_stone_in_eye")
        verifyExpectedOverallScore( -0.05625f) //-0.0666667f)
    }

    /**
     *  | X    OO |
     *  |XXX   O O|
     *  |  X   OO |
     *  |XX
     */
    @Test
    def testOccupiedPositionalAlive(): Unit = {

        initializeBoard("positional_score_alive")

        var totalScore: PositionalScore = PositionalScore.createZeroScore()
                                                                           //       badShp  posScore health
        verifyPositionalScore(new ByteLocation(2,2), PositionalScore.createOccupiedScore(-0.281f, 0.0211f, 0.0f), totalScore)
        verifyPositionalScore(new ByteLocation(2,3), PositionalScore.createOccupiedScore(-0.1875f, .0141f, 0.0f), totalScore)
        verifyPositionalScore(new ByteLocation(3,3), PositionalScore.createOccupiedScore(-0.09375f, 0.211f, 0.0f), totalScore)
        verifyPositionalScore(new ByteLocation(3,2), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore)

        verifyPositionalScore(new ByteLocation(8,2), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore)
        verifyPositionalScore(new ByteLocation(8,3), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore)
        verifyPositionalScore(new ByteLocation(9,3), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore)
        verifyPositionalScore(new ByteLocation(9,2), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore)

        verifyScoresEqual(PositionalScore.createOccupiedScore(-0.5625f, 0.2461f, 0.0f), totalScore)
        Assert.assertEquals("Unexpected position score.  ",
                -0.3164f, totalScore.getPositionScore, TOLERANCE)
    }

    @Test
    def testOverallPositionalScoreAlive(): Unit = {

        initializeBoard("positional_score_alive")
        verifyExpectedOverallScore(-0.2601562491618f) //-0.975f)
    }

    /**
     * @param file saved sgf game file to load
     */
    protected def initializeBoard(file: String): Unit = {
        restore(PREFIX + file)

        var board: GoBoard = getBoard
        scoreAnalyzer_ = new PositionalScoreAnalyzer(board)
    }

    /**
     * Verify candidate move generation.
     */
    private def verifyPositionalScore(loc: Location, expScore: PositionalScore, totalScore: PositionalScore): Unit = {

        var actScore: PositionalScore =
                scoreAnalyzer_.determineScoreForPosition(loc.row, loc.col,
                        goWeights.getPlayer1Weights)
        verifyScoresEqual(expScore, actScore)
        totalScore.incrementBy(actScore)
    }

    private def verifyScoresEqual(expScore: PositionalScore, actScore: PositionalScore): Unit = {
        Assert.assertEquals("Unexpected eye space score. " + actScore,
                expScore.getEyeSpaceScore, actScore.getEyeSpaceScore, TOLERANCE)
        Assert.assertEquals("Unexpected posScore. " + actScore,
                expScore.getPosScore, actScore.getPosScore, TOLERANCE)
        Assert.assertEquals("Unexpected badShape score. " + actScore,
                expScore.getBadShapeScore, actScore.getBadShapeScore, TOLERANCE)
        Assert.assertEquals("Unexpected deadStone score.  " + actScore,
                expScore.getDeadStoneScore, actScore.getDeadStoneScore, TOLERANCE)
        Assert.assertEquals("Unexpected health score.  " + actScore,
                expScore.getHealthScore, actScore.getHealthScore, TOLERANCE)
    }


    private def verifyExpectedOverallScore(expectedScore: Float): Unit = {

        var size: Int = getBoard.getNumRows

        var score: PositionalScore = PositionalScore.createZeroScore()

        for (row <- 1 to size) {
            for (col <- 1 to size) {
               score.incrementBy(scoreAnalyzer_.determineScoreForPosition(row, col, goWeights.getPlayer1Weights))
            }
        }
        Assert.assertEquals(expectedScore, score.getPositionScore, TOLERANCE)
    }

}
