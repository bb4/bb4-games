/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;

import java.util.Arrays;

/**
 * @author Barry Becker
 */
public class TestLifeAndDeath extends GoTestCase {

    private static final String PREFIX1 = "lifeanddeath/";

    private static final String PREFIX2 = "problems/sgf/life_death/";

    private static final boolean BLACK_TO_PLAY = true;
    private static final boolean WHITE_TO_PLAY = false;

    /**
     * @param options default options to override
     */
    @Override
    protected void setOptionOverrides(SearchOptions options) {
        options.getBruteSearchOptions().setAlphaBeta(true);
        options.getBruteSearchOptions().setLookAhead(1);    // should be at least 3.
        options.getBestMovesSearchOptions().setPercentageBestMoves(60);
        options.getBruteSearchOptions().setQuiescence(false);  // should be true
        options.setSearchStrategyMethod(SearchStrategyType.MINIMAX);
    }

    public void testPass() { assertTrue(true); }

    /**
     * took 77 seconds with look-ahead = 3, bestMoves= 60% and quiescence.
     *
    public void testProblem57() {
         doLifeAndDeathTest("problem_life57", 6, 1);  // 6, 1 is the correct move  (common mistake is  5, 1)
    } */

    /**
     * originally took 250 seconds
     * - reduced calls to GoGroup.getStones()  to improve to 214 seconds.  14.4% improvement
     * - avoided boundary check in getBoardPostion by accessing positions_ array directly 197 seconds or   8%
     * - don't calculate the liberties every time in GoString.getLiberties(),
     *     but instead update them incrementally. 75 seconds or 62%
     * - Don't play the move to determine if a suicide and then undo it. Instead infer suicide from looking at the nobi nbrs.
     *     75 -> 74
     *
     * overall= 250 -> 74    factor of 3 speedup!
     *
    public void testProblem58() {
         doLifeAndDeathTest("problem_life58", 1, 12);   // 1, 12 is the correct move
        //  common mistakes : 4, 6; 5, 13
    } */

    /*
    public void testProblem59() {
        doLifeAndDeathTest("problem_life59", 12, 1);  // 12, 1 is the correct move
        // common mistakes 13, 5  6, 3;  10, 5 is ok for now.
    } */

    // ----------------------------------------

    /*
    public void testProblem3() {
        Location[] acceptableMoves = {new ByteLocation(5, 18), new ByteLocation(11, 18)};
        doLifeAndDeathTest2("life_death.3", acceptableMoves, WHITE_TO_PLAY);  // [E18|K18]
    } */

    /** takes too long
    public void testProblem4() {
        Location[] acceptableMoves = {new ByteLocation(11, 18)};
        doLifeAndDeathTest2("life_death.4", acceptableMoves, BLACK_TO_PLAY); // [K18]
    }  */

    /**
     * @param filename name of the file where test case resides
     * @param row row of expected next move.
     * @param column  column of expected next move.
     */
    private void doLifeAndDeathTest(String filename, int row, int column) throws Exception {
        GoMove m = getNextMove(PREFIX1 + filename, true);
        verifyExpected(m, row, column);
    }

    /**
     *
     * @param filename
     */
    private void doLifeAndDeathTest2(
            String filename, Location[] acceptableMoves, boolean blackToPlay) throws Exception {
        GoMove move = getNextMove(PREFIX2 + filename, blackToPlay);
        verifyAcceptable(move, acceptableMoves);

    }

    private void verifyAcceptable(GoMove move, Location[] acceptableMoves)  {
        boolean pass = false;
        // if the result matches any of the acceptable moves, then pass.
        for (Location loc : acceptableMoves) {
            if (isExpected(move, loc)) {
                pass = true;
            }
        }
        assertTrue("The computed move (" + move +") was not one that we though acceptable ="+
                Arrays.toString(acceptableMoves), pass);
    }

    private static void verifyExpected(GoMove m, int row, int col) {

        assertTrue("Was expecting " + row + ", " + col + ", but instead got " + m,
                isExpected(m, row, col));
    }

    private static boolean isExpected(GoMove m, Location loc) {

        return isExpected(m, m.getToRow(), loc.getCol());
    }

    private static boolean isExpected(GoMove m, int row, int col) {

        return m.getToRow() == row && m.getToCol() == col;
    }

}