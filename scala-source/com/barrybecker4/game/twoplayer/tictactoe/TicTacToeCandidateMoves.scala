// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.tictactoe

import com.barrybecker4.game.twoplayer.gomoku.{CandidateMoves, GoMokuBoard}

/**
 * Representation of the candidate next moves on a TicTacToe Game Board.
 * Immutable after construction.
 * @author Barry Becker
 */
class TicTacToeCandidateMoves(board: TicTacToeBoard) extends CandidateMoves():

  private val numRows = board.getNumRows
  private val numCols = board.getNumCols

  /** this is an auxiliary structure to help determine candidate moves. should extract to sep class. */
  private val candidateMoves_ = Array.ofDim[Boolean](numRows + 2, numCols + 2)

  determineCandidateMoves(board)

  /**
   * We consider only those spaces bordering on non-empty spaces.
   * In theory all empties should be considered, but in practice only
   * those bordering existing moves are likely to be favorable.
   *
   * @return true if this position is a possible next move
   */
  override def isCandidateMove(row: Int, col: Int): Boolean =
    candidateMoves_(row)(col)

  /**
   * All empty positions are candidate moves for tic tac toe.
   * This is a bit similar than what we do for gomoku.
   */
  override protected def determineCandidateMoves(board: GoMokuBoard): Unit =
    var i = 1
    while i <= numRows do
      var j = 1
      while j <= numCols do
        if !board.getPosition(i, j).isOccupied then
          candidateMoves_(i)(j) = true
        j += 1
      i += 1
