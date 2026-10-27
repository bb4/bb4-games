/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.territory.TerritoryUpdater
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import org.junit.Assert.{assertEquals, assertFalse, assertTrue}
import org.junit.Test
import scala.jdk.CollectionConverters.*

/**
 * Life is shared across solid links and safe diagonals.
 * Jumps do not donate eyes, and a stale alive flag does not force health to ±1.
 */
class TestLifeConnections {

  @Test
  def diagonalSharesAGroupOnlyWhenACutIsOpen(): Unit = {
    val cut = new GoBoard(5, 0)
    play(cut, (2, 2, true), (3, 3, true), (2, 3, false), (3, 2, false))
    assertEquals("both cutting points occupied separates the stones", 2, blackGroupCount(cut))

    val open = new GoBoard(5, 0)
    play(open, (2, 2, true), (3, 3, true), (3, 2, false))
    assertEquals("one open cutting point joins the diagonal", 1, blackGroupCount(open))
  }

  @Test
  def onePointJumpDoesNotShareAGroup(): Unit = {
    val board = new GoBoard(5, 0)
    play(board, (3, 2, true), (3, 4, true))
    assertEquals(2, blackGroupCount(board))
  }

  @Test
  def staleAliveFlagDoesNotForcePerfectHealth(): Unit = {
    val board = new GoBoard(5, 0)
    play(board, (3, 3, true))
    val pos = board.getPosition(3, 3).asInstanceOf[GoBoardPosition]
    pos.getString.setUnconditionallyAlive(true)

    new TerritoryUpdater(board, new GroupAnalyzerMap()).updateTerritory(false)

    val health = pos.getPiece.asInstanceOf[GoStone].getHealth
    assertFalse(pos.getString.isUnconditionallyAlive)
    assertTrue(s"health was forced to $health", math.abs(health) < 0.5f)
  }

  private def play(board: GoBoard, moves: (Int, Int, Boolean)*): Unit =
    moves.foreach { case (row, col, black) =>
      board.makeMove(new GoMove(new ByteLocation(row, col), 0, new GoStone(black)))
    }

  private def blackGroupCount(board: GoBoard): Int =
    board.getGroups.asScala.count(_.isOwnedByPlayer1)
}
