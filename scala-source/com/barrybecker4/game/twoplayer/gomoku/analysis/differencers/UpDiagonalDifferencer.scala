// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis.differencers

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import com.barrybecker4.game.twoplayer.gomoku.analysis.LineFactory
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Determines the difference in value between the most recent move
 * and how it was before in the upward diagonal direction.
 *
 * @author Barry Becker
 */
class UpDiagonalDifferencer(board: TwoPlayerBoard[?], patterns: Patterns,
                           lineFactory: LineFactory)
  extends DiagonalDifferencer(board, patterns, lineFactory) {

  override def findValueDifference(row: Int, col: Int, weights: ParameterArray): Int = {
    init()
    var startc = col - winLength
    var startr = row + winLength
    if (startc < 1) {
      startr += startc - 1
      startc = 1
    }
    if (startr > numRows) {
      startc += startr - numRows
      startr = numRows
    }
    var stopc = col + winLength
    var stopr = row - winLength
    if (stopc > numCols) {
      stopr += stopc - numCols
      stopc = numCols
    }
    if (stopr < 1) {
      stopc += stopr - 1
    }
    val line = lineFactory_.createLine(patterns_, weights)
    for (i <- startc to stopc)
      line.append(board_.getPosition(startr - i + startc, i))

    val position = col - startc
    line.computeValueDifference(position)
  }
}
