/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.board

import java.io.Serializable

/**
 * the BoardPosition describes the physical marker at a location on the board.
 * It may be empty if there is no piece there.
 * Immutable except for transparency and annotation which should not matter.
 *
 * @see Board
 * @author Barry Becker
 */
class GamePiece(
  var ownedByPlayer1: Boolean = false,
  var pieceType: Char = GamePiece.REGULAR_PIECE,
  private var transparency: Short = 0,
  private var annotation: String = null
) extends Serializable {

  def this(ownedByPlayer1: Boolean) = 
    this(ownedByPlayer1, GamePiece.REGULAR_PIECE, 0, null)

  def copy(): GamePiece = new GamePiece(ownedByPlayer1, pieceType, transparency, annotation)

  final def getType: Char = pieceType

  final def isOwnedByPlayer1: Boolean = ownedByPlayer1

  final def setTransparency(trans: Short): Unit = {
    transparency = trans
  }

  final def getTransparency: Short = transparency

  final def setAnnotation(annotation: String): Unit = {
    this.annotation = annotation
  }

  final def getAnnotation: String = annotation

  def getDescription: String = toString

  def getSymbol: Char = if (ownedByPlayer1) 'X' else 'O'

  override def toString: String = getSymbol.toString
}

object GamePiece {
  val P1_SYMB: Char = 'X'
  val P2_SYMB: Char = 'O'
  val REGULAR_PIECE: Char = 'x'
}
