/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions;
import com.barrybecker4.game.twoplayer.common.search.ISearchableHelper;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType;
import junit.framework.TestCase;

/**
 * @author Barry Becker
 */
public class ComputerVsComputerMiniMaxTest extends TestCase {

    protected TwoPlayerController controller;
    protected SearchOptions searchOptions;
    protected ISearchableHelper helper;


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        helper = new TicTacToeHelper();
        controller = helper.createController();

        initializeSearchOptions();
        searchOptions.setSearchStrategyMethod(SearchStrategyType.MINIMAX);
        searchOptions.getBruteSearchOptions().setLookAhead(4);
        searchOptions.getBruteSearchOptions().setAlphaBeta(false);
    }

    private void initializeSearchOptions() {

        searchOptions = helper.createSearchOptions();
        PlayerList players = controller.getPlayers();
        ((TwoPlayerPlayerOptions) players.getPlayer1().getOptions()).setSearchOptions(searchOptions);
        ((TwoPlayerPlayerOptions) players.getPlayer2().getOptions()).setSearchOptions(searchOptions);
    }


    /**
     * In a computer vs computer game, the second move must be in the corner, not on the edge
     */
    public void testSecondMoveInCorner() {

        controller.computerMovesFirst();
        TwoPlayerMove move = (TwoPlayerMove)controller.getLastMove();
        assertEquals("Unexpected first move for computer.", new ByteLocation(2, 2), move.getToLocation());

        controller.requestComputerMove(false, true);
        move = (TwoPlayerMove)controller.getLastMove();
        assertEquals("Unexpected second move for computer.", new ByteLocation(1, 1), move.getToLocation());
    }
}
