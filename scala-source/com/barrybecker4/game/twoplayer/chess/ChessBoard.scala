/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess

import com.barrybecker4.game.common.board.{BoardPosition, CaptureList}
import com.barrybecker4.game.twoplayer.checkers.CheckersBoard
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard

class ChessBoard extends TwoPlayerBoard[ChessMove] {
  setSize(ChessBoard.SIZE, ChessBoard.SIZE)

  def this(b: ChessBoard) = {
    this()
    positions_ = b.positions_.copy()
  }

  override def copy(): ChessBoard = new ChessBoard(this)

  override def getMaxNumMoves: Int = 4 * ChessBoard.SIZE * ChessBoard.SIZE

  override def reset(): Unit = {
    super.reset()
    fillRows()
  }

  def fillRows(): Unit = {
    setupPlayerPieces(isPlayer1 = true)
    setupPlayerPieces(isPlayer1 = false)
  }

  private def setupPlayerPieces(isPlayer1: Boolean): Unit = {
    val numRows = getNumRows
    val pawnRow = if (isPlayer1) 2 else numRows - 1
    val kingRow = if (isPlayer1) 1 else numRows
    
    for (j <- 1 to getNumCols) {
      setPosition(new BoardPosition(kingRow, j, new ChessPiece(isPlayer1, ChessBoard.PIECE_ARRANGEMENT(j - 1))))
      setPosition(new BoardPosition(pawnRow, j, new ChessPiece(isPlayer1, ChessPieceType.PAWN)))
    }
  }

  def isKingCheckedByPosition(pos: BoardPosition, lastMove: ChessMove): Boolean = {
    if (pos.isUnoccupied) return false
    
    val moves = pos.getPiece.asInstanceOf[ChessPiece].findPossibleMoves(this, pos.getRow, pos.getCol, lastMove)
    
    import scala.jdk.CollectionConverters._
    moves.asScala.exists { move =>
      val cl = move.captureList
      if (cl != null && !cl.isEmpty) {
        val piece = cl.getFirst.getPiece.asInstanceOf[ChessPiece]
        piece.is(ChessPieceType.KING)
      } else {
        false
      }
    }
  }

  override def makeInternalMove(move: ChessMove): Boolean = {
    val oldPos = getPosition(move.getFromRow, move.getFromCol)
    val newPos = getPosition(move.getToRow, move.getToCol)

    removeCaptures(move.captureList)

    if (oldPos.getPiece != null) {
      move.setFirstTimeMoved(oldPos.getPiece.asInstanceOf[ChessPiece].isFirstTimeMoved)
      newPos.setPiece(move.getPiece)
      newPos.getPiece.asInstanceOf[ChessPiece].setFirstTimeMoved(false)
      getPosition(move.getFromRow, move.getFromCol).clear()
    }
    true
  }

  override def undoInternalMove(move: ChessMove): Unit = {
    val start = getPosition(move.getFromRow, move.getFromCol)
    start.setPiece(move.getPiece)
    getPosition(move.getToRow, move.getToCol).clear()
    start.getPiece.asInstanceOf[ChessPiece].setFirstTimeMoved(move.isFirstTimeMoved)
    restoreCaptures(move.captureList)
  }

  def removeCaptures(captureList: CaptureList): Unit = {
    if (captureList != null) captureList.removeFromBoard(this)
  }

  def restoreCaptures(captureList: CaptureList): Unit = {
    if (captureList != null) captureList.restoreOnBoard(this)
  }

  def causesSelfCheck(m: ChessMove): Boolean = {
    assert(m != null)
    makeMove(m)

    var checked = false
    var row = 1
    while (row <= getNumRows && !checked) {
      var col = 1
      while (col <= getNumCols && !checked) {
        val pos = getPosition(row, col)
        if (pos.isOccupied && pos.getPiece.isOwnedByPlayer1 == !m.isPlayer1) {
          checked = isKingCheckedByPosition(pos, m)
        }
        col += 1
      }
      row += 1
    }

    undoMove()
    checked
  }

  override def getNumPositionStates: Int = 12

  override def getStateIndex(pos: BoardPosition): Int = {
    if (pos.isOccupied) {
      val p = pos.getPiece.asInstanceOf[ChessPiece]
      p.typeIndex()
    } else {
      0
    }
  }
}

object ChessBoard {
  val SIZE: Int = 8

  private val PIECE_ARRANGEMENT: Array[ChessPieceType] = Array(
    ChessPieceType.ROOK, ChessPieceType.KNIGHT, ChessPieceType.BISHOP,
    ChessPieceType.QUEEN, ChessPieceType.KING,
    ChessPieceType.BISHOP, ChessPieceType.KNIGHT, ChessPieceType.ROOK
  )
}
