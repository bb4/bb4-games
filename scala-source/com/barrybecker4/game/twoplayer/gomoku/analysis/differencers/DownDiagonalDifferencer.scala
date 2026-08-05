// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis.differencers

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import com.barrybecker4.game.twoplayer.gomoku.analysis.LineFactory
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Determines the difference in value between the most recent move
 * and how it was before in the downward diagonal direction.
 *
 * @author Barry Becker
 */
class DownDiagonalDifferencer(board: TwoPlayerBoard[?], patterns: Patterns,
                             lineFactory: LineFactory)
  extends DiagonalDifferencer(board, patterns, lineFactory) {

  override def findValueDifference(row: Int, col: Int, weights: ParameterArray): Int = {
    init()
    var startc = col - winLength
    var startr = row - winLength
    if (startc < 1) {
      startr += (1 - startc)
      startc = 1
    }
    if (startr < 1) {
      startc += (1 - startr)
      startr = 1
    }
    val stopc = col + winLength
    var stopr = row + winLength
    if (stopc > numCols) {
      stopr += (numCols - stopc)
    }
    if (stopr > numRows) {
      stopr = numRows
    }

    val line = lineFactory_.createLine(patterns_, weights)
    for (i <- startr to stopr)
      line.append(board_.getPosition(i, startc + i - startr))

    val position = row - startr
    line.computeValueDifference(position)
  }
}
