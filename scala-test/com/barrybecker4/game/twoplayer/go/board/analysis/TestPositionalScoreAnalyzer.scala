/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.PositionalScore
import com.barrybecker4.game.twoplayer.go.options.GoWeights
import com.barrybecker4.optimization.parameter.ParameterArray

class TestPositionalScoreAnalyzer extends GoTestCase:

  /** we can just reuse one of the other file sets */
  private val PREFIX: String = "board/analysis/scoring/"

  private var goWeights: GoWeights = scala.compiletime.uninitialized

  private var scoreAnalyzer_ : PositionalScoreAnalyzer = scala.compiletime.uninitialized

  private val TOLERANCE: Double = 0.001

  @Before
  override def setUp(): Unit =
    super.setUp()
    goWeights = new GoWeights()
    val params: ParameterArray = goWeights.getPlayer1Weights
    for (i <- 0 until params.size) do
      params.get(i).setValue(2.0)
    goWeights.setPlayer1Weights(params)

  /**
   *  | XO
   *  | X
   *  |
   *  |     O
   * Second-line stones contribute 0 positional score with current LINE_VALS.
   */
  @Test
  def testOccupiedPositionalScoreNoEye(): Unit =
    initializeBoard("positional_score_no_eye")

    val totalScore: PositionalScore = PositionalScore.createZeroScore()
    verifyPositionalScore(new ByteLocation(2, 2), PositionalScore.createOccupiedScore(0.0, 0.0, 0.0), totalScore)
    verifyPositionalScore(new ByteLocation(2, 3), PositionalScore.createOccupiedScore(0.0, 0.0, 0.0), totalScore)
    verifyPositionalScore(new ByteLocation(3, 3), PositionalScore.createOccupiedScore(0.0, 0.0, 0.0), totalScore)
    verifyPositionalScore(new ByteLocation(3, 2), PositionalScore.createOccupiedScore(0.0, 0.0, 0.0), totalScore)

    verifyScoresEqual(PositionalScore.createOccupiedScore(0.0, 0.0, 0.0), totalScore)
    Assert.assertEquals("Unexpected position score.  ", 0.0, totalScore.getPositionScore, TOLERANCE)

  @Test
  def testOverallPositionalScoreNoEye(): Unit =
    initializeBoard("positional_score_no_eye")
    verifyExpectedOverallScore(0.0f)

  /**
   *  |   X
   *  |  OX
   *  |XXXX
   *  |
   *  |                  O
   */
  @Test
  def testOccupiedPositionalScoreStoneInEye(): Unit =
    initializeBoard("positional_score_stone_in_eye")

    val totalScore: PositionalScore = PositionalScore.createZeroScore()
    verifyPositionalScore(new ByteLocation(2, 2), PositionalScore.createOccupiedScore(0.0, 0.0, 0.0), totalScore)
    verifyPositionalScore(new ByteLocation(2, 3), PositionalScore.createOccupiedScore(0.0, 0.0, 0.0), totalScore)
    verifyPositionalScore(new ByteLocation(3, 2), PositionalScore.createOccupiedScore(0.0, 0.0, 0.0), totalScore)
    verifyPositionalScore(new ByteLocation(3, 3), PositionalScore.createOccupiedScore(0.0, 0.625, 0.0), totalScore)

    verifyScoresEqual(PositionalScore.createOccupiedScore(0.0, 0.625, 0.0), totalScore)
    Assert.assertEquals("Unexpected position score.  ", 0.625, totalScore.getPositionScore, TOLERANCE)

  @Test
  def testOverallPositionalScoreInEye(): Unit =
    initializeBoard("positional_score_stone_in_eye")
    verifyExpectedOverallScore(-1.875f)

  /**
   *  | X    OO |
   *  |XXX   O O|
   *  |  X   OO |
   *  |XX
   */
  @Test
  def testOccupiedPositionalAlive(): Unit =
    initializeBoard("positional_score_alive")

    val totalScore: PositionalScore = PositionalScore.createZeroScore()
    verifyPositionalScore(new ByteLocation(2, 2), PositionalScore.createOccupiedScore(-0.5f, 0.0f, 0.0f), totalScore)
    verifyPositionalScore(new ByteLocation(2, 3), PositionalScore.createOccupiedScore(-1.0f / 3.0f, 0.0f, 0.0f), totalScore)
    verifyPositionalScore(new ByteLocation(3, 3), PositionalScore.createOccupiedScore(-1.0f / 6.0f, 0.625f, 0.0f), totalScore)
    verifyPositionalScore(new ByteLocation(3, 2), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore)

    verifyPositionalScore(new ByteLocation(8, 2), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore)
    verifyPositionalScore(new ByteLocation(8, 3), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore)
    verifyPositionalScore(new ByteLocation(9, 3), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore)
    verifyPositionalScore(new ByteLocation(9, 2), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore)

    verifyScoresEqual(PositionalScore.createOccupiedScore(-1.0f, 0.625f, 0.0f), totalScore)
    Assert.assertEquals("Unexpected position score.  ", -0.375f, totalScore.getPositionScore, TOLERANCE)

  @Test
  def testOverallPositionalScoreAlive(): Unit =
    initializeBoard("positional_score_alive")
    verifyExpectedOverallScore(-0.5f)

  protected def initializeBoard(file: String): Unit =
    restore(PREFIX + file)
    scoreAnalyzer_ = new PositionalScoreAnalyzer(getBoard)

  private def verifyPositionalScore(loc: Location, expScore: PositionalScore, totalScore: PositionalScore): Unit =
    val actScore: PositionalScore =
      scoreAnalyzer_.determineScoreForPosition(loc.row, loc.col, goWeights.getPlayer1Weights)
    verifyScoresEqual(expScore, actScore)
    totalScore.incrementBy(actScore)

  private def verifyScoresEqual(expScore: PositionalScore, actScore: PositionalScore): Unit =
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

  private def verifyExpectedOverallScore(expectedScore: Float): Unit =
    val size: Int = getBoard.getNumRows
    val score: PositionalScore = PositionalScore.createZeroScore()
    for (row <- 1 to size; col <- 1 to size) do
      score.incrementBy(scoreAnalyzer_.determineScoreForPosition(row, col, goWeights.getPlayer1Weights))
    Assert.assertEquals(expectedScore, score.getPositionScore, TOLERANCE)
