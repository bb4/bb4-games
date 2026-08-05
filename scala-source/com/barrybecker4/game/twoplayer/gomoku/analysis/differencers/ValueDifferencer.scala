// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis.differencers

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import com.barrybecker4.game.twoplayer.gomoku.analysis.LineFactory
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Determines the difference in value between the most recent move
 * and how it was before in one specific direction
 *
 * @author Barry Becker
 */
abstract class ValueDifferencer(protected val board_ : TwoPlayerBoard[_],
                               protected val patterns_ : Patterns,
                               protected val lineFactory_ : LineFactory) {

  protected val winLength: Int = patterns_.getWinRunLength

  /**
   * @return the difference in worth after making a move compared with before.
   */
  def findValueDifference(row: Int, col: Int, weights: ParameterArray): Int
}
