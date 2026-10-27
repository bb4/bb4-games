/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.board

import com.barrybecker4.common.geometry.{ByteLocation, Location}

/**
 * The BoardPosition describes the physical marker at a location on the board.
 * It may be empty if there is no piece there.
 *
 * @see Board
 * @author Barry Becker
 */
class BoardPosition(
  var location: Location,
  var piece: GamePiece
) {

  def this(row: Int, col: Int, piece: GamePiece) = {
    this(new ByteLocation(row, col), piece)
  }

  def this(p: BoardPosition) = {
    this(new ByteLocation(p.getRow, p.getCol), if (p.piece != null) p.piece.copy() else null)
  }

  def copy(): BoardPosition = new BoardPosition(this)

  def getPiece: GamePiece = piece

  def setPiece(piece: GamePiece): Unit = {
    this.piece = piece
  }

  final def isUnoccupied: Boolean = piece == null

  final def isOccupied: Boolean = piece != null

  final def getRow: Int = location.row

  final def getCol: Int = location.col

  final def setLocation(loc: Location): Unit = {
    location = loc
  }

  final def getLocation: Location = location

  final def getDistanceFrom(position: BoardPosition): Double =
    location.getDistanceFrom(position.getLocation)

  final def isNeighbor(position: BoardPosition): Boolean =
    getDistanceFrom(position) == 1.0

  def clear(): Unit = {
    setPiece(null)
  }

  def getDescription: String = toString(longForm = true)

  override def toString: String = toString(longForm = false)

  private def toString(longForm: Boolean): String = {
    val sb = new StringBuilder()
    if (piece != null)
      sb.append(if (longForm) piece.getDescription else " " + piece.toString)
    sb.append(" (").append(location.toString).append(')')
    sb.toString()
  }

  override def equals(pos: Any): Boolean = {
    if (pos == null || !pos.getClass.equals(this.getClass)) {
      return false
    }

    val comparisonPos = pos.asInstanceOf[BoardPosition]
    val sameSide = if (getPiece != null && comparisonPos.getPiece != null) {
      getPiece.isOwnedByPlayer1 == comparisonPos.getPiece.isOwnedByPlayer1
    } else {
      getPiece == null && comparisonPos.getPiece == null
    }
    (getRow == comparisonPos.getRow) && (getCol == comparisonPos.getCol) && sameSide
  }

  override def hashCode(): Int = getRow * 300 + getCol
}
