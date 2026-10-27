/* Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku

import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerMove}

/**
 * Representation of a GoMoku Game Board.
 *
 * @author Barry Becker
 */
class GoMokuBoard(numRows: Int, numCols: Int) extends TwoPlayerBoard[TwoPlayerMove] {

  setSize(numRows, numCols)

  def this() = {
    this(30, 30)
  }

  private def this(pb: GoMokuBoard) = {
    this(pb.getNumRows, pb.getNumCols)
    this.positions_ = pb.positions_.copy()
  }

  override def copy(): GoMokuBoard = {
    new GoMokuBoard(this)
  }

  override def getMaxNumMoves: Int = {
    positions_.getNumBoardSpaces
  }

  def getCandidateMoves: CandidateMoves = {
    new CandidateMoves(this)
  }

  /**
   *  For gomoku, undoing a move is just changing that space back to a blank.
   *  @param move the move to undo
   */
  override def undoInternalMove(move: TwoPlayerMove): Unit = {
    getPosition(move.getToRow, move.getToCol).clear()
  }

  /**
   * Num different states.
   * This is used primarily for the Zobrist hash. You do not need to override if yo udo not use it.
   * States: player1, player2, empty.
   * @return number of different states this position can have.
   */
  override def getNumPositionStates: Int = 3
}
