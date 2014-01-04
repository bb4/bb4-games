// Copyright by Barry G. Becker, 2012-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.execution;

import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPanel;
import com.barrybecker4.game.twoplayer.comparison.model.ResultsModel;
import com.barrybecker4.game.twoplayer.comparison.model.config.SearchOptionsConfigList;
import com.barrybecker4.game.twoplayer.comparison.ui.execution.GameRunnerDialog;

/**
 * Run through the grid of game combinations and gather the performance results.
 * @author Barry Becker
 */
public class PerformanceRunner {

    private SearchOptionsConfigList optionsList;
    private TwoPlayerPanel gamePanel_;
    private PerformanceRunnerListener reporter;

    /**
     * Construct the runner
     */
    public PerformanceRunner(TwoPlayerPanel gamePanel, SearchOptionsConfigList optionsList,
                             PerformanceRunnerListener listener)  {
        this.optionsList = optionsList;
        this.gamePanel_ = gamePanel;
        this.reporter = listener;
    }

    /**
     * Run the NxN comparison and let the reporter do what it wants with the results.
     */
    public void doComparisonRuns() {

        final GameRunnerDialog runnerDialog = new GameRunnerDialog(gamePanel_);
        runnerDialog.showDialog();
        gamePanel_.init(null);

        // add listeners not in constructor
        PerformanceWorker worker =
                new PerformanceWorker(gamePanel_.get2PlayerController(), optionsList);
        worker.addListener(reporter);
        worker.addListener(new PerformanceRunnerListener() {
            @Override
            public void performanceRunsDone(ResultsModel model) {
                 runnerDialog.close();
            }
        });

        worker.execute();
    }
}
