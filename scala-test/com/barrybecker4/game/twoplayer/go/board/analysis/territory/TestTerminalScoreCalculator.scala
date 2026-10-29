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
   * The lower-left white group has four liberties in a nine-point pocket and no eye.
   * The living black wall owns that fight. The white stones are dead.
   */
  @Test
  def lowerLeftPocketIsDead(): Unit = {
    val board = boardFromDiagram(Array(
      ".OOX.....",
      "OOXXXXXXO",
      ".OOOOX..X",
      "OOOOX..X.",
      "XXXXXXXO.",
      "..OOX..X.",
      "..OXXX.X.",
      "X.OX..XX.",
      "....X..X."
    ))
    val score = new TerminalScoreCalculator(board).calculate()
    assertTrue("white at 6,3 is dead", isDead(board, 6, 3))
    assertTrue("white at 6,4 is dead", isDead(board, 6, 4))
    assertTrue("white at 7,3 is dead", isDead(board, 7, 3))
    assertTrue("white at 8,3 is dead", isDead(board, 8, 3))
    assertFalse("the two-eyed white group lives", isDead(board, 1, 2))
    assertFalse("black wall lives", isDead(board, 5, 1))
    assertTrue("edge white at 2,9 stays dead", isDead(board, 2, 9))
    assertEquals(0, score.deadBlack)
  }

  /**
   * The left black stones are alive once the corner white is removed.
   * The corner white is dead even if those black stones are given to it.
   * Rows are top to bottom. X black, O white.
   */
  @Test
  def cornerWhiteIsDeadAndLeftBlackLives(): Unit = {
    val board = boardFromDiagram(Array(
      "..O.X.X.X",
      "OO.XXX.X.",
      "X.OOOX..X",
      "XOO.OXX..",
      ".XXOXX.XX",
      "X.XXX.X.X",
      ".OOX.XXX.",
      "OOX.XOOXO",
      "...X..O.."
    ))
    val score = new TerminalScoreCalculator(board).calculate()
    assertFalse("black at 3,1 is alive", isDead(board, 3, 1))
    assertFalse("black at 4,1 is alive", isDead(board, 4, 1))
    assertFalse("black at 6,1 is alive", isDead(board, 6, 1))
    assertTrue("corner white at 1,3 is dead", isDead(board, 1, 3))
    assertTrue("corner white at 2,1 is dead", isDead(board, 2, 1))
    assertTrue("corner white at 2,2 is dead", isDead(board, 2, 2))
    assertTrue("the enclosed white at 5,4 stays dead", isDead(board, 5, 4))
    assertFalse("the big black group stays alive", isDead(board, 1, 5))
    assertTrue("the corner is still a fight until the white is removed", new TerminalScoreCalculator(new GoBoard(board)).hasUnsettledGroup)
    assertEquals(0, score.deadBlack)
  }

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
   * A three-point region is one eye, not two. The black stones that only have that
   * region are dead. White lives and takes the points.
   */
  @Test
  def threePointRegionIsNotTwoEyes(): Unit = {
    val board = boardFromDiagram(Array(
      ".XX..",
      ".XOOO",
      ".XOXX",
      "XOXXO",
      ".O.O."
    ))
    val score = new TerminalScoreCalculator(board).calculate()

    assertEquals(9, score.deadBlack)
    assertEquals(0, score.deadWhite)
    assertEquals(0, score.blackTerritory)
    assertEquals(17, score.whiteTerritory)
  }

  /**
   * Finished 9x9. The top-left corner is a six-point white eye.
   * The white stone at the top-right has three liberties in a six-point pocket
   * and no eye, so it is dead and that corner is black territory.
   * White stones with three liberties in the large bottom area can still live,
   * and that area stays dame.
   * Rows are top to bottom. X black, O white.
   */
  @Test
  def smallPocketStoneIsDead(): Unit = {
    val board = boardFromDiagram(Array(
      "...OOX...",
      "...OXXX..",
      "OOOOOXX.O",
      "OXOOOOXX.",
      "XXXOXOX.X",
      ".X.XXX..X",
      "..X.OOXX.",
      "O.X.OXX.X",
      "........."
    ))
    val score = new TerminalScoreCalculator(board).calculate()

    assertEquals("top-left corner stays white", 6, score.whiteTerritory)
    assertEquals("black eyes plus the top-right pocket", 13, score.blackTerritory)
    assertEquals("only the top-right white stone is dead", 1, score.deadWhite)
    assertEquals("black groups have eyes", 0, score.deadBlack)
    assertFalse("white wall around the corner lives", isDead(board, 1, 4))
    assertTrue("corner white at 3,9 is dead", isDead(board, 3, 9))
    assertFalse("bottom white still has room", isDead(board, 8, 1))
    assertFalse(isDead(board, 7, 5))
    assertTrue("room to play is not settled life", new TerminalScoreCalculator(new GoBoard(board)).hasUnsettledGroup)
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

  /** A 2x2 eye is two eyes. The surrounding group lives. */
  @Test
  def squareFourLives(): Unit = {
    val board = boardFromDiagram(Array(
      "XXXXXX",
      "XX..XX",
      "XX..XX",
      "XXXXXX",
      "XXXXXX",
      "XXXXXX"
    ))
    val score = new TerminalScoreCalculator(board).calculate()
    assertEquals(0, score.deadBlack)
    assertEquals(0, score.deadWhite)
    assertEquals(4, score.blackTerritory)
  }

  /** Corner bent four is not two eyes, so the group that owns only that shape is dead. */
  @Test
  def cornerBentFourIsDead(): Unit = {
    val board = boardFromDiagram(Array(
      "...X",
      ".XXX",
      "XXXX",
      "XXXX"
    ))
    val score = new TerminalScoreCalculator(board).calculate()
    assertEquals(12, score.deadBlack)
    assertEquals(0, score.deadWhite)
    assertEquals(0, score.blackTerritory)
  }

  /**
   * Both colors share exactly the same two liberties. Neither side has two eyes.
   * Both live, and the shared points are not territory.
   */
  @Test
  def sharedLibertySekiBothLive(): Unit = {
    val board = boardFromDiagram(Array(
      "XXXX",
      "X.OX",
      "XO.X",
      "XXXX"
    ))
    val score = new TerminalScoreCalculator(board).calculate()
    assertEquals(0, score.deadBlack)
    assertEquals(0, score.deadWhite)
    assertEquals(0, score.blackTerritory)
    assertEquals(0, score.whiteTerritory)
    assertFalse(isDead(board, 2, 3))
    assertFalse(isDead(board, 3, 2))
    assertFalse(isDead(board, 1, 1))
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
