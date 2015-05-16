// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis.differencers;

import com.barrybecker4.game.twoplayer.gomoku.analysis.Line;
import com.barrybecker4.game.twoplayer.gomoku.analysis.LineEvaluator;
import com.barrybecker4.game.twoplayer.gomoku.analysis.LineFactory;
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Creates lines and remembers what lines were created.
 * @author Barry Becker
 */
public class StubLineFactory extends LineFactory {

    private StubLine lastLine;
    public StubLineFactory() {
    }

    @Override
    public Line createLine(Patterns patterns, ParameterArray weights) {
        lastLine = new StubLine(new LineEvaluator(patterns, weights));
        return lastLine;
    }

    /** return the last line that was created by the factory */
    public String getLineContent() {
        return lastLine.getLineContent();
    }
}