/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.pente.analysis;

import com.barrybecker4.game.common.GameWeights;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.pente.pattern.SimplePatterns;
import com.barrybecker4.game.twoplayer.pente.pattern.SimpleWeights;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Verify that we correctly evaluate lines on the board.
 *
 * @author Barry Becker
 */
public class LineTest {

    private GameWeights weights = new SimpleWeights();

    @Test
    public void testAppendEmpty() {

        Line line = new Line(createLineEvaluator());
        BoardPosition pos = new BoardPosition(2, 2, null);
        line.append(pos);
        assertEquals("_", line.toString());
    }

    @Test
    public void testAppendPlayer() {

        Line line = new Line(createLineEvaluator());
        BoardPosition pos = new BoardPosition(2, 2, new GamePiece(true));
        line.append(pos);
        assertEquals("X", line.toString());

        pos = new BoardPosition(4, 3, new GamePiece(false));
        line.append(pos);
        assertEquals("XO", line.toString());
    }

    @Test
    public void testComputeValueDifferenceXX_Integraction() {

        Line line = createLine("XX");

        int diff = line.computeValueDifference(0);
        assertEquals(9, diff);

        diff = line.computeValueDifference(1);
        assertEquals(9, diff);
    }

    @Test
    public void testComputeValueDifferenceOO_Integraction() {

        Line line = createLine("OO");

        int diff = line.computeValueDifference(0);
        assertEquals(-9, diff);

        diff = line.computeValueDifference(1);
        assertEquals(-9, diff);
    }

    @Test
    public void testComputeValueDifference_X_Integraction() {

        Line line = createLine("_X");
        assertEquals("_X", line.toString());

        int diff = line.computeValueDifference(1);
        assertEquals(1, diff);
    }

    @Test
    public void testComputeValueDifferenceXX_TooShort() {

        Line line = createLineWithMock("XX");

        int diff = line.computeValueDifference(0);
        assertEquals(0, diff);

        diff = line.computeValueDifference(1);
        assertEquals(0, diff);
    }

    @Test
    public void testComputeValueDifference_XXX() {

        Line line = createLineWithMock("_XXX");

        int diff = line.computeValueDifference(2);
        assertEquals(2, diff);

        diff = line.computeValueDifference(3);
        assertEquals(2, diff);
    }

    /** The mock evaluator returns 1 whenever the position matches the player symbol (or -1 if opponent) */
    @Test
    public void testComputeValueDifference_XOX() {

        Line line = createLineWithMock("_XOX");

        int diff = line.computeValueDifference(2);
        assertEquals(-2, diff);

        diff = line.computeValueDifference(3);
        assertEquals(2, diff);
    }

    @Test
    public void testComputeValueDifference_XOXrecorded() {

        Line line = createLineWithRecorder("_XOX");

        int diff = line.computeValueDifference(2);
        assertEquals(1, diff);
        TstUtil.checkRecordedPatterns(new String[] {"_", "_X_X", "O", "_X"}, line);

        diff = line.computeValueDifference(3);
        assertEquals(1, diff);
        TstUtil.checkRecordedPatterns(new String[] {"_", "O_", "X", ""}, line);
    }

    /** shows a more typical pattern */
    @Test
    public void testComputeValueDifferenceXX_XO_X_X_recorded() {

        Line line = createLineWithRecorder("XX_XO_X_X_");

        // The first two patterns are when we set the position to unoccupied, then check both points of view
        // The second two are when we set the players piece again and check both points of view.
        int diff = line.computeValueDifference(0);
        assertEquals(0, diff);
        TstUtil.checkRecordedPatterns(new String[] {"_X_X", "_", "XX_X", ""}, line);

        diff = line.computeValueDifference(1);
        assertEquals(-1, diff);
        TstUtil.checkRecordedPatterns(new String[] {"X_", "_", "XX_X", "_"}, line);

        diff = line.computeValueDifference(3);
        assertEquals(2, diff);
        TstUtil.checkRecordedPatterns(new String[] {"_", "_O_", "XX_X", "_", "O_"}, line);

        diff = line.computeValueDifference(4);
        assertEquals(-1, diff);
        TstUtil.checkRecordedPatterns(new String[] {"_", "XX_X_", "O_", "XX_X", "_X_X_"}, line);

        diff = line.computeValueDifference(6);
        assertEquals(-1, diff);
        TstUtil.checkRecordedPatterns(new String[] {"_", "_", "_X_X_", "O_", "_"}, line);

        diff = line.computeValueDifference(8);
        assertEquals(0, diff);
        TstUtil.checkRecordedPatterns(new String[] {"_", "_", "_X_X_", "_"}, line);
    }

    /** Cannot check where no symbol was played */
    @Test (expected = IllegalStateException.class)
    public void testComputeValueDifferenceXX_XO_X_X_emptyPosition() {

        Line line = createLineWithRecorder("XX_XO_X_X_");
        line.computeValueDifference(2);
    }

    /**
     * @param linePattern  some sequence of X, O, _
     * @return the line
     */
    private Line createLine(String linePattern) {
        return TstUtil.createLine(linePattern, createLineEvaluator());
    }

    /**
     * @param linePattern  some sequence of X, O, _
     * @return the line
     */
    private Line createLineWithMock(String linePattern) {
        return TstUtil.createLine(linePattern, new MockLineEvaluator(3));
    }

    /**
     * @param linePattern  some sequence of X, O, _
     * @return the line
     */
    private Line createLineWithRecorder(String linePattern) {
        StubLineEvaluator evaluator =
                new StubLineEvaluator(new SimplePatterns(), weights.getDefaultWeights());
        return TstUtil.createLine(linePattern, evaluator);
    }

    private LineEvaluator createLineEvaluator() {
        return new LineEvaluator(new SimplePatterns(), weights.getDefaultWeights());
    }
}