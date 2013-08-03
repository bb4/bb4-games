// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente.analysis;

import com.barrybecker4.game.common.GameWeights;
import com.barrybecker4.game.twoplayer.pente.pattern.SimplePatterns;
import com.barrybecker4.game.twoplayer.pente.pattern.SimpleWeights;
import junit.framework.TestCase;

/**
 * Verify that we correctly evaluate patterns in lines on the board.
 *
 * @author Barry Becker
 */
public class LineEvaluatorTest extends TestCase  {

    private StringBuilder line;
    private LineEvaluator lineEvaluator;
    private double worth;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        lineEvaluator = createLineEvaluator();
    }

    public void testEvalLinePlayer1_X() {

        line = createLine("_X");
        double worth = lineEvaluator.evaluate(line, true, 1, 0, 1);
        assertEquals(1.0, worth);

        line = createLine("_X");
        worth = lineEvaluator.evaluate(line, true, 0, 0, 1);
        assertEquals(1.0, worth);
    }

    public void testEvalLinePlayer1_X_simp() {

        line = createLine("_X_");
        worth = lineEvaluator.evaluate(line, true, 1, 0, 2);
        assertEquals(3.0, worth);
    }

    public void testEvalLinePlayer1_X_() {

        line = createLine("_X_");
        worth = lineEvaluator.evaluate(line, true, 1, 0, 2);
        assertEquals(3.0, worth);

        line = createLine("_X_");
        worth = lineEvaluator.evaluate(line, true, 0, 0, 2);
        assertEquals(3.0, worth);

        // should be same if in longer line
        line = createLine("OO_X__");
        worth = lineEvaluator.evaluate(line, true, 3, 2, 4);
        assertEquals(3.0, worth);

        line = createLine("O_X_O_O");
        worth = lineEvaluator.evaluate(line, true, 2, 1, 3);
        assertEquals(3.0, worth);
    }

    public void testEvalLinePlayer1X_() {

        line = createLine("X_");
        worth = lineEvaluator.evaluate(line, false, 0, 0, 1);
        assertEquals(0.0, worth);

        line = createLine("X_");
        worth = lineEvaluator.evaluate(line, true, 0, 0, 1);
        assertEquals(1.0, worth);

        line = createLine("X_");
        worth = lineEvaluator.evaluate(line, true, 1, 0, 1);
        assertEquals(1.0, worth);
    }

    public void testEvalLinePlayer1XX() {

        line = createLine("XX");
        worth = lineEvaluator.evaluate(line, true, 0, 0, 1);
        assertEquals(10.0, worth);

        line = createLine("XX");
        worth = lineEvaluator.evaluate(line, true, 1, 0, 1);
        assertEquals(10.0, worth);
    }

    public void testEvaLineSimplePlayer2() {

        line = createLine("_O");
        worth = lineEvaluator.evaluate(line, false, 1, 0, 1);
        assertEquals(-1.0, worth);

        line = createLine("_O");
        worth = lineEvaluator.evaluate(line, false, 0, 0, 1);
        assertEquals(-1.0, worth);

        line = createLine("O_");
        worth = lineEvaluator.evaluate(line, true, 0, 0, 1);
        assertEquals(0.0, worth);

        line = createLine("O_");
        worth = lineEvaluator.evaluate(line, true, 1, 0, 1);
        assertEquals(0.0, worth);

        line = createLine("O_");
        worth = lineEvaluator.evaluate(line, false, 0, 0, 1);
        assertEquals(-1.0, worth);

        line = createLine("OO");
        worth = lineEvaluator.evaluate(line, false, 0, 0, 1);
        assertEquals(-10.0, worth);

        line = createLine("OO");
        worth = lineEvaluator.evaluate(line, false, 1, 0, 1);
        assertEquals(-10.0, worth);
    }

    /** XX is a winning pattern in this test example */
    public void testEvalLineLongerThanPattern() {

        line = createLine("X_XX");

        // the pattern is XX
        worth = lineEvaluator.evaluate(line, true, 2, 2, 3);
        assertEquals(10.0, worth);

        // the pattern is X with is not a pattern in SimplePatterns
        worth = lineEvaluator.evaluate(line, false, 2, 2, 3);
        assertEquals(0.0, worth);

        worth = lineEvaluator.evaluate(line, true, 3, 2, 3);
        assertEquals(10.0, worth);

        worth = lineEvaluator.evaluate(line, true, 1, 1, 2);
        assertEquals(1.0, worth);

        worth = lineEvaluator.evaluate(line, true, 1, 0, 3);
        assertEquals(0.0, worth);

        // X_XX is not a recognizable pattern
        worth = lineEvaluator.evaluate(line, true, 2, 0, 3);
        assertEquals(0.0, worth);

        // X_XX is not a recognizable StubPattern
        worth = lineEvaluator.evaluate(line, true, 3, 0, 3);
        assertEquals(0.0, worth);
    }

    public void testEvalMixedLineXOXwithRecordedPatterns() {

        StubLineEvaluator evaluator = createStubLineEvaluator();

        line = createLine("XOX");
        worth = evaluator.evaluate(line, true, 2, 0, 2);
        TstUtil.checkRecordedPatterns(new String[] {"X"}, evaluator);
        assertEquals(0.0, worth);

        line = createLine("XOX");
        worth = evaluator.evaluate(line, false, 1, 0, 2);
        TstUtil.checkRecordedPatterns(new String[] {"O"}, evaluator);
        assertEquals(0.0, worth);

        line = createLine("XOX");
        worth = evaluator.evaluate(line, true, 1, 0, 2);
        TstUtil.checkRecordedPatterns(new String[] {}, evaluator);
        assertEquals(0.0, worth);
    }

    public void testEvalMixedLine_XOX_withRecordedPatterns() {

        StubLineEvaluator evaluator = createStubLineEvaluator();

        line = createLine("_XOX_");
        worth = evaluator.evaluate(line, true, 3, 0, 4);
        TstUtil.checkRecordedPatterns(new String[] {"X_"}, evaluator);
        assertEquals(1.0, worth);

        line = createLine("_XOX_");
        worth = evaluator.evaluate(line, true, 1, 0, 4);
        TstUtil.checkRecordedPatterns(new String[] {"_X"}, evaluator);
        assertEquals(1.0, worth);

        line = createLine("_XOX_");
        worth = evaluator.evaluate(line, false, 2, 0, 4);
        TstUtil.checkRecordedPatterns(new String[] {"O"}, evaluator);
        assertEquals(0.0, worth);

        line = createLine("_XOX_");
        worth = evaluator.evaluate(line, true, 2, 0, 4);
        TstUtil.checkRecordedPatterns(new String[] {"_X", "X_"}, evaluator);
        assertEquals(2.0, worth);
    }


    public void testEvalMixedLineX_OX() {

        line = createLine("X_OX");
        worth = lineEvaluator.evaluate(line, true, 2, 0, 3);
        // X_ gets 1.
        assertEquals(1.0, worth);

        line = createLine("X_OX");
        worth = lineEvaluator.evaluate(line, false, 2, 0, 3);
        assertEquals(-1.0, worth);
    }

    public void testEvalMixedLineX_O_X() {

        line = createLine("X_O_X");
        worth = lineEvaluator.evaluate(line, true, 2, 0, 4);
        // X_ and _X get 2.
        assertEquals(2.0, worth);

        worth = lineEvaluator.evaluate(line, false, 2, 0, 4);
        // X_ and _X get 4.
        assertEquals(-3.0, worth);
    }

    public void testEvalMixedLineXXOXXwithRecordedPatterns() {
        StubLineEvaluator evaluator = createStubLineEvaluator();

        line = createLine("XXOXX");
        worth = evaluator.evaluate(line, true, 2, 0, 4);
        TstUtil.checkRecordedPatterns(new String[] {"XX", "XX"}, evaluator);
        assertEquals(20.0, worth);

        worth = evaluator.evaluate(line, true, 1, 0, 4);
        TstUtil.checkRecordedPatterns(new String[] {"XX"}, evaluator);
        assertEquals(10.0, worth);

        worth = evaluator.evaluate(line, false, 1, 0, 4);
        TstUtil.checkRecordedPatterns(new String[] {"O"}, evaluator);
        assertEquals(0.0, worth);

        worth = evaluator.evaluate(line, false, 2, 0, 4);
        TstUtil.checkRecordedPatterns(new String[] {"O"}, evaluator);
        assertEquals(0.0, worth);
    }

    public void testEvalMixedLine_XO_X() {

        line = createLine("_XO_X");

        worth = lineEvaluator.evaluate(line, true, 0, 0, 4);
        assertEquals(1.0, worth);

        worth = lineEvaluator.evaluate(line, false, 0, 0, 4);
        assertEquals(0.0, worth);

        worth = lineEvaluator.evaluate(line, true, 1, 0, 4);
        assertEquals(1.0, worth);

        worth = lineEvaluator.evaluate(line, true, 2, 0, 4);
        assertEquals(2.0, worth);

        worth = lineEvaluator.evaluate(line, false, 2, 0, 4);
        assertEquals(-1.0, worth);

        worth = lineEvaluator.evaluate(line, true, 1, 0, 4);
        assertEquals(1.0, worth);

        worth = lineEvaluator.evaluate(line, false, 2, 0, 4);
        assertEquals(-1.0, worth);

        worth = lineEvaluator.evaluate(line, true, 3, 0, 4);
        assertEquals(1.0, worth);

        worth = lineEvaluator.evaluate(line, false, 3, 0, 4);
        assertEquals(-1.0, worth);
    }


    /**
     * @return the line
     */
    private StringBuilder createLine(String line) {
        return new StringBuilder(line);
    }

    private LineEvaluator createLineEvaluator()  {
        GameWeights weights = new SimpleWeights();
        return new LineEvaluator(new SimplePatterns(), weights.getDefaultWeights());
    }

    private StubLineEvaluator createStubLineEvaluator()  {
        GameWeights weights = new SimpleWeights();
        return new StubLineEvaluator(new SimplePatterns(), weights.getDefaultWeights());
    }
}