// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.board

import com.barrybecker4.game.common.Move
import org.scalatest.funsuite.AnyFunSuite

class CaptureListSuite extends AnyFunSuite:

  /** Minimal board so CaptureList can remove/restore pieces. */
  private class TinyBoard extends Board[Move]:
    setSize(3, 3)
    override def makeInternalMove(move: Move): Boolean = true
    override def undoInternalMove(move: Move): Unit = ()
    override def getMaxNumMoves: Int = 9
    override def copy(): TinyBoard =
      val c = new TinyBoard()
      c.positions_ = positions_.copy()
      c

  test("alreadyCaptured matches row, col, and piece type") {
    val captures = new CaptureList()
    val piece = new GamePiece(true, 'K', 0, null)
    val pos = new BoardPosition(2, 3, piece)
    captures.add(pos)

    assert(captures.alreadyCaptured(new BoardPosition(2, 3, new GamePiece(true, 'K', 0, null))))
    assert(!captures.alreadyCaptured(new BoardPosition(1, 3, new GamePiece(true, 'K', 0, null))))
    assert(!captures.alreadyCaptured(new BoardPosition(2, 3, new GamePiece(true, 'Q', 0, null))))
  }

  test("removeFromBoard and restoreOnBoard round-trip") {
    val board = new TinyBoard()
    val piece = new GamePiece(true)
    board.getPosition(2, 2).setPiece(piece)

    val captures = new CaptureList()
    captures.add(new BoardPosition(2, 2, piece.copy()))

    captures.removeFromBoard(board)
    assert(board.getPosition(2, 2).isUnoccupied)

    captures.restoreOnBoard(board)
    assert(board.getPosition(2, 2).isOccupied)
    assert(board.getPosition(2, 2).getPiece.isOwnedByPlayer1)
  }

  test("toString lists captures") {
    val captures = new CaptureList()
    captures.add(new BoardPosition(1, 1, new GamePiece(true)))
    captures.add(new BoardPosition(2, 2, new GamePiece(false)))
    val s = captures.toString
    assert(s.contains("captured"))
    assert(s.contains("(1, 1)") || s.contains("1, 1") || s.contains("1"))
  }

  test("copy is independent") {
    val captures = new CaptureList()
    captures.add(new BoardPosition(1, 1, new GamePiece(true)))
    val copied = captures.copy()
    captures.clear()
    assert(copied.size() == 1)
    assert(captures.isEmpty)
  }
