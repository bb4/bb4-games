/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.territory

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import org.junit.Assert.{assertEquals, assertFalse, assertTrue}
import org.junit.Test

/**
 * Exact terminal life-and-death / territory tests (no health-heuristic tolerance).
 */
class TestTerminalScoreCalculator {

  /**
   * Screenshot position: living black group on the right must not be marked dead.
   * Diagram rows top-to-bottom, X=black, O=white, .=empty.
   */
  @Test
  def screenshotLivingBlackNotMarkedDead(): Unit = {
    val board = boardFromDiagram(Array(
      "....OX...",
      "...OOX...",
      "O.O.XX...",
      "OO.OOX...",
      "O.OXXX...",
      "O.OOOOX..",
      "....OXX..",
      ".O.OOOX..",
      "OO..OXX.."
    ))
    val score = new TerminalScoreCalculator(board).calculate()

    assertEquals("no dead black stones", 0, score.deadBlack)
    assertEquals("no dead white stones", 0, score.deadWhite)
    assertFalse("black stone at 1,6 should be alive", isDead(board, 1, 6))
    assertFalse("black stone at 5,4 should be alive (connected to right)", isDead(board, 5, 4))
    assertFalse("black stone at 9,7 should be alive", isDead(board, 9, 7))
    assertFalse("last-move white at 9,1 should be alive", isDead(board, 9, 1))
    assertTrue("black should have some territory on the right", score.blackTerritory > 0)
    assertTrue(
      "white territory must not swallow the whole empty board",
      score.whiteTerritory < 40
    )
  }

  /**
   * Black group with one eye, fully inside white territory, is dead.
   * Surrounding white must keep exterior liberties (larger board).
   */
  @Test
  def deadGroupInsideWhiteTerritory(): Unit = {
    val board = boardFromDiagram(Array(
      ".......",
      ".OOOOO.",
      ".OXXXO.",
      ".OX.XO.",
      ".OXXXO.",
      ".OOOOO.",
      "......."
    ))
    val score = new TerminalScoreCalculator(board).calculate()

    assertEquals("all 8 interior black stones dead", 8, score.deadBlack)
    assertEquals(0, score.deadWhite)
    assertEquals(0, score.blackTerritory)
    // Interior 3x3 (including dead black) is white territory; exterior empties also white
    assertTrue(s"white territory was ${score.whiteTerritory}", score.whiteTerritory >= 9)
    assertTrue(isDead(board, 3, 3))
    assertTrue(isDead(board, 4, 4) || !board.getPosition(4, 4).isOccupied)
  }

  /** Shared empty point between two living groups is dame. */
  @Test
  def damePointScoresZero(): Unit = {
    val board = boardFromDiagram(Array(
      "XX.OO",
      "XX.OO",
      "XX.OO",
      "XX.OO",
      "XX.OO"
    ))
    val score = new TerminalScoreCalculator(board).calculate()

    assertEquals(0, score.deadBlack)
    assertEquals(0, score.deadWhite)
    assertEquals(0, score.blackTerritory)
    assertEquals(0, score.whiteTerritory)
  }

  /**
   * Seki: both sides share dame liberties; neither is removed.
   * Each color also has open space behind so neither is liberty-less.
   */
  @Test
  def simpleSekiBothAlive(): Unit = {
    // Vertical dame wall; both sides have room behind them
    val board = boardFromDiagram(Array(
      "XX.OO",
      "XX.OO",
      "XX.OO",
      "XX.OO",
      "XX.OO"
    ))
    val score = new TerminalScoreCalculator(board).calculate()

    assertEquals(0, score.deadBlack)
    assertEquals(0, score.deadWhite)
    assertFalse(isDead(board, 1, 1))
    assertFalse(isDead(board, 1, 5))
    assertEquals("shared column is dame", 0, score.blackTerritory + score.whiteTerritory)
  }

  /** Dead black stones become white territory points and dead-black counts (not black territory). */
  @Test
  def deadStonesBelongToOpponentCounts(): Unit = {
    val board = boardFromDiagram(Array(
      ".......",
      ".OOOOO.",
      ".OXXXO.",
      ".OX.XO.",
      ".OXXXO.",
      ".OOOOO.",
      "......."
    ))
    val score = new TerminalScoreCalculator(board).calculate()
    assertEquals(8, score.deadBlack)
    assertEquals(0, score.deadWhite)
    assertEquals(0, score.blackTerritory)
    // Points under dead black + the eye are white territory
    assertTrue(s"white territory was ${score.whiteTerritory}", score.whiteTerritory >= 9)
  }

  private def isDead(board: GoBoard, row: Int, col: Int): Boolean = {
    val pos = board.getPosition(row, col).asInstanceOf[GoBoardPosition]
    pos.isOccupied && pos.getPiece.asInstanceOf[GoStone].isDead
  }

  /**
   * Place stones from a diagram and rebuild strings/groups via GoBoard copy.
   * @param lines equal-length rows; X/B black, O/W white, . empty
   */
  private def boardFromDiagram(lines: Array[String]): GoBoard = {
    val size = lines.length
    require(lines.forall(_.length == size), "diagram must be square")
    val seed = new GoBoard(size, 0)
    for (r <- 1 to size; c <- 1 to size) {
      val ch = lines(r - 1).charAt(c - 1)
      val stone = ch match {
        case 'X' | 'B' => new GoStone(true)
        case 'O' | 'W' => new GoStone(false)
        case '.' | ' ' => null
        case other => throw new IllegalArgumentException(s"bad char '$other'")
      }
      if (stone != null) {
        seed.setPosition(new GoBoardPosition(r, c, null, stone))
      }
    }
    new GoBoard(seed)
  }
}
