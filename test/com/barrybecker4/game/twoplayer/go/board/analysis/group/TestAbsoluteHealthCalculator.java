// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.analysis.group;

import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;


/**
* Mostly test that the scoring of groups works correctly.
* @author Barry Becker
*/
public class TestAbsoluteHealthCalculator extends GoTestCase {

    private static final String PREFIX = "board/analysis/grouphealth/";

    private AbsoluteHealthCalculator absHealthCalculator;

    @Override
    public void setUp() {
        IGoGroup group = new StubGoGroup(0.0f, true, 10);
                GroupAnalyzerMap analyzerMap = new GroupAnalyzerMap();

                absHealthCalculator = new AbsoluteHealthCalculator(group, analyzerMap);
    }

    public void testDefaultEyePotential() {
        assertEquals("Unexpected eye potential",
                0.0f, absHealthCalculator.getEyePotential());
    }

    public void testIsValid() {
            assertFalse("Unexpected isValidValue", absHealthCalculator.isValid());
    }

}
