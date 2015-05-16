/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.pattern;

import com.barrybecker4.game.common.GameWeights;
import com.barrybecker4.optimization.parameter.NumericParameterArray;
import com.barrybecker4.optimization.parameter.types.DoubleParameter;
import com.barrybecker4.optimization.parameter.types.Parameter;

/**
 * Simple weights for unit testing.
 *
 * @author Barry Becker
 */
public class SimpleWeights extends GameWeights {

    private static final Parameter[] PARAMS = new Parameter[] {
                    new DoubleParameter(1.0, 0.0, 5.0, "weight 1"),
                    new DoubleParameter(3.0, 1.0, 100.0, "weight 2"),
                    new DoubleParameter(10.0, 5.0, 1000.0, "won weight 3"),
                };

    public SimpleWeights() {

        super(new NumericParameterArray(PARAMS));
    }

}