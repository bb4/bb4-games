/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard

class CheckersBoard extends TwoPlayerBoard[CheckersMove] {
  setSize(CheckersBoard.SIZE, CheckersBoard.SIZE)

  def this(b: CheckersBoard) = {
    this()
    positions_ = b.positions_.copy()
  }

  override def copy(): CheckersBoard = new CheckersBoard(this)

  override def reset(): Unit = {
    super.reset()
    fillRows()
  }

  def fillRows(): Unit = {
    val pieceRows = if (CheckersBoard.SIZE > 7) 2 else 1
    
    for (i <- 1 to (1 + pieceRows)) {
      fillRow(i, i % CheckersBoard.TWO, player1 = true)
    }

    for (i <- CheckersBoard.SIZE - pieceRows to CheckersBoard.SIZE) {
      fillRow(i, i % CheckersBoard.TWO, player1 = false)
    }
  }

  private def fillRow(row: Int, odd: Int, player1: Boolean): Unit = {
    for (j <- 1 to CheckersBoard.SIZE / 2) {
      setPosition(new BoardPosition(row, CheckersBoard.TWO * j - odd,
        new CheckersPiece(player1, CheckersPieceType.REGULAR)))
    }
  }

  override def setSize(numRows: Int, numCols: Int): Unit = {
    super.setSize(numRows, numCols)
    if (numRows != CheckersBoard.SIZE || numCols != CheckersBoard.SIZE) {
      GameContext.log(0, s"Can't change the size of a checkers/chess board. It must be ${CheckersBoard.SIZE}x${CheckersBoard.SIZE}")
    }
  }

  override def getMaxNumMoves: Int = 4 * CheckersBoard.SIZE * CheckersBoard.SIZE

  override def makeInternalMove(move: CheckersMove): Boolean = {
    getPosition(move.getToRow, move.getToCol).setPiece(move.getPiece)
    move.removeCaptures(this)
    getPosition(move.getFromRow, move.getFromCol).clear()
    true
  }

  override def undoInternalMove(move: CheckersMove): Unit = {
    val startPos = getPosition(move.getFromRow, move.getFromCol)
    startPos.setPiece(move.getPiece.copy())
    
    if (move.kinged) {
      startPos.setPiece(new CheckersPiece(move.isPlayer1, CheckersPieceType.REGULAR))
    }
    
    move.restoreCaptures(this)
    getPosition(move.getToRow, move.getToCol).clear()
  }

  override def getNumPositionStates: Int = 5

  override def getStateIndex(pos: BoardPosition): Int = {
    if (pos.isOccupied) {
      val p = pos.getPiece.asInstanceOf[CheckersPiece]
      (if (p.isOwnedByPlayer1) 1 else 2) + (if (p.isKing) 0 else 2)
    } else {
      0
    }
  }
}

object CheckersBoard {
  val SIZE: Int = 8
  private val TWO: Int = 2
}
