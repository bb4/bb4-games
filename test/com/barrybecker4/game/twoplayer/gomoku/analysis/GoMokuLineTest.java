/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.analysis;

import com.barrybecker4.game.common.GameWeights;
import com.barrybecker4.game.twoplayer.gomoku.GoMokuTestWeights;
import com.barrybecker4.game.twoplayer.gomoku.pattern.GoMokuPatterns;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
public class GoMokuLineTest {

    GameWeights weights;
    private static final boolean PLAYER1_PERSP = true;
    private static final boolean PLAYER2_PERSP = false;

    @Before
    public void setUp() throws Exception {
        weights = new GoMokuTestWeights();
    }

    /** from the perspective of X */
    @Test
    public void testEvalLinePlayer1Short_p1Persp() {
        checkLine("_X_XO", 1, PLAYER1_PERSP, 2, new String[] {"_X_X"});
        checkLine("_X_XO", 2, PLAYER1_PERSP, 2, new String[] {"_X_X"});
        checkLine("_X_XO", 3, PLAYER1_PERSP, 2, new String[] {"_X_X"});
        checkLine("_X_XO", 4, PLAYER1_PERSP, 0, new String[] {""});
        checkLine("_XX", 1, PLAYER1_PERSP, 0, new String[] {"_XX"});
        checkLine("_XX_", 2, PLAYER1_PERSP, 8, new String[] {"_XX_"});
        checkLine("_XX_", 3, PLAYER1_PERSP, 8, new String[] {"_XX_"});
    }

    /** from the perspective of O */
    @Test
    public void testEvalLinePlayer1Short_p2Persp() {
        checkLine("_X_XO", 3, PLAYER2_PERSP, 0, new String[] {"_"});
        checkLine("_X_XO", 4, PLAYER2_PERSP, 0, new String[] {"O"});
        checkLine("_XX_", 2, PLAYER2_PERSP, 0, new String[] {});
        checkLine("_XX_", 3, PLAYER2_PERSP, 0, new String[] {"_"});
    }

    @Test
    public void testEvalLinePlayer1Long() {
        checkLine("_XX_XX__",  1, PLAYER1_PERSP, 320, new String[] {"_XX_XX_"});
        checkLine("_XX_XX__",  3, PLAYER1_PERSP, 320, new String[] {"_XX_XX_"});
        checkLine("_XX_XX__",  3, PLAYER2_PERSP, 0,   new String[] {"_"});
        checkLine("_XX_XX__",  4, PLAYER1_PERSP, 320, new String[] {"_XX_XX_"});
        checkLine("_XX_XX__",  6, PLAYER1_PERSP, 320, new String[] {"_XX_XX_"});
        checkLine("_XX_XX__",  1, PLAYER1_PERSP, 320, new String[] {"_XX_XX_"});
        checkLine("_XX_XOX_X__", 1, PLAYER1_PERSP, 8, new String[] {"_XX_X"});
        checkLine("_XX_XOX_X__", 1, PLAYER2_PERSP, 0,  new String[] {""});
        checkLine("_XX_XOX_X__", 5, PLAYER1_PERSP, 10, new String[] {"_XX_X", "X_X_"});
        checkLine("_XX_XOX_X__", 5, PLAYER2_PERSP, 0,  new String[] {"O"});
        checkLine("_OO_OXO_O__", 5, PLAYER1_PERSP, 0,  new String[] {"X"});
        checkLine("_OO_OXO_O__", 5, PLAYER2_PERSP, -10, new String[] {"_OO_O", "O_O_"});
    }

    private void checkLine(String linePattern, int position, boolean player1persp, int expectedWorth,
                           String[] expectedPatternsChecked) {
        StubLineEvaluator evaluator =
                new StubLineEvaluator(new GoMokuPatterns(), weights.getDefaultWeights());
        StringBuilder line = new StringBuilder(linePattern);
        int worth = evaluator.evaluate(line, player1persp, position, 0, linePattern.length() - 1);
        //System.out.println("p1Persp=" + player1persp + " " + line +" pos="+ position);

        assertEquals("unexpected score for pattern "+ linePattern + " pos=" + position + " player1Persp="+ player1persp,
                expectedWorth, worth);

        List<CharSequence> checkedPats = evaluator.getPatternsChecked();
        //System.out.println("pats="+ TstUtil.quoteStringList(checkedPats));
        assertEquals("Unexpected number of patterns checked",
                expectedPatternsChecked.length, checkedPats.size());
        int i = 0;
        for (CharSequence p : checkedPats) {
            assertEquals(expectedPatternsChecked[i++], p);
        }
    }

    /**
     *
     * @param linePattern  some sequence of X, O, _
     * @return the line
     */
    private Line createLine(String linePattern, LineEvaluator evaluator) {
        return TstUtil.createLine(linePattern, evaluator);
    }
}
