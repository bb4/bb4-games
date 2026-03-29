package com.barrybecker4.game.twoplayer.common.ui;

import com.barrybecker4.optimization.parameter.ParameterArrayWithFitness;

/**
 * Called when the optimization is done running.
 *
 * @author Barry Becker
 */
public interface OptimizationDoneHandler {

    void done(ParameterArrayWithFitness parameters);

    /**
     * Called when optimization fails or the worker cannot deliver a result.
     * Default: no-op; override to show an error in the UI.
     */
    default void onFailure(Throwable error) {
    }
}
