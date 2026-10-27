/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.territory

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.WorthCalculator
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.go.options.{GoOptions, GoWeights}
import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test

/**
 * Search worth is a Japanese point estimate: territory of living groups,
 * prisoners, estimated dead stones, and komi. One point beats any shape bonus.
 */
class TestSearchScoreCalculator {

  private val Komi = GoOptions.DEFAULT_KOMI.toDouble

  @Test
  def emptyBoardIsMinusKomi(): Unit = {
    val board = new GoBoard(9, 0)
    assertEquals(-Komi, new SearchScoreCalculator(board).points(), 0.001)
    assertEquals(-55, worth(board))
  }

  @Test
  def whitePrisonerChangesScoreByOnePoint(): Unit = {
    val captured = new GoBoard(5, 0)
    play(captured, 2, 1, black = true)
    play(captured, 1, 1, black = false)
    play(captured, 1, 2, black = true)
    setHealth(captured, 0.5f)

    val sameStones = new GoBoard(5, 0)
    play(sameStones, 2, 1, black = true)
    play(sameStones, 1, 2, black = true)
    setHealth(sameStones, 0.5f)

    val difference = new SearchScoreCalculator(captured).points() - new SearchScoreCalculator(sameStones).points()
    assertEquals(1.0, difference, 0.001)
  }

  @Test
  def surroundedEmptyPointCountsAsOne(): Unit = {
    val board = livingBoard(Array(
      "XXX",
      "X.X",
      "XXX"
    ))
    markAlive(board)
    assertEquals(1.0 - Komi, new SearchScoreCalculator(board).points(), 0.001)
  }

  @Test
  def highHealthWithoutBensonDoesNotClaimTheEye(): Unit = {
    val board = livingBoard(Array(
      "XXX",
      "X.X",
      "XXX"
    ))
    assertEquals(-Komi, new SearchScoreCalculator(board).points(), 0.001)
  }

  @Test
  def fillingDameDoesNotChangeTheScore(): Unit = {
    val dame = livingBoard(Array(
      "XXX",
      "...",
      "OOO"
    ))
    val filled = boardFromDiagram(Array(
      "XXX",
      ".X.",
      "OOO"
    ))
    setHealth(filled, black = 1.0f, white = -1.0f)
    setHealthAt(filled, 2, 2, 0.5f)

    assertEquals(-Komi, new SearchScoreCalculator(dame).points(), 0.001)
    assertEquals(new SearchScoreCalculator(dame).points(), new SearchScoreCalculator(filled).points(), 0.001)
  }

  @Test
  def unsettledStonesAreNotTerritoryOrPrisoners(): Unit = {
    val board = boardFromDiagram(Array(
      "XXX",
      "X.X",
      "XXX"
    ))
    setHealth(board, 0.5f)
    assertEquals(-Komi, new SearchScoreCalculator(board).points(), 0.001)
  }

  @Test
  def ordinaryStoneIsNotAPrisoner(): Unit = {
    val board = new GoBoard(3, 0)
    play(board, 2, 2, black = true)
    setHealthAt(board, 2, 2, 0.19f)
    assertEquals(-Komi, new SearchScoreCalculator(board).points(), 0.001)
  }

  @Test
  def clearlyLosingStoneIsDead(): Unit = {
    val board = new GoBoard(3, 0)
    play(board, 2, 2, black = true)
    setHealthAt(board, 2, 2, -0.5f)
    assertEquals(-1.0 - Komi, new SearchScoreCalculator(board).points(), 0.001)
  }

  @Test
  def bensonAliveCountsEvenWhenHealthIsUnsettled(): Unit = {
    val board = boardFromDiagram(Array(
      "XXX",
      "X.X",
      "XXX"
    ))
    setHealth(board, 0.5f)
    markAlive(board)
    assertEquals(1.0 - Komi, new SearchScoreCalculator(board).points(), 0.001)
  }

  @Test
  def deadWhiteStoneIsAPointAndItsVacatedPointIsTerritory(): Unit = {
    val board = livingBoard(Array(
      "XXX",
      "XOX",
      "XXX"
    ))
    setHealthAt(board, 2, 2, 0.8f)
    markAlive(board, blackOnly = true)
    assertEquals(2.0 - Komi, new SearchScoreCalculator(board).points(), 0.001)
  }

  @Test
  def openWallDoesNotOwnTheBoard(): Unit = {
    val board = boardFromDiagram(Array(
      ".OO......",
      ".O..XX...",
      "O.OOOX...",
      ".OO.X....",
      ".XXX.....",
      ".........",
      "..X...X..",
      ".........",
      "........."
    ))
    val points = scoredPoints(board)
    assertTrue(s"white's wall was scored as owning the board: $points", points > -20.0)
  }

  /** The finished game: black has two real eyes and about twenty dead white stones. */
  @Test
  def groupWithRealEyeOwnsItsTerritory(): Unit = {
    val board = boardFromDiagram(Array(
      ".O.XX.XO.",
      ".OX..X.X.",
      "OXOOOOX..",
      ".XOOXXXOO",
      ".XXOX.X..",
      "..XXO.XOO",
      ".OX.XXXXX",
      ".X.X..XOO",
      "..O..O..."
    ))
    val points = scoredPoints(board)
    assertTrue(s"black's territory was ignored: $points", points > 20.0)
  }

  @Test
  def firstLineIsWorseThanTheThirdLine(): Unit = {
    val edge = new GoBoard(9, 0)
    play(edge, 1, 5, black = true)
    val third = new GoBoard(9, 0)
    play(third, 3, 5, black = true)
    val edgeWorth = worth(edge)
    val thirdWorth = worth(third)
    assertTrue(s"first line $edgeWorth should be worse than third line $thirdWorth", thirdWorth >= edgeWorth + 1)
  }

  /** Same eye analysis the search uses, then the point score. */
  private def scoredPoints(board: GoBoard): Double = {
    val map = new GroupAnalyzerMap()
    new TerritoryUpdater(board, map).updateTerritory(false)
    new SearchScoreCalculator(board, GoOptions.DEFAULT_KOMI, map).points()
  }

  private def worth(board: GoBoard): Int = {
    val calculator = new WorthCalculator(board, new GroupAnalyzerMap())
    val move = new GoMove(new ByteLocation(1, 1), 0, new GoStone(true))
    calculator.worth(move, new GoWeights().getDefaultWeights)
  }

  private def livingBoard(lines: Array[String]): GoBoard = {
    val board = boardFromDiagram(lines)
    setHealth(board, black = 1.0f, white = -1.0f)
    board
  }

  private def play(board: GoBoard, row: Int, col: Int, black: Boolean): Unit = {
    val played = board.makeMove(new GoMove(new ByteLocation(row, col), 0, new GoStone(black)))
    assertTrue(s"move ($row,$col) should be legal", played)
  }

  private def setHealth(board: GoBoard, health: Float): Unit =
    setHealth(board, if health > 0 then health else health, if health > 0 then -health else health)

  private def setHealth(board: GoBoard, black: Float, white: Float): Unit = {
    for (r <- 1 to board.getNumRows; c <- 1 to board.getNumCols) {
      val pos = board.getPosition(r, c).asInstanceOf[GoBoardPosition]
      if (pos.isOccupied) {
        val signed = if (pos.getPiece.isOwnedByPlayer1) black else white
        pos.getPiece.asInstanceOf[GoStone].setHealth(signed)
      }
    }
  }

  private def setHealthAt(board: GoBoard, row: Int, col: Int, health: Float): Unit = {
    val pos = board.getPosition(row, col).asInstanceOf[GoBoardPosition]
    pos.getPiece.asInstanceOf[GoStone].setHealth(health)
  }

  private def markAlive(board: GoBoard, blackOnly: Boolean = false): Unit = {
    for (r <- 1 to board.getNumRows; c <- 1 to board.getNumCols) {
      val pos = board.getPosition(r, c).asInstanceOf[GoBoardPosition]
      if (pos.isOccupied && pos.getString != null && (!blackOnly || pos.getPiece.isOwnedByPlayer1)) {
        pos.getString.setUnconditionallyAlive(true)
      }
    }
  }

  private def boardFromDiagram(lines: Array[String]): GoBoard = {
    val size = lines.length
    require(lines.forall(_.length == size), "diagram must be square")
    val seed = new GoBoard(size, 0)
    for (r <- 1 to size; c <- 1 to size) {
      val stone = lines(r - 1).charAt(c - 1) match {
        case 'X' => new GoStone(true)
        case 'O' => new GoStone(false)
        case '.' => null
        case other => throw new IllegalArgumentException(s"bad char '$other'")
      }
      if (stone != null) {
        seed.setPosition(new GoBoardPosition(r, c, null, stone))
      }
    }
    new GoBoard(seed)
  }
}
