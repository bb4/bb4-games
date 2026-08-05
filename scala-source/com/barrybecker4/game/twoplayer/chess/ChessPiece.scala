/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess

import com.barrybecker4.game.common.board.{Board, BoardPosition, GamePiece}
import com.barrybecker4.optimization.parameter.ParameterArray

class ChessPiece(player1: Boolean, private val chessPieceType: ChessPieceType) 
    extends GamePiece(player1, chessPieceType.getSymbol) {

  private var firstTimeMoved: Boolean = true

  def this(piece: ChessPiece) = {
    this(piece.isOwnedByPlayer1, piece.getPieceType)
    this.firstTimeMoved = piece.firstTimeMoved
  }

  override def copy(): ChessPiece = new ChessPiece(this)

  def is(pieceType: ChessPieceType): Boolean = this.chessPieceType == pieceType

  def getPieceType: ChessPieceType = chessPieceType

  def isFirstTimeMoved: Boolean = firstTimeMoved

  def setFirstTimeMoved(firstTimeMoved: Boolean): Unit = {
    this.firstTimeMoved = firstTimeMoved
  }

  def findPossibleMoves(board: Board[ChessMove], row: Int, col: Int, lastMove: ChessMove): java.util.List[ChessMove] = {
    chessPieceType.findPossibleMoves(board, row, col, lastMove, this)
  }

  def getWeightedScore(side: Int, pos: BoardPosition, weights: ParameterArray, advancement: Int): Double = {
    chessPieceType.getWeightedScore(side, pos, weights, advancement)
  }

  def typeIndex(): Int = chessPieceType.ordinal + 1

  override def toString: String = {
    val sb = new StringBuilder(super.toString)
    if (this.isFirstTimeMoved) sb.append(" notYetMoved ")
    sb.toString
  }
}
