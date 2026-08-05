/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common

/**
 * Keep track of timing info for different game searching aspects.
 * @author Barry Becker
 */
class GameProfiler extends AbstractGameProfiler {

  add(AbstractGameProfiler.GENERATE_MOVES)
  add(AbstractGameProfiler.CALC_WORTH, AbstractGameProfiler.GENERATE_MOVES)
  add(AbstractGameProfiler.UNDO_MOVE)
  add(AbstractGameProfiler.MAKE_MOVE)
}

object GameProfiler {
  private var instance: GameProfiler = _

  def getInstance: GameProfiler = {
    if (instance == null) {
      instance = new GameProfiler()
    }
    instance
  }
}
