// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis.differencers

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import com.barrybecker4.game.twoplayer.gomoku.analysis.{Direction, LineFactory}
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns

/**
 * Determines the difference in value between the most recent move
 * and how it was before in one specific direction
 *
 * @author Barry Becker
 */
class ValueDifferencerFactory(protected val patterns_ : Patterns,
                              protected val lineFactory_ : LineFactory) {

  /**
   * @param dir the direction used to decide which differencer to create.
   * @return differencer for the specified direction.
   */
  def createValueDifferencer(board: TwoPlayerBoard[_], dir: Direction): ValueDifferencer = {
    dir match {
      case Direction.VERTICAL | Direction.HORIZONTAL =>
        new StraightDifferencer(board, patterns_, lineFactory_, dir)
      case Direction.UP_DIAGONAL =>
        new UpDiagonalDifferencer(board, patterns_, lineFactory_)
      case Direction.DOWN_DIAGONAL =>
        new DownDiagonalDifferencer(board, patterns_, lineFactory_)
    }
  }
}
