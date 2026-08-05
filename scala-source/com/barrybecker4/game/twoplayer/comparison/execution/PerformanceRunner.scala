// Copyright by Barry G. Becker, 2012-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.execution

import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPanel
import com.barrybecker4.game.twoplayer.comparison.model.config.SearchOptionsConfigList
import com.barrybecker4.game.twoplayer.comparison.ui.execution.GameRunnerDialog

/**
 * Run through the grid of game combinations and gather the performance results.
 * @author Barry Becker
 */
class PerformanceRunner(
  gamePanel: TwoPlayerPanel,
  optionsList: SearchOptionsConfigList,
  listener: PerformanceRunnerListener
) {

  def doComparisonRuns(): Unit = {
    val runnerDialog = new GameRunnerDialog(gamePanel)
    runnerDialog.showDialog
    gamePanel.init(null)

    val worker = new PerformanceWorker(gamePanel.get2PlayerController(), optionsList)
    worker.addListener(listener)
    worker.addListener(new PerformanceRunnerListener {
      override def performanceRunsDone(model: com.barrybecker4.game.twoplayer.comparison.model.ResultsModel): Unit = {
        runnerDialog.close()
      }
    })

    worker.execute()
  }
}
