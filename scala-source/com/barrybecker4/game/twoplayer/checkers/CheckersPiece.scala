/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers

import com.barrybecker4.game.common.board.GamePiece

class CheckersPiece(player1: Boolean, pieceType: CheckersPieceType) 
    extends GamePiece(player1, pieceType.symbol) {

  def this(piece: CheckersPiece) = {
    this(piece.isOwnedByPlayer1, CheckersPieceType.values.find(_.symbol == piece.getType).get)
  }

  override def copy(): CheckersPiece = new CheckersPiece(this)

  def isKing: Boolean = getType == CheckersPieceType.KING.symbol
  
  def getPieceType: CheckersPieceType = 
    CheckersPieceType.values.find(_.symbol == getType).get
}

object CheckersPiece {
  val REGULAR_PIECE: Char = CheckersPieceType.REGULAR.symbol
  val KING: Char = CheckersPieceType.KING.symbol
}
