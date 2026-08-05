// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis.differencers

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import com.barrybecker4.game.twoplayer.gomoku.analysis.LineFactory
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns

/**
 * Determines the difference in value between the most recent move
 * and how it was before in the up/down or left right direction.
 *
 * @author Barry Becker
 */
abstract class DiagonalDifferencer(board: TwoPlayerBoard[?], patterns: Patterns,
                                  lineFactory: LineFactory)
  extends ValueDifferencer(board, patterns, lineFactory) {

  protected var numRows: Int = scala.compiletime.uninitialized
  protected var numCols: Int = scala.compiletime.uninitialized

  protected def init(): Unit = {
    numRows = board_.getNumRows
    numCols = board_.getNumCols
  }
}
