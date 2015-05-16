/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.analysis;

import com.barrybecker4.game.common.GameWeights;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.gomoku.GoMokuBoard;
import com.barrybecker4.game.twoplayer.gomoku.GoMokuTestWeights;
import com.barrybecker4.game.twoplayer.gomoku.analysis.differencers.ValueDifferencerFactory;
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
public class MoveEvaluatorTest  {

    MoveEvaluator evaluator;
    GameWeights weights;
    GoMokuBoard board;
    TwoPlayerMove lastMove;
    LineFactoryRecorder lineFactory;

    /**
     * Common initialization for all test cases.
     */
    @Before
    public void setUp() throws Exception {

        board = new GoMokuBoard();
        weights = new GoMokuTestWeights();
        GoMokuPatterns patterns = new GoMokuPatterns();
        evaluator = new MoveEvaluator(patterns);
        lineFactory = new LineFactoryRecorder();
        ValueDifferencerFactory differencerFactory =
                new ValueDifferencerFactory(patterns, lineFactory);
        evaluator.setValueDifferencerFactory(differencerFactory);
    }

    /**
     * Verify error if piece is not where last move was played.
     */
    @Test(expected=IllegalStateException.class)
    public void testInvalidEvaluation() {

        initRow(board, 1, "___");
        lastMove = TwoPlayerMove.createMove(1, 1, 0, new GamePiece(true));
        checkResultLines(lastMove, 0, new String[] {"______", "______", "_", "______"});
    }

    @Test
    public void testX__Evaluation() {

        initRow(board, 1, "X__");
        lastMove = TwoPlayerMove.createMove(1, 1, 0, new GamePiece(true));
        checkResultLines(lastMove, 0, new String[] {"X_____", "X_____", "X", "X_____"});
    }

    @Test
    public void test___X__Evaluation() {

        initRow(board, 1, "___X__");
        lastMove = TwoPlayerMove.createMove(1, 4, 0, new GamePiece(true));
        checkResultLines(lastMove, 0, new String[] {"___X_____", "X_____", "___X", "X_____"});
    }

    /**
     * Board looks like this:
     *   _Xx____  where X is the last move played.
     *   _______
     *   _______
     *   _______
     *
     *   Lines are :
     *     horizontal: _XX____
     *     vertical :  X_____
     *     upDiag   :   _X
     *     downDiag :  X_____
     */
    @Test
    public void test_XX__Evaluation() {

        initRow(board, 1, "_XX__");
        lastMove = TwoPlayerMove.createMove(1, 2, 0, new GamePiece(true));
        checkResultLines(lastMove, 8, new String[] {"_XX____", "X_____", "_X", "X_____"});
    }

    @Test
    public void test_OO__Evaluation() {

        initRow(board, 1, "_OO__");
        lastMove = TwoPlayerMove.createMove(1, 2, 0, new GamePiece(false));
        checkResultLines(lastMove, -8, new String[] {"_OO____", "O_____", "_O", "O_____"});
    }

    /** the side of the last move played must match the piece on the board or we get an exception. */
    @Test (expected = IllegalStateException.class)
    public void test_XO__PlayerMismatch() {

        initRow(board, 1, "_XO__");
        lastMove = TwoPlayerMove.createMove(1, 2, 0, new GamePiece(false));
        checkResultLines(lastMove, 0, new String[] {"_XO____", "X_____", "_X", "X_____"});
    }

    @Test
    public void test_XO__Evaluation() {

        initRow(board, 1, "_XO__");
        lastMove = TwoPlayerMove.createMove(1, 3, 0, new GamePiece(false));
        checkResultLines(lastMove, 0, new String[] {"_XO_____", "O_____", "__O", "O_____"});
    }

    @Test
    public void test_XX__2Evaluation() {

        initRow(board, 2, "_XX__");  // on second row
        lastMove = TwoPlayerMove.createMove(2, 2, 0, new GamePiece(true));
        checkResultLines(lastMove, 8, new String[] {"_XX____", "_X_____", "_X_", "_X_____"});
    }

    @Test
    public void test_XX_X_X_Row1Evaluation() {

        initRow(board, 1, "_XX_X__X_");
        lastMove = TwoPlayerMove.createMove(1, 2, 0, new GamePiece(true));
        checkResultLines(lastMove, 24, new String[] {"_XX_X__", "X_____", "_X", "X_____"});

        lastMove = TwoPlayerMove.createMove(1, 3, 0, new GamePiece(true));
        checkResultLines(lastMove, 32, new String[] {"_XX_X__X", "X_____", "__X", "X_____"});

        lastMove = TwoPlayerMove.createMove(1, 5, 0, new GamePiece(true));
        checkResultLines(lastMove, 32, new String[] {"_XX_X__X__", "X_____", "____X", "X_____"});

        lastMove = TwoPlayerMove.createMove(1, 8, 0, new GamePiece(true));
        checkResultLines(lastMove, 0, new String[] {"X_X__X_____", "X_____", "_____X", "X_____"});
    }

    @Test
    public void test_XX_X_X_Row5Evaluation() {

        initRow(board, 5, "_XX_X__X_");
        lastMove = TwoPlayerMove.createMove(5, 2, 0, new GamePiece(true));
        checkResultLines(lastMove, 24, new String[] {"_XX_X__", "____X_____", "_X____", "_X_____"});

        lastMove = TwoPlayerMove.createMove(5, 3, 0, new GamePiece(true));
        checkResultLines(lastMove, 32, new String[] {"_XX_X__X", "____X_____", "__X____", "__X_____"});

        lastMove = TwoPlayerMove.createMove(5, 5, 0, new GamePiece(true));
        checkResultLines(lastMove, 32, new String[] {"_XX_X__X__", "____X_____", "____X____", "____X_____"});

        lastMove = TwoPlayerMove.createMove(5, 8, 0, new GamePiece(true));
        checkResultLines(lastMove, 0, new String[] {"X_X__X_____", "____X_____", "_____X____", "____X_____"});
    }

    @Test
    public void test_XO_X_X_Row1Evaluation() {

        initRow(board, 1, "_XO_X__X_");

        lastMove = TwoPlayerMove.createMove(1, 2, 0, new GamePiece(true));
        checkResultLines(lastMove, 0, new String[] {"_XO_X__", "X_____", "_X", "X_____"});

        // we check that the moved game piece matches what is on the board.
        lastMove = TwoPlayerMove.createMove(1, 3, 0, new GamePiece(false));
        checkResultLines(lastMove, 0, new String[] {"_XO_X__X", "O_____", "__O", "O_____"});

        lastMove = TwoPlayerMove.createMove(1, 5, 0, new GamePiece(true));
        checkResultLines(lastMove, 0, new String[] {"_XO_X__X__", "X_____", "____X", "X_____"});

        lastMove = TwoPlayerMove.createMove(1, 8, 0, new GamePiece(true));
        checkResultLines(lastMove, 0, new String[] {"O_X__X_____", "X_____", "_____X", "X_____"});
    }

    @Test
    public void test3RowEvaluation() {

        initRow(board, 2, "_OX_X__X_");
        initRow(board, 3, "_XX_X__X_");
        initRow(board, 4, "_XX_X__X_");
        TwoPlayerMove lastMove = TwoPlayerMove.createMove(2, 2, 0, new GamePiece(false));
        checkResultLines(lastMove, -14, new String[] {"_OX_X__", "_OXX___", "_O_", "_OX____"});

        lastMove = TwoPlayerMove.createMove(3, 3, 0, new GamePiece(true));
        checkResultLines(lastMove, 64, new String[] {"_XX_X__X", "_XXX____", "_XX__", "_OX_____"});

        lastMove = TwoPlayerMove.createMove(3, 8, 0, new GamePiece(true));
        checkResultLines(lastMove, 24, new String[] {"X_X__X_____", "_XXX____", "_____X__", "__X_____"});
    }


    /**
     * The 4 expected lines are - | / \
     */
    private void checkResultLines(TwoPlayerMove lastMove, int expectedWorth, String[] expectedLines) {
        lineFactory.clearLines();
        int worth = evaluator.worth(board, lastMove, weights.getDefaultWeights());
        List<Line> lines = lineFactory.getCreatedLines();
        TstUtil.printLines(lines);

        assertEquals("Unexpected worth for move " + lastMove,
                expectedWorth, worth);
        assertEquals(expectedLines.length, lines.size());
        int i = 0;
        for (String expPattern : expectedLines) {
             assertEquals(expPattern, lines.get(i++).toString());
        }
    }

    private void initRow(TwoPlayerBoard board, int row, String pattern) {
        assert row > 0;
        for (int i = 0; i < pattern.length(); i++)  {
            char c = pattern.charAt(i);

            if (c == 'X' || c == 'O')  {
                boolean player1 = c == 'X';
                TwoPlayerMove move = TwoPlayerMove.createMove(row, i+1, 0, new GamePiece(player1));
                board.makeMove(move);
            }
        }
    }
}
