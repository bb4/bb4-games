/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.board.{CaptureList, GamePiece}
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove

class ChessMove private (
    val fromLocation: Location,
    destination: Location,
    var captureList: CaptureList,
    value: Int,
    piece: GamePiece
) extends TwoPlayerMove(destination, value, piece) {

  private var firstTimeMoved: Boolean = true

  private def this(move: ChessMove) = {
    this(
      move.fromLocation,
      move.toLocation,
      if (move.captureList != null) move.captureList.copy() else null,
      move.getValue,
      move.getPiece
    )
    firstTimeMoved = move.firstTimeMoved
  }

  override def copy(): ChessMove = new ChessMove(this)

  def getFromRow: Int = fromLocation.row
  def getFromCol: Int = fromLocation.col

  def isFirstTimeMoved: Boolean = firstTimeMoved

  def setFirstTimeMoved(firstTimeMoved: Boolean): Unit = {
    this.firstTimeMoved = firstTimeMoved
  }

  override def toString: String = {
    val sb = new StringBuilder(super.toString)
    if (this.isFirstTimeMoved) sb.append(" firstTimeMoved ")
    if (captureList != null) sb.append(captureList.toString)
    sb.append(s" ($fromLocation)->($toLocation)")
    sb.toString
  }
}

object ChessMove {
  def createMove(
      origin: Location,
      destination: Location,
      captures: CaptureList,
      value: Int,
      piece: GamePiece
  ): ChessMove = {
    new ChessMove(origin, destination, captures, value, piece)
  }
}
