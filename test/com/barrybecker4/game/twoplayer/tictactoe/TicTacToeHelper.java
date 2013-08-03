/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe;

import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.search.SearchableHelper;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;

/**
 * @author Barry Becker
 */
public class TicTacToeHelper extends SearchableHelper {

    /**
     * @return default search options for all games
     */
    @Override
    public SearchOptions createSearchOptions() {
        SearchOptions options = new SearchOptions();
        options.getBruteSearchOptions().setLookAhead(2);
        options.getBruteSearchOptions().setAlphaBeta(true);
        options.getBestMovesSearchOptions().setPercentLessThanBestThresh(0);
        options.getBestMovesSearchOptions().setPercentageBestMoves(100);
        options.getBruteSearchOptions().setQuiescence(false);
        return options;
    }

    @Override
    public TwoPlayerController createController() {
        return new TicTacToeController();
    }

    @Override
    public String getTestCaseDir() {
        return EXTERNAL_TEST_CASE_DIR + "tictactoe/cases/searchable/";
    }

    @Override
    protected String getStartGameMoveFileName(boolean player1) {
        return player1 ? "midGameCenterX" : "midGameCenterO";
    }

    @Override
    protected String getMiddleGameMoveFileName(boolean player1) {
        return player1 ? "lateMidGameX" : "lateMidGameO";
    }

    @Override
    protected String getEndGameMoveFileName(boolean player1) {
        return player1 ? "endGameX" : "endGameO";
    }
}
