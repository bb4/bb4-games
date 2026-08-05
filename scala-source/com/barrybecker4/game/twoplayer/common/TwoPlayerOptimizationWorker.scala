/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common

import com.barrybecker4.math.MathUtil
import com.barrybecker4.game.twoplayer.common.ui.OptimizationDoneHandler
import com.barrybecker4.optimization.Optimizer
import com.barrybecker4.optimization.parameter.ParameterArray
import com.barrybecker4.optimization.parameter.ParameterArrayWithFitness
import com.barrybecker4.optimization.strategy.OptimizationStrategyType
import scala.Option
import javax.swing.SwingWorker
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE

/**
 * This worker runs in a separate thread to figure out optimal game weights give the UI a chance to update.
 * The computer optimizes by playing against itself for a long time.
 *
 * @author Barry Becker
 */
class TwoPlayerOptimizationWorker(
  private val optimizer: Optimizer,
  private val initialWeights: ParameterArray,
  private val handler: OptimizationDoneHandler
) extends SwingWorker[ParameterArrayWithFitness, Integer]:

  import TwoPlayerOptimizationWorker.*

  override protected def doInBackground(): ParameterArrayWithFitness =
    optimizer.doOptimization(
      DEFAULT_STRATEGY,
      initialWeights,
      WINNING_VALUE,
      MathUtil.RANDOM,
      Option.empty,
      false
    )

  override protected def done(): Unit =
    try
      handler.done(get())
    catch
      case e: Exception =>
        GameContext.log(0, s"Optimization failed: $e")
        handler.onFailure(e)

object TwoPlayerOptimizationWorker:
  /** Scala enum case name; use OptimizationStrategyType.valueOf(String) for Java/IDE-friendly lookup. */
  private val DEFAULT_STRATEGY: OptimizationStrategyType =
    OptimizationStrategyType.valueOf("HILL_CLIMBING")
