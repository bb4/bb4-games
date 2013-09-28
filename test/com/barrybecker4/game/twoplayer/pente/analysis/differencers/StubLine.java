// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente.analysis.differencers;

import com.barrybecker4.game.twoplayer.pente.analysis.Line;
import com.barrybecker4.game.twoplayer.pente.analysis.LineEvaluator;

import java.util.List;

/**
 * Represents a run of symbols to be evaluated on the board.
 * @author Barry Becker
 */
public class StubLine extends Line {

    /** recorded set of patterns that we got weights for */
    List<String> patternsChecked_;

    /**
     * Constructor
     */
    public StubLine(LineEvaluator evaluator) {
        super(evaluator);
    }


    @Override
    public int computeValueDifference(int position) {
        return 0;
    }

    public String getLineContent() {
        return line.toString();
    }
}
