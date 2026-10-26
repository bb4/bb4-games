/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.GoSearchable
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test

/**
 * @author Barry Becker
 */
class TestScoring extends GoTestCase:

  private val PATH_PREFIX = "scoring/"

  /** give some leeway on the territory estimate since its a heuristic. */
  private val TOLERANCE = 5.0

  @Test
  def testScoring1(): Unit =
    checkScoring("problem_score1", 0, 0, 0, 0, 74, 57, -7)

  @Test
  def testScoring55a(): Unit =
    //                                           bt, wt, finalWorth
    checkScoring("problem_score55a", 0, 0, 0, 0, 4, 1, 400)

  @Test
  def testScoring55b(): Unit =
    //                                           bt, wt,  finalWorth
    checkScoring("problem_score55b", 0, 2, 0, 5, 13, 0, 1604)

  @Test
  def testScoring2(): Unit =
    //                                          bt, wt,  finalWorth
    checkScoring("problem_score2", 0, 0, 0, 0, 58, 0, -138)

  @Test
  def testScoringIdentPosition1a(): Unit =
    //                                               bt, wt,  finalWorth
    checkScoring("problem_identPosition1a", 0, 0, 0, 1, 3, 0, -50)

  @Test
  def testScoringIdentPosition1b(): Unit =
    checkScoring("problem_identPosition1b", 0, 0, 0, 1, 3, 0, -50)

  @Test
  def testScoringIdentPosition2a(): Unit =
    checkScoring("problem_identPosition2a", 0, 0, 1, 1, 0, 0, 23)

  @Test
  def testScoringIdentPosition2b(): Unit =
    checkScoring("problem_identPosition2b", 0, 1, 1, 1, 0, 0, 601)

  /**
   * @param scoringProblem determines problem file to load
   * @param expectedBlackCapturesSoFar number of black stones that were captured by white.
   * @param expectedWhiteCapturesSoFar number of white stones that were captured by black.
   * @param expectedDeadBlackOnBoard number of black stones that that are dead on the board at the end.
   * @param expectedDeadWhiteOnBoard number of white stones that that are dead on the board at the end.
   * @param expectedBlackTerr amount of black territory (excluding dead white stones in the territory)
   * @param expectedWhiteTerr amount of white territory (excluding dead black stones in the territory)
   */
  private def checkScoring(
    scoringProblem: String,
    expectedBlackCapturesSoFar: Int, expectedWhiteCapturesSoFar: Int,
    expectedDeadBlackOnBoard: Int, expectedDeadWhiteOnBoard: Int,
    expectedBlackTerr: Int, expectedWhiteTerr: Int, expectedFinalWorth: Int
  ): Unit =
    updateLifeAndDeath(PATH_PREFIX + scoringProblem)
    val blackTerrEst = controller.getFinalTerritory(true)
    val whiteTerrEst = controller.getFinalTerritory(false)
    val searchable = controller.getSearchable().asInstanceOf[GoSearchable]
    val numBlackCaptures = searchable.getNumCaptures(true)
    val numWhiteCaptures = searchable.getNumCaptures(false)
    val numDeadBlack = searchable.getNumDeadStonesOnBoard(true)
    val numDeadWhite = searchable.getNumDeadStonesOnBoard(false)
    GameContext.log(0, "CaptureCounts :          black = " + numBlackCaptures + "   white = " + numWhiteCaptures)
    GameContext.log(0, "Territory: black = " + blackTerrEst + "   white = " + whiteTerrEst)
    assertEquals("Unexpected number of black captures. Expected ", expectedBlackCapturesSoFar, numBlackCaptures)
    assertEquals("Unexpected number of white captures. Expected ", expectedWhiteCapturesSoFar, numWhiteCaptures)
    assertEquals("Unexpected number of dead black stones on board", expectedDeadBlackOnBoard, numDeadBlack)
    assertEquals("Unexpected number of dead white stones on board.", expectedDeadWhiteOnBoard, numDeadWhite)
    assertTrue(
      "The black territory estimate (" + blackTerrEst + ") was not close to " + expectedBlackTerr,
      withinBounds(blackTerrEst, expectedBlackTerr)
    )
    assertTrue(
      "The white territory estimate (" + whiteTerrEst + ") was not close to " + expectedWhiteTerr,
      withinBounds(whiteTerrEst, expectedWhiteTerr)
    )
    val finalWorth = searchable.worth(
      searchable.getMoveList.getLastMove.asInstanceOf[GoMove],
      controller.getComputerWeights().getDefaultWeights
    )
    assertEquals("Unexpected worth for final move.", expectedFinalWorth, finalWorth)

  protected def updateLifeAndDeath(problemFile: String): Unit =
    GameContext.log(0, "finding score for " + problemFile + " ...")
    restore(problemFile)
    // force dead stones to be updated by calling done with resignation move.
    controller.getSearchable().done(GoMove.createResignationMove(true), true)

  private def withinBounds(actual: Int, expected: Int): Boolean =
    actual > (expected - TOLERANCE) && actual < (expected + TOLERANCE)
