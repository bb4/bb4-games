/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe

import com.barrybecker4.game.twoplayer.gomoku.GoMokuBoard

/**
 * Representation of a TicTacToe Game Board
 *
 * @author Barry Becker
 */
class TicTacToeBoard extends GoMokuBoard {

  /**
   * Constructor.
   * Tic tac toe is always 3x3
   */
  setSize(3, 3)

  override def copy(): TicTacToeBoard = {
    new TicTacToeBoard(this)
  }

  private def this(pb: TicTacToeBoard) = {
    this()
    this.positions_ = pb.positions_.copy()
  }

  override def getMaxNumMoves: Int = 9

  override def getCandidateMoves: TicTacToeCandidateMoves = {
    new TicTacToeCandidateMoves(this)
  }
}
