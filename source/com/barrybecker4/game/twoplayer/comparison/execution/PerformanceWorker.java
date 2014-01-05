// Copyright by Barry G. Becker, 2012-2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.execution;

import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.comparison.model.PerformanceResultsPair;
import com.barrybecker4.game.twoplayer.comparison.model.ResultsModel;
import com.barrybecker4.game.twoplayer.comparison.model.config.SearchOptionsConfigList;

import javax.swing.SwingWorker;
import java.util.LinkedList;
import java.util.List;

/**
 * A worker that will run all the computer vs computer games serially in a separate thread.
 * @author Barry Becker
 */
public class PerformanceWorker extends SwingWorker<ResultsModel, Integer> {

    private PerformanceResultsBuilder resultsBuilder;
    private List<PerformanceRunnerListener> listeners = new LinkedList<>();
    private ResultsModel model;
    private int progress;

    /**
     * Constructor.
     * The listener will be called when all the performance results have been computed and normalized.
     */
    PerformanceWorker(TwoPlayerController controller, SearchOptionsConfigList optionsList) {
        this.model = new ResultsModel(optionsList.size());
        resultsBuilder = new PerformanceResultsBuilder(controller, optionsList);
        this.progress = 0;
    }

    public void addListener(PerformanceRunnerListener listener) {
        listeners.add(listener);
    }

    /**
     * runs all the game strategies against each other and accumulate results
     */
    @Override
    protected ResultsModel doInBackground() throws Exception {
        int size = model.getSize();
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {

                PerformanceResultsPair results = resultsBuilder.getResultsForComparison(i, j);
                model.setResults(i, j, results);
                publish(progress++);
            }
        }
        model.normalize();
        return model;
    }

    @Override
    protected void process(List<Integer> chunks) {
        // potentially show progress
    }

    protected void done() {
        try {
            ResultsModel model = get();
            for (PerformanceRunnerListener listener : listeners)  {
                listener.performanceRunsDone(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
