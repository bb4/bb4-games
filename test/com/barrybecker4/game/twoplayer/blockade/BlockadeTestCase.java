/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.barrybecker4.ui.file.GenericFileFilter;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 *Base class for all Blockade test cases.
 * @author Barry Becker
 */
public abstract class BlockadeTestCase extends TestCase {

    /** moved all test cases here so they are not included in the jar and do not need to be searched */
    private static final String EXTERNAL_TEST_CASE_DIR = "/com/barrybecker4/game/twoplayer/blockade/cases/"; // NON-NLS

    private static final String SGF_EXTENSION = ".sgf";  // NON-NLS

    protected BlockadeController controller_;



    /**
     * common initialization for all test cases.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        GameContext.loadResources("blockade"); // NON-NLS
        GameContext.setDebugMode(0);

        controller_ = new BlockadeController();

        for (Player player : controller_.getPlayers())  {
            SearchOptions sOptions = ((TwoPlayerPlayerOptions)player.getOptions()).getSearchOptions();
            sOptions.getBruteSearchOptions().setAlphaBeta(true);
            sOptions.getBruteSearchOptions().setLookAhead(3);
            sOptions.getBestMovesSearchOptions().setPercentageBestMoves(100);
            //sOptions.setQuiescence(true); // takes too long if on
            sOptions.setSearchStrategyMethod(SearchStrategyType.MINIMAX);
        }
    }

    protected void restore(String problemFile) throws Exception {
        controller_.restoreFromStream(
                getClass().getResourceAsStream(EXTERNAL_TEST_CASE_DIR + problemFile + SGF_EXTENSION));
    }

    /**
     * @param directory to search for the files in.
     * @param pattern desired.
     * @return all the files matching the supplied pattern in the specified directory
     */
    protected static String[] getFilesMatching(String directory, String pattern) {

        return GenericFileFilter.getFilesMatching(EXTERNAL_TEST_CASE_DIR + directory, pattern);
    }

    protected Move getNextMove(String problemFile, boolean firstPlayerPlays) throws Exception {


        GameContext.log(1, "finding next move for "+problemFile+" ...");
        long time = System.currentTimeMillis();
        restore(problemFile);
        controller_.requestComputerMove( firstPlayerPlays, true );

        Move m = controller_.getLastMove();

        long elapsedTime = (System.currentTimeMillis() - time) / 1000;
        GameContext.log(1, "got " + m + " in " + elapsedTime + " seconds.");
        return m;
    }

    protected static void checkExpected(BlockadeMove m, int row, int col) {

        Assert.assertTrue("Was expecting "+ row +", "+ col +", but instead got "+m,
                          m.getToRow() == row && m.getToCol() == col);
    }

}