/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.common.profile.Profiler

/**
 * Keep track of timing info for different game searching aspects.
 * @author Barry Becker
 */
abstract class AbstractGameProfiler extends Profiler {

  private var searchTime: Long = scala.compiletime.uninitialized

  def startProfiling(): Unit = {
    searchTime = 0
    if (GameContext.isProfiling) {
      searchTime = System.currentTimeMillis()
      initialize()
    }
  }

  def stopProfiling(numMovesConsidered: Long): Unit = {
    if (GameContext.isProfiling) {
      val totalTime = System.currentTimeMillis() - searchTime
      showProfileStats(totalTime, numMovesConsidered)
    }
  }

  def showProfileStats(totalTime: Long, numMovesConsidered: Long): Unit = {
    GameContext.log(0, "----------------------------------------------------------------------------------\n")
    GameContext.log(0, s"There were $numMovesConsidered moves considered.\n")
    GameContext.log(0, s"The total time for the computer to move was : " +
      FormatUtil.formatNumber(totalTime.toFloat / 1000) + " seconds.\n")
    print()
  }

  def initialize(): Unit = {
    resetAll()
    setEnabled(GameContext.isProfiling)
    setLogger(GameContext.getLogger)
  }

  def startGenerateMoves(): Unit = {
    this.start(AbstractGameProfiler.GENERATE_MOVES)
  }

  def stopGenerateMoves(): Unit = {
    this.stop(AbstractGameProfiler.GENERATE_MOVES)
  }

  def startUndoMove(): Unit = {
    this.start(AbstractGameProfiler.UNDO_MOVE)
  }

  def stopUndoMove(): Unit = {
    this.stop(AbstractGameProfiler.UNDO_MOVE)
  }

  def startMakeMove(): Unit = {
    this.start(AbstractGameProfiler.MAKE_MOVE)
  }

  def stopMakeMove(): Unit = {
    this.stop(AbstractGameProfiler.MAKE_MOVE)
  }

  def startCalcWorth(): Unit = {
    this.start(AbstractGameProfiler.CALC_WORTH)
  }

  def stopCalcWorth(): Unit = {
    this.stop(AbstractGameProfiler.CALC_WORTH)
  }
}

object AbstractGameProfiler {
  val GENERATE_MOVES: String = "generating moves"
  val UNDO_MOVE: String = "undoing move"
  val MAKE_MOVE: String = "making move"
  val CALC_WORTH: String = "calculating worth"
}
