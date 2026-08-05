/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.{Board, BoardPosition, GamePiece}
import scala.jdk.CollectionConverters._

/**
 * Defines the structure of the board and the pieces on it.
 * Each BoardPosition can contain a piece.
 *
 * @author Barry Becker
 */
abstract class TwoPlayerBoard[M <: TwoPlayerMove] extends Board[M] {

  /** copy constructor */
  def this(board: TwoPlayerBoard[M]) = {
    this()
  }

  def copy(): TwoPlayerBoard[M]

  /**
   * given a move specification, execute it on the board
   * This places the players symbol at the position specified by move.
   * @param move the move to make, if possible.
   * @return false if the move is illegal.
   */
  override def makeInternalMove(move: M): Boolean = {
    if (!move.isPassOrResignation) {
      val pos = getPosition(move.getToLocation)
      assert(move.getPiece != null, s"move's piece was null :$move")
      pos.setPiece(move.getPiece)
      val piece = pos.getPiece

      if (GameContext.getDebugMode > 0) {
        piece.setAnnotation(getMoveList.getNumMoves.toString)
      }
    }
    true
  }

  /**
   * @param moves list of moves to make all at once.
   */
  def makeMoves(moves: java.util.List[M]): Unit = {
    moves.asScala.foreach(makeMove)
  }

  /**
   * Num different states. E.g. black queen.
   * This is used primarily for the Zobrist hash. You do not need to override if you do not use it.
   * @return number of different states this position can have.
   */
  def getNumPositionStates: Int

  /**
   * The index of the state for this position.
   * Perhaps this would be better abstract.
   * @return The index of the state for this position.
   */
  def getStateIndex(pos: BoardPosition): Int = {
    if (!pos.isOccupied) {
      throw new IllegalArgumentException(s"this should only be called on occupied positions. Position = $pos")
    }
    if (pos.getPiece.isOwnedByPlayer1) 1 else 2
  }

  override def toString: String = {
    val bldr = new StringBuilder(1000)
    bldr.append("\n")
    val nRows = getNumRows
    val nCols = getNumCols
    val lastMove = getMoveList.getLastMove

    for (i <- 1 to nRows) {
      var followingLastMove = false
      for (j <- 1 to nCols) {
        val pos = this.getPosition(i, j)
        if (pos.isOccupied) {
          if (lastMove != null && pos.getLocation.equals(lastMove.getToLocation)) {
            bldr.append("[").append(pos.getPiece).append("]")
            followingLastMove = true
          } else {
            bldr.append(if (followingLastMove) "" else " ").append(pos.getPiece)
            followingLastMove = false
          }
        } else {
          bldr.append(if (followingLastMove) "" else " ").append("_")
          followingLastMove = false
        }
      }
      bldr.append("\n")
    }
    bldr.toString
  }
}
