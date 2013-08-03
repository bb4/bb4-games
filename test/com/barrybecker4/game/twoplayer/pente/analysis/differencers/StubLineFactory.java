// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente.analysis.differencers;

import com.barrybecker4.game.twoplayer.pente.analysis.Line;
import com.barrybecker4.game.twoplayer.pente.analysis.LineEvaluator;
import com.barrybecker4.game.twoplayer.pente.analysis.LineFactory;
import com.barrybecker4.game.twoplayer.pente.pattern.Patterns;
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