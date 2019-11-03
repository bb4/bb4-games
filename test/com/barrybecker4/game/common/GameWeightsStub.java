/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common;

import com.barrybecker4.optimization.parameter.NumericParameterArray;
import com.barrybecker4.optimization.parameter.types.DoubleParameter;
import com.barrybecker4.optimization.parameter.types.Parameter;
import scala.jdk.javaapi.CollectionConverters;
import scala.util.Random;

import java.util.Arrays;
import java.util.List;

/**
 * Stub GameWeights for testing
 *
 * @author Barry Becker
 */
public class GameWeightsStub extends GameWeights {

    /** not really used. */
    private static final List<Parameter> PARAMS =
            Arrays.asList(new DoubleParameter(1, 0, 10, "paramName", NONE));


    public GameWeightsStub() {
        super(new NumericParameterArray(CollectionConverters.asScala(PARAMS).toIndexedSeq(), 5, new Random(1)));
    }

}
