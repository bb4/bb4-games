/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.analysis;

import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Creates lines. Easy to override and create mock in tests.
 * @author Barry Becker
 */
public class LineFactory {

    public LineFactory() {}

    public Line createLine(Patterns patterns, ParameterArray weights) {
        return new Line(new LineEvaluator(patterns, weights));
    }
}
