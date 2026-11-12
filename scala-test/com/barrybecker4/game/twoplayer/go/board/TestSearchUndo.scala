/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.common.cache.ScoreCache
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.go.options.GoOptions
import org.junit.jupiter.api.Assertions.{assertEquals, assertFalse, assertNotEquals, assertTrue}
import org.junit.jupiter.api.Test
import scala.jdk.CollectionConverters.*

/**
 * Make/undo through GoSearchable must restore the hash, stones, liberties, and groups.
 */
class TestSearchUndo extends GoTestCase:

  override protected def getBoardSize: Int = 9

  @Test
  def makeUndoRestoresHashBoardAndGroups(): Unit =
    val board = new GoBoard(9, 0)
    val searchable = new GoSearchable(board, controller.getPlayers, new ScoreCache(), new GoOptions())

    // Two white stones with a single liberty at (3,3), and a real stone on (1,1)
    // so a resignation (stored at 1,1) must not delete it.
    val setup = Seq(
      (1, 1, true), (1, 2, true), (1, 3, true), (1, 4, true),
      (2, 1, true), (2, 4, true),
      (3, 1, true), (3, 2, true), (3, 4, true),
      (2, 2, false), (2, 3, false)
    )
    setup.foreach { case (r, c, black) =>
      searchable.makeInternalMove(stoneMove(r, c, black))
    }

    val before = snapshot(searchable)
    val beforeKey = searchable.getHashKey().getKey
    assertTrue(board.getPosition(1, 1).isOccupied, "corner stone is the resignation coordinate")
    val whiteLibs = board.getPosition(2, 3).asInstanceOf[GoBoardPosition].getString.getNumLiberties(board)
    assertEquals(1, whiteLibs, "white string should have one liberty before the capture")

    val pass = GoMove.createPassMove(0, true)
    searchable.makeInternalMove(pass)
    assertNotEquals(beforeKey, searchable.getHashKey().getKey, "a pass must change the hash")
    searchable.undoInternalMove(pass)
    assertEquals(before, snapshot(searchable), "undoing a pass must restore hash and board")

    val resignation = GoMove.createResignationMove(false)
    searchable.makeInternalMove(resignation)
    searchable.undoInternalMove(resignation)
    assertTrue(board.getPosition(1, 1).isOccupied, "resignation undo must leave the stone at (1,1)")
    assertEquals(before, snapshot(searchable), "undoing a resignation must restore hash and board")

    val capturing = stoneMove(3, 3, black = true)
    searchable.makeInternalMove(capturing)
    assertEquals(2, capturing.getNumCaptures, "the filling move captures both white stones")
    assertEquals(2, searchable.getNumCaptures(false))
    assertFalse(board.getPosition(2, 2).isOccupied)
    searchable.undoInternalMove(capturing)
    assertEquals(before, snapshot(searchable), "undoing a multi-stone capture must restore hash, liberties, and groups")

  private def stoneMove(row: Int, col: Int, black: Boolean): GoMove =
    new GoMove(new ByteLocation(row, col), 0, new GoStone(black))

  private def snapshot(searchable: GoSearchable): String =
    val board = searchable.getBoard
    val stones = new StringBuilder
    for r <- 1 to board.getNumRows; c <- 1 to board.getNumCols do
      val pos = board.getPosition(r, c).asInstanceOf[GoBoardPosition]
      if pos.isOccupied then
        val libs = if pos.getString != null then pos.getString.getNumLiberties(board) else -1
        stones.append(s"$r,$c,${pos.getPiece.isOwnedByPlayer1},$libs;")
    val groups = board.getGroups.asScala.toSeq.map { g =>
      s"${g.isOwnedByPlayer1}:${g.getNumStones}:${g.getNumLiberties(board)}"
    }.sorted.mkString(",")
    s"hash=${searchable.getHashKey().getKey}|caps=${searchable.getNumCaptures(true)},${searchable.getNumCaptures(false)}|moves=${searchable.getNumMoves()}|groups=$groups|stones=$stones"
