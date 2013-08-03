/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.player.PlayerOptions;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerOptions;
import com.barrybecker4.game.twoplayer.common.search.Searchable;

import java.awt.*;

/**
 * Defines how the computer should play checkers.
 *
 * @author Barry Becker
 */
public class CheckersController extends TwoPlayerController {

    /**
     *  Constructor.
     */
    public CheckersController() {
        initializeData();
    }

    @Override
    protected CheckersBoard createBoard() {
        return new CheckersBoard();
    }

    /**
     * this gets the checkers specific weights.
     */
    @Override
    protected void initializeData() {
        weights_ = new CheckersWeights();
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new TwoPlayerOptions();
    }

    @Override
    protected PlayerOptions createPlayerOptions(String playerName, Color color) {
        return new CheckersPlayerOptions(playerName, color);
    }

    /**
     * The computer makes the first move in the game.
     */
    @Override
    public void computerMovesFirst() {
        // determine the possible moves and choose one at random.
        MoveList moveList = getSearchable().generateMoves( null, weights_.getPlayer1Weights());

        assert (!moveList.isEmpty());
        makeMove( moveList.getRandomMove() );

        player1sTurn_ = false;
    }

    /**
     * Measure is determined by the score (amount of territory)
     * If called before the end of the game it just returns 0 - same as it does in the case of a tie.
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    @Override
    public int getStrengthOfWin()  {
        if (!getPlayers().anyPlayerWon())  {
            return 0;
        }
        return getSearchable().worth((TwoPlayerMove) getLastMove(), weights_.getDefaultWeights());
    }

    @Override
    protected Searchable createSearchable(TwoPlayerBoard board, PlayerList players) {
        return new CheckersSearchable(board, players);
    }
}
