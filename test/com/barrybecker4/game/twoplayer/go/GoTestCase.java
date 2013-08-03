/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;
import junit.framework.TestCase;

import java.util.Set;

/**
 * Base class for all Go test cases. Most of this code could be moved to GoBoardConfigurator
 *
 * @author Barry Becker
 */
@SuppressWarnings("HardCodedStringLiteral")
public abstract class GoTestCase extends TestCase {

    /** moved all test cases here so they are not included in the jar and do not need to be searched   */
    public static final String EXTERNAL_TEST_CASE_DIR = "/com/barrybecker4/game/twoplayer/go/cases/";

    private static final String SGF_EXTENSION = ".sgf";

    /** usually 0, but 1 or 2 may be useful when debugging. */
    private static final int DEBUG_LEVEL = 0;

    protected GoController controller_;

    /**
     * common initialization for all go test cases.
     * Override setOptionOverrides if you want different search parameters.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        GameContext.loadResources("go");
        GameContext.setDebugMode(DEBUG_LEVEL);

        controller_ = new GoController(getBoardSize(), 0);

        for (Player player : controller_.getPlayers()) {
            SearchOptions searchOptions = ((TwoPlayerPlayerOptions) player.getOptions()).getSearchOptions();
            setOptionOverrides(searchOptions);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        controller_ = null;
        super.tearDown();
    }

    /**
     * Derived classes should override if they want different options.
     * @param sOptions default options to override
     */
    protected void setOptionOverrides(SearchOptions sOptions) {
        sOptions.getBruteSearchOptions().setAlphaBeta(true);
        sOptions.getBruteSearchOptions().setLookAhead(2);
        sOptions.getBestMovesSearchOptions().setPercentageBestMoves(40);
        sOptions.getBestMovesSearchOptions().setPercentLessThanBestThresh(0);
        //sOptions.setQuiescence(true); // takes too long if on
        sOptions.setSearchStrategyMethod(SearchStrategyType.NEGAMAX_W_MEMORY);
    }

    /**
     * Override if your test suite requires a different size.
     * @return board size to use for tests.
     */
    protected int getBoardSize() {
        return 13;
    }

    protected GoBoard getBoard() {
       return (GoBoard) controller_.getBoard();
    }

    protected void restore(String problemFile) throws Exception {
        String path = EXTERNAL_TEST_CASE_DIR + problemFile + SGF_EXTENSION;
        controller_.restoreFromStream(getClass().getResourceAsStream(path));
    }

    protected GoMove getNextMove(String problemFile, boolean blackPlays) throws Exception {
        System.out.println("finding next move for " + problemFile + " ...");
        long time = System.currentTimeMillis();
        restore(problemFile);
        controller_.requestComputerMove( blackPlays, true );

        GoMove m = (GoMove) controller_.getLastMove();

        double elapsedTime = (System.currentTimeMillis() - time) / 1000.0;
        System.out.println("got " + m + " in " + FormatUtil.formatNumber(elapsedTime) + " seconds.");
        return m;
    }

    /**
     * @param isBlack true if black
     * @return the biggest black group if black is true else biggest white group.
     */
    protected IGoGroup getBiggestGroup(boolean isBlack) {

        Set<IGoGroup> groups = getBoard().getGroups();
        IGoGroup biggestGroup = null;

        for (IGoGroup group : groups) {
            GoBoardPositionSet stones = group.getStones();
            if (stones.iterator().next().getPiece().isOwnedByPlayer1() == isBlack) {
                if (biggestGroup == null || group.getNumStones() > biggestGroup.getNumStones()) {
                    biggestGroup = group;
                }
            }
        }
        return biggestGroup;
    }
}