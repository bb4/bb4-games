// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis.differencers

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import com.barrybecker4.game.twoplayer.gomoku.analysis.{Direction, LineFactory}
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Determines the difference in value between the most recent move
 * and how it was before in the up/down or left right direction.
 *
 * @author Barry Becker
 */
class StraightDifferencer(board: TwoPlayerBoard[_], patterns: Patterns,
                         lineFactory: LineFactory, dir: Direction)
  extends ValueDifferencer(board, patterns, lineFactory) {

  private val isVertical: Boolean = {
    assert(dir == Direction.VERTICAL || dir == Direction.HORIZONTAL, "unexpected direction")
    dir == Direction.VERTICAL
  }

  override def findValueDifference(row: Int, col: Int, weights: ParameterArray): Int = {
    val numMax = if (isVertical) board_.getNumRows else board_.getNumCols
    val currentPos = if (isVertical) row else col
    var start = currentPos - winLength
    if (start < 1) {
      start = 1
    }
    var stop = currentPos + winLength
    if (stop > numMax) {
      stop = numMax
    }

    val line = lineFactory_.createLine(patterns_, weights)
    for (i <- start to stop) {
      if (isVertical) {
        line.append(board_.getPosition(i, col))
      } else {
        line.append(board_.getPosition(row, i))
      }
    }

    val position = currentPos - start
    line.computeValueDifference(position)
  }
}
