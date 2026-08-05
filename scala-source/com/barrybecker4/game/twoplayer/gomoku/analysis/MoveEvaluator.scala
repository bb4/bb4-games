/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.analysis

import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.gomoku.GoMokuBoard
import com.barrybecker4.game.twoplayer.gomoku.analysis.differencers.{ValueDifferencer, ValueDifferencerFactory}
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Evaluates the value a recent move.
 * It does this by comparing the change in worth of the line patterns
 * vertically, horizontally, and diagonally through the new move's position.
 *
 * @author Barry Becker
 */
class MoveEvaluator(val patterns: Patterns) {

  var differencerFactory: ValueDifferencerFactory = new ValueDifferencerFactory(patterns, new LineFactory())
  private var vertDifferencer: ValueDifferencer = scala.compiletime.uninitialized
  private var horzDifferencer: ValueDifferencer = scala.compiletime.uninitialized
  private var upDiagDifferencer: ValueDifferencer = scala.compiletime.uninitialized
  private var downDiagDifferencer: ValueDifferencer = scala.compiletime.uninitialized

  /**
   * Used for testing to inject something that will create mock differencers.
   */
  def setValueDifferencerFactory(differencerFactory: ValueDifferencerFactory): Unit = {
    this.differencerFactory = differencerFactory
  }

  private def createDifferencers(board: GoMokuBoard): Unit = {
    vertDifferencer = differencerFactory.createValueDifferencer(board, Direction.VERTICAL)
    horzDifferencer = differencerFactory.createValueDifferencer(board, Direction.HORIZONTAL)
    upDiagDifferencer = differencerFactory.createValueDifferencer(board, Direction.UP_DIAGONAL)
    downDiagDifferencer = differencerFactory.createValueDifferencer(board, Direction.DOWN_DIAGONAL)
  }

  /**
   * Statically evaluate the board position from player1's point of view. Larger values are good for player1.
   * @return the lastMove's value modified by the value add of the new move.
   *  a large positive value means that the move is good from player1's viewpoint.
   */
  def worth(board: GoMokuBoard, lastMove: TwoPlayerMove, weights: ParameterArray): Int = {
    val row = lastMove.getToRow
    val col = lastMove.getToCol
    createDifferencers(board)

    val piece = board.getPosition(row, col).getPiece
    if (piece == null) {
      throw new IllegalStateException(
        "There must be a piece where the last move was played (" + row + ", " + col + ")")
    }
    if (lastMove.isPlayer1 != piece.isOwnedByPlayer1) {
      throw new IllegalStateException("The last move played must be for the same player found on the board.")
    }

    var diff = 0
    diff = horzDifferencer.findValueDifference(row, col, weights)
    diff += vertDifferencer.findValueDifference(row, col, weights)
    diff += upDiagDifferencer.findValueDifference(row, col, weights)
    diff += downDiagDifferencer.findValueDifference(row, col, weights)

    lastMove.getValue + diff
  }
}
