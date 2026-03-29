/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common;

import com.barrybecker4.math.MathUtil;
import com.barrybecker4.game.twoplayer.common.ui.OptimizationDoneHandler;
import com.barrybecker4.optimization.Optimizer;
import com.barrybecker4.optimization.parameter.ParameterArray;
import com.barrybecker4.optimization.parameter.ParameterArrayWithFitness;
import com.barrybecker4.optimization.strategy.OptimizationStrategyType;

import javax.swing.SwingWorker;

import com.barrybecker4.game.common.GameContext;

import static com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

/**
 * This worker runs in a separate thread to figure out optimal game weights give the UI a chance to update.
 * The computer optimizes by playing against itself for a long time.
 *
 * @author Barry Becker
 */
public class TwoPlayerOptimizationWorker extends SwingWorker<ParameterArrayWithFitness, Integer> {

    /** Scala enum case name; use {@link OptimizationStrategyType#valueOf(String)} for Java/IDE-friendly lookup. */
    private static final OptimizationStrategyType DEFAULT_STRATEGY =
            OptimizationStrategyType.valueOf("HILL_CLIMBING");

    private Optimizer optimizer;
    private ParameterArray initialWeights;
    private OptimizationDoneHandler handler;

    /**
     * Constructor
     * @param optimizer the thing to do the optimizing
     * @param initialWeights initial game weights to optimize
     * @param handler will be called when optimization is done.
     */
    TwoPlayerOptimizationWorker(final Optimizer optimizer,
                 final ParameterArray initialWeights, final OptimizationDoneHandler handler) {
        this.optimizer = optimizer;
        this.initialWeights = initialWeights;
        this.handler = handler;
    }


    @Override
    protected ParameterArrayWithFitness doInBackground() {
        return optimizer.doOptimization(
                DEFAULT_STRATEGY,
                initialWeights,
                WINNING_VALUE,
                MathUtil.RANDOM());
    }

    @Override
    protected void done() {
        try {
            handler.done(get());
        } catch (Exception e) {
            GameContext.log(0, "Optimization failed: " + e);
            handler.onFailure(e);
        }
    }
}
