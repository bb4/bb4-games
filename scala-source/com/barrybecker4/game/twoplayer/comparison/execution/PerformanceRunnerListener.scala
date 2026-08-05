// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.execution

import com.barrybecker4.game.twoplayer.comparison.model.ResultsModel

/**
 * @author Barry Becker
 */
trait PerformanceRunnerListener {
  def performanceRunsDone(model: ResultsModel): Unit
}
