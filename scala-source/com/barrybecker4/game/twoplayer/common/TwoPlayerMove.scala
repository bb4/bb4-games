/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.common.{GameContext, Move}
import com.barrybecker4.game.common.board.GamePiece

/**
 * This base class describes a change in state from one board position to the next in a game.
 * Perhaps save space by removing some of these members.
 * Consider splitting this into TwoPlayerMove (immutable part) and TwoPlayerMoveNode (game tree parts)
 * @author Barry Becker
 */
class TwoPlayerMove(
  var toLocation: Location = null,
  private var piece: GamePiece = null
) extends Move {

  private var inheritedValue: Int = 0
  private var player1: Boolean = false
  private var urgent: Boolean = false
  var isPass: Boolean = false
  var isResignation: Boolean = false
  private var selected: Boolean = false
  private var isFutureMove: Boolean = false
  private var scoreDescription: String = null

  /**
   * Create a move object representing a transition on the board.
   */
  def this(destination: Location, value: Int, p: GamePiece) = {
    this(destination, p)
    setValue(value)
    inheritedValue = getValue
    selected = false
    if (p != null) {
      player1 = p.isOwnedByPlayer1
    }
    isPass = false
  }

  /**
   * Copy constructor
   */
  def this(move: TwoPlayerMove) = {
    this(move.getToLocation, move.getValue, if (move.getPiece != null) move.getPiece.copy() else null)
    this.inheritedValue = move.inheritedValue
    this.selected = move.selected
    this.isPass = move.isPass
    this.isFutureMove = move.isFutureMove
    this.urgent = move.urgent
    this.isResignation = move.isResignation
    this.scoreDescription = move.scoreDescription
    this.setPlayer1(move.isPlayer1)
  }

  /**
   * @return a deep copy.
   */
  override def copy(): TwoPlayerMove = new TwoPlayerMove(this)

  def getToRow: Byte = toLocation.row.toByte

  def getToCol: Byte = toLocation.col.toByte

  def getToLocation: Location = toLocation

  /**
   * We sort based on the statically evaluated board value
   * because the inherited value is not known yet.
   * @return > 0 if move m is bigger, < 0 if smaller, =0 if equal
   */
  override def compareTo(m: Move): Int = {
    val result = super.compareTo(m)
    if (result != 0) {
      return result
    }

    val move = m.asInstanceOf[TwoPlayerMove]
    if (this.getToRow < move.getToRow)
      -1
    else if (this.getToRow > move.getToRow)
      1
    else {
      if (this.getToCol < move.getToCol) {
        -1
      } else if (this.getToCol > move.getToCol) {
        1
      } else {
        0
      }
    }
  }

  /**
   * CRITICAL: equals checks player1 and toLocation ONLY (not getValue)
   * This is intentionally inconsistent with hashCode - DO NOT CHANGE
   */
  override def equals(o: Any): Boolean = o match {
    case that: TwoPlayerMove =>
      player1 == that.player1 && toLocation != null && toLocation.equals(that.toLocation)
    case _ => false
  }

  /**
   * CRITICAL: hashCode mixes getValue() even though equals doesn't use it
   * This inconsistency is INTENTIONAL - DO NOT CHANGE
   */
  override def hashCode(): Int = {
    var result = if (toLocation != null) toLocation.hashCode() else 0
    result = 31 * result + getValue
    result = 31 * result + (if (player1) 1 else 0)
    result
  }

  def isPassingMove: Boolean = isPass

  def isResignationMove: Boolean = isResignation

  def isPassOrResignation: Boolean = isPass || isResignation

  def getInheritedValue: Int = inheritedValue

  def setInheritedValue(inheritedValue: Int): Unit = {
    this.inheritedValue = inheritedValue
  }

  def isPlayer1: Boolean = player1

  def setPlayer1(player1: Boolean): Unit = {
    this.player1 = player1
  }

  def getPiece: GamePiece = piece

  def setPiece(piece: GamePiece): Unit = {
    this.piece = piece
  }

  def isUrgent: Boolean = urgent

  def setUrgent(urgent: Boolean): Unit = {
    this.urgent = urgent
  }

  def isSelected: Boolean = selected

  def setSelected(selected: Boolean): Unit = {
    this.selected = selected
  }

  def isFuture: Boolean = isFutureMove

  def setFuture(future: Boolean): Unit = {
    isFutureMove = future
  }

  def getScoreDescription: String = scoreDescription

  def setScoreDescription(desc: String): Unit = {
    scoreDescription = desc
  }

  /**
   * @return a string, which if executed will create a move identical to this instance.
   */
  def getConstructorString: String = {
    val pieceCreator = if (getPiece != null) {
      if (getPiece.isOwnedByPlayer1) "PLAYER1_PIECE" else "PLAYER2_PIECE"
    } else {
      "null"
    }
    s"TwoPlayerMove.createMove(new ByteLocation(${getToLocation.row}, ${getToLocation.col}), $getValue, $pieceCreator),"
  }

  override def toString: String = {
    val s = new StringBuilder()
    val P1 = GameContext.getLabel("PLAYER1")
    val P2 = GameContext.getLabel("PLAYER2")

    s.append(if (player1) P1 else P2)
    s.append(" val:").append(FormatUtil.formatNumber(getValue))
    s.append(" inhrtd:").append(FormatUtil.formatNumber(inheritedValue))
    if (piece != null) {
      s.append(" piece: ").append(piece.toString)
    }
    if (!(isPass || isResignation)) {
      s.append('(').append(toLocation.toString).append(')')
    }
    if (urgent) {
      s.append(" urgent!")
    }
    if (isPass) {
      s.append(" Passing move")
    }
    if (isResignation) {
      s.append(" Resignation move")
    }
    s.append(" ")
    s.toString
  }
}

object TwoPlayerMove {
  /**
   * factory method for getting new moves. It uses recycled objects if possible.
   * @return the newly created move.
   */
  def createMove(destinationRow: Int, destinationCol: Int, value: Int, piece: GamePiece): TwoPlayerMove = {
    new TwoPlayerMove(ByteLocation(destinationRow.toByte, destinationCol.toByte), value, piece)
  }

  /**
   * factory method for getting new moves. It uses recycled objects if possible.
   * @return the newly created move.
   */
  def createMove(destinationLocation: Location, value: Int, piece: GamePiece): TwoPlayerMove = {
    new TwoPlayerMove(destinationLocation, value, piece)
  }
}
