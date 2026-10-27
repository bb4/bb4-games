/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.common.cache.ScoreCache
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.go.options.GoOptions
import org.junit.Assert.{assertEquals, assertFalse, assertNotEquals, assertTrue}
import org.junit.Test
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
    assertTrue("corner stone is the resignation coordinate", board.getPosition(1, 1).isOccupied)
    val whiteLibs = board.getPosition(2, 3).asInstanceOf[GoBoardPosition].getString.getNumLiberties(board)
    assertEquals("white string should have one liberty before the capture", 1, whiteLibs)

    val pass = GoMove.createPassMove(0, true)
    searchable.makeInternalMove(pass)
    assertNotEquals("a pass must change the hash", beforeKey, searchable.getHashKey().getKey)
    searchable.undoInternalMove(pass)
    assertEquals("undoing a pass must restore hash and board", before, snapshot(searchable))

    val resignation = GoMove.createResignationMove(false)
    searchable.makeInternalMove(resignation)
    searchable.undoInternalMove(resignation)
    assertTrue("resignation undo must leave the stone at (1,1)", board.getPosition(1, 1).isOccupied)
    assertEquals("undoing a resignation must restore hash and board", before, snapshot(searchable))

    val capturing = stoneMove(3, 3, black = true)
    searchable.makeInternalMove(capturing)
    assertEquals("the filling move captures both white stones", 2, capturing.getNumCaptures)
    assertEquals(2, searchable.getNumCaptures(false))
    assertFalse(board.getPosition(2, 2).isOccupied)
    searchable.undoInternalMove(capturing)
    assertEquals("undoing a multi-stone capture must restore hash, liberties, and groups", before, snapshot(searchable))

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
