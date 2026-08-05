/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.board.{CaptureList, GamePiece}
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove

class CheckersMove private (
    val fromLocation: Location,
    destination: Location,
    var captureList: CaptureList,
    value: Int,
    piece: GamePiece
) extends TwoPlayerMove(destination, value, piece) {

  var kinged: Boolean = false

  private def this(move: CheckersMove) = {
    this(
      move.fromLocation,
      move.toLocation,
      if (move.captureList != null) move.captureList.copy() else null,
      move.getValue,
      move.getPiece
    )
    kinged = move.kinged
  }

  override def copy(): CheckersMove = new CheckersMove(this)

  def setToLocation(toPos: Location): Unit = {
    toLocation = toPos
  }

  def getFromRow: Int = fromLocation.row
  def getFromCol: Int = fromLocation.col

  def removeCaptures(b: CheckersBoard): Unit = {
    if (captureList != null) captureList.removeFromBoard(b)
  }

  def restoreCaptures(b: CheckersBoard): Unit = {
    if (captureList != null) captureList.restoreOnBoard(b)
  }

  override def equals(o: Any): Boolean = {
    if (!o.isInstanceOf[CheckersMove]) return false
    val that = o.asInstanceOf[CheckersMove]
    
    if (!super.equals(o)) return false
    if (this.getPiece.getType != that.getPiece.getType) return false
    if (fromLocation != null) {
      if (!fromLocation.equals(that.fromLocation)) return false
    } else if (that.fromLocation != null) return false
    
    true
  }

  override def hashCode(): Int = {
    var result = super.hashCode()
    result = 31 * result + (if (fromLocation != null) fromLocation.hashCode() else 0)
    result = 31 * result + (if (this.getPiece.getType == CheckersPiece.KING) 1 else 0)
    result
  }

  override def toString: String = {
    var s = super.toString
    if (kinged) s += " (the piece was just kinged)"
    if (captureList != null) s += captureList.toString
    s += s" ($fromLocation)->($toLocation)"
    s
  }
}

object CheckersMove {
  def createMove(
      origin: Location,
      destination: Location,
      captures: CaptureList,
      value: Int,
      piece: GamePiece
  ): CheckersMove = {
    val m = new CheckersMove(origin, destination, captures, value, piece)

    if ((piece.getType == CheckersPiece.REGULAR_PIECE) &&
        ((piece.isOwnedByPlayer1 && m.getToRow == CheckersBoard.SIZE) ||
         (!piece.isOwnedByPlayer1 && m.getToRow == 1))) {
      m.kinged = true
      m.setPiece(new CheckersPiece(m.isPlayer1, CheckersPieceType.KING))
    }
    m
  }
}
