package com.barrybecker4.game.twoplayer.go

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.go.board.analysis.CandidateMoveAnalyzer
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import org.junit.Assert.{assertFalse, assertTrue, fail}
import org.junit.Test

/**
 * Regression: White's second move must not prefer a first-line edge play in the opening.
 */
class TestOpeningEdgeMove extends GoTestCase:

  override protected def getBoardSize: Int = 9

  private def play(r: Int, c: Int, black: Boolean): Unit =
    controller.makeMove(new GoMove(new ByteLocation(r, c), 0, new GoStone(black)))

  /** B E7, W C7, B F4 — the screenshot position where White previously played B9. */
  private def setupScreenshotOpening(): Unit =
    play(3, 5, true)
    play(3, 3, false)
    play(6, 6, true)

  @Test
  def firstLineNotCandidateAfterInteriorThreeThree(): Unit =
    setupScreenshotOpening()
    val cma = new CandidateMoveAnalyzer(getBoard)
    assertFalse("B9 must not be a candidate after an interior 3-3", cma.isCandidateMove(1, 2))
    assertFalse("A9 must not be a candidate after an interior 3-3", cma.isCandidateMove(1, 1))
    assertTrue("Nearby 3rd/4th line moves remain candidates", cma.isCandidateMove(2, 5) || cma.isCandidateMove(4, 4))

  @Test
  def computerDoesNotPlayFirstLineOnSecondMove(): Unit =
    setupScreenshotOpening()
    controller.requestComputerMove(false, true)
    val move = controller.getLastMove.asInstanceOf[GoMove]
    val onFirstLine =
      move.getToRow == 1 || move.getToCol == 1 || move.getToRow == 9 || move.getToCol == 9
    if onFirstLine then
      fail(s"White played first-line opening blunder at (${move.getToRow},${move.getToCol})")
