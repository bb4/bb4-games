package com.barrybecker4.game.twoplayer.go.board.update

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * A liberty update must see every neighboring stone, including ones an earlier
 * search left marked visited. Otherwise a capture is missed and the placed stone
 * is reported to have no liberties.
 */
class TestNoLibertyCrash {

  /**
   * Filling the last liberty is legal when it captures. Visited stones must not
   * freeze the liberty cache, or the white stone stays and black has nowhere to breathe.
   */
  @Test
  def captureOnLastLibertySurvivesVisitedStones(): Unit = {
    val board = boardFromDiagram(Array(
      "XXXXX",
      "XXOXX",
      "XX.XX",
      "XXXXX",
      "XXXXX"
    ))
    markAllVisited(board)
    val move = new GoMove(new ByteLocation(3, 3), 0, new GoStone(true))
    assertTrue("capturing the last liberty is legal", !move.isSuicidal(board))
    assertTrue(board.makeMove(move))
    val stone = board.getPosition(3, 3).asInstanceOf[GoBoardPosition]
    assertTrue(
      s"liberties=${stone.getString.getNumLiberties(board)}\n$board",
      stone.getString.getNumLiberties(board) > 0
    )
    assertTrue("the white stone was captured", board.getPosition(2, 3).isUnoccupied)
  }

  /** The endgame crash position, with every stone left visited the way search can. */
  @Test
  def blackAtRow9Col5KeepsLibertiesWhenStonesAreVisited(): Unit = {
    val board = boardFromDiagram(Array(
      "..XOXXO..",
      "O...OXXO.",
      ".OOOOXOO.",
      ".X.O.XXOX",
      ".XOXX.O.O",
      "OXXXXOO..",
      "XOX..X.OO",
      "..X.XO.O.",
      "...X.XO.."
    ))
    markAllVisited(board)
    val move = new GoMove(new ByteLocation(9, 5), 0, new GoStone(true))
    assertTrue("the connection is legal", !move.isSuicidal(board))
    assertTrue(board.makeMove(move))
    val stone = board.getPosition(9, 5).asInstanceOf[GoBoardPosition]
    assertTrue(
      s"liberties=${stone.getString.getNumLiberties(board)}\n$board",
      stone.getString.getNumLiberties(board) > 0
    )
  }

  private def markAllVisited(board: GoBoard): Unit = {
    for (r <- 1 to board.getNumRows; c <- 1 to board.getNumCols) {
      board.getPosition(r, c).asInstanceOf[GoBoardPosition].setVisited(true)
    }
  }

  private def boardFromDiagram(lines: Array[String]): GoBoard = {
    val size = lines.length
    val seed = new GoBoard(size, 0)
    for (r <- 1 to size; c <- 1 to size) {
      val stone = lines(r - 1).charAt(c - 1) match {
        case 'X' => new GoStone(true)
        case 'O' => new GoStone(false)
        case '.' => null
        case other => throw new IllegalArgumentException(s"bad char '$other'")
      }
      if (stone != null) seed.setPosition(new GoBoardPosition(r, c, null, stone))
    }
    new GoBoard(seed)
  }
}
