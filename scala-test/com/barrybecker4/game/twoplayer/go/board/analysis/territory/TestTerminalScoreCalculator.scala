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
    // (4,4)-(4,5) have a single real eye and are dead. The rest of white is alive.
    assertEquals("only the one-eyed white pair is dead", 2, score.deadWhite)
    assertTrue(isDead(board, 4, 4))
    assertTrue(isDead(board, 4, 5))
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

  /** Two separate one-point eyes are enough to live. */
  @Test
  def twoRealEyesAreAlive(): Unit = {
    val board = boardFromDiagram(Array(
      ".........",
      ".OOOOOOO.",
      ".OXXXXXO.",
      ".OX.X.XO.",
      ".OXXXXXO.",
      ".OX.X.XO.",
      ".OXXXXXO.",
      ".OOOOOOO.",
      "........."
    ))
    val score = new TerminalScoreCalculator(board).calculate()

    assertEquals(0, score.deadBlack)
    assertEquals(0, score.deadWhite)
    assertFalse(isDead(board, 4, 3))
    assertFalse(isDead(board, 6, 5))
  }

  /**
   * Finished 9x9 game. White stones the UI left unmarked are dead:
   * the bottom group and both edge stones. Black, including the stone at 4,4
   * and the small string inside the bottom white, is alive once white is removed.
   * Rows are top to bottom. X black, O white.
   */
  @Test
  def finishedGameMarksAllDeadWhite(): Unit = {
    val board = boardFromDiagram(Array(
      "...X.XO.O",
      "OO.XX.XXO",
      "OXX..XX.X",
      ".OOX..XX.",
      "XXXX.X..X",
      "OOOXXX.XO",
      ".OXOOXXO.",
      "OXXOOX.XO",
      "....OX.X."
    ))
    val score = new TerminalScoreCalculator(board).calculate()

    assertEquals("every white stone is dead", 21, score.deadWhite)
    assertEquals("no black stone is dead", 0, score.deadBlack)
    assertEquals(0, score.whiteTerritory)
    assertEquals("empty points plus dead white", 48, score.blackTerritory)
    assertTrue("edge white at 8,1", isDead(board, 8, 1))
    assertTrue("bottom white at 7,4", isDead(board, 7, 4))
    assertTrue("bottom white at 9,5", isDead(board, 9, 5))
    assertTrue("edge white at 8,9", isDead(board, 8, 9))
    assertFalse("black at 4,4 lives", isDead(board, 4, 4))
    assertFalse("black at 6,6 lives", isDead(board, 6, 6))
    assertFalse("inner black at 7,3 lives", isDead(board, 7, 3))
    assertFalse("inner black at 8,2 lives", isDead(board, 8, 2))
  }

  /**
   * 9x9 game. The big white group on the left is alive (three-point eye).
   * Three white stones in the open right side have no eyes and one or two liberties.
   * They are dead, and that side is black territory. The top-left corner stays white.
   */
  @Test
  def looseWhiteStonesInOpenSideAreDead(): Unit = {
    val board = boardFromDiagram(Array(
      ".OOX.....",
      ".OOXOX...",
      ".OOXXX...",
      "OOXX.XO..",
      ".OOXX.X..",
      "OO.OXOX..",
      ".OOOX.X..",
      "OOXXX....",
      "OXXX....."
    ))
    val score = new TerminalScoreCalculator(board).calculate()

    assertEquals("three loose white stones", 3, score.deadWhite)
    assertEquals("black is alive", 0, score.deadBlack)
    assertEquals("left-side eyes", 6, score.whiteTerritory)
    assertEquals("right side plus the one black eye", 34, score.blackTerritory)
    assertTrue(isDead(board, 2, 5))
    assertTrue(isDead(board, 4, 7))
    assertTrue(isDead(board, 6, 6))
    assertFalse("left white group lives", isDead(board, 1, 2))
    assertFalse("black wall lives", isDead(board, 1, 4))
    assertFalse("black group on the right lives", isDead(board, 5, 7))
  }

  /**
   * The four black stones have no eye and one liberty. The white stone at the
   * corner has one real eye (the enemy diagonal does not falsify a corner).
   * Black is removed first; every white stone then has room for two eyes.
   */
  @Test
  def noEyeGroupDiesBeforeOneEyedNeighbor(): Unit = {
    val board = boardFromDiagram(Array(
      ".XX..",
      ".XOOO",
      ".XOXX",
      "XOXXO",
      ".O.O."
    ))
    val score = new TerminalScoreCalculator(board).calculate()

    assertEquals(4, score.deadBlack)
    assertEquals(0, score.deadWhite)
    assertEquals(3, score.blackTerritory)
    assertEquals(6, score.whiteTerritory)
    assertTrue(isDead(board, 3, 4))
    assertFalse("corner white keeps its eye", isDead(board, 4, 5))
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
