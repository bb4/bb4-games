package com.barrybecker4.game.twoplayer.common.ui;

import com.barrybecker4.optimization.parameter.ParameterArrayWithFitness;

/**
 * Called when the optimization is done running.
 *
 * @author Barry Becker
 */
public interface OptimizationDoneHandler {

    void done(ParameterArrayWithFitness parameters);
}
