/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;
import com.barrybecker4.game.multiplayer.trivial.player.TrivialPlayer;
import com.barrybecker4.game.multiplayer.trivial.player.TrivialRobotPlayer;

import java.util.Arrays;
import java.util.List;

/**
 * Defines everything the computer needs to know to play Trivial game.
 * In this simplistic game, players either keep their card hidden or revealed
 * when it is their turn. When all cards are revealed the highest value card
 * wins. The purpose is to test multi-player game without complex rules.
 *
 * @author Barry Becker
 */
public class TrivialController extends MultiGameController {

    private static final int DEFAULT_NUM_ROWS = 32;
    private static final int DEFAULT_NUM_COLS = 32;

    private static final int TRIVIAL_SERVER_PORT = 4447;


    /**
     * Construct the game controller
     */
    public TrivialController() {
        super(DEFAULT_NUM_ROWS, DEFAULT_NUM_COLS);
    }

    /**
     *  Construct the game controller given an initial board size
     */
    @Override
    protected TrivialTable createTable(int nrows, int ncols) {
        return new TrivialTable(nrows, ncols);
    }


    @Override
    public GameOptions createOptions() {
        return new TrivialOptions();
    }

    /**
     * By default we start with one human and one robot player.
     * Just initialize the first time.
     * After that, they can change manually to get different players.
     */
    @Override
    protected void initPlayers() {

        if (getPlayers() == null) {
            // create the default players. One human and one robot.
            PlayerList players = new PlayerList();

            players.add(TrivialPlayer.createTrivialPlayer("Player 1",
                                  MultiGamePlayer.getNewPlayerColor(players), true));

            players.add(TrivialPlayer.createTrivialPlayer("Player 2",
                                  TrivialPlayer.getNewPlayerColor(players), false));
            players.get(1).setName(players.get(1).getName()+'('+((TrivialRobotPlayer)players.get(1)).getType()+')');
            setPlayers(players);
        }

        currentPlayerIndex_ = 0;
        ((TrivialTable)getBoard()).initPlayers(getPlayers());
    }

    @Override
    public int getServerPort() {
        return TRIVIAL_SERVER_PORT;
    }

    /**
     * Game is over when only one player has enough money left to play
     *
     * @return true if the game is over.
     */
    @Override
    public boolean isDone() {

        int numPlayersStillHidden = 0;
        for (Player p : getPlayers()) {
            TrivialPlayer tp  = (TrivialPlayer) p.getActualPlayer();

            if (!tp.isRevealed())  {
                numPlayersStillHidden++;
            }
        }
        return (numPlayersStillHidden == 0);
    }

    /**
     * @return the player with the best Trivial hand
     */
    @Override
    public List<? extends MultiGamePlayer> determineWinners() {
        PlayerList players = getPlayers();
        TrivialPlayer winner = null;

        int maxValue = -1;

        for (Player player : players) {
            TrivialPlayer p = (TrivialPlayer)player;
            if (p.getValue() > maxValue) {
                maxValue = p.getValue();
                winner = p;
            }
        }
        return Arrays.asList(winner);
    }

    /**
     * make it the next players turn
     * @return the index of the next player
     */
    @Override
    public int advanceToNextPlayerIndex() {
        playIndex_++;
        Player player;
        do {
            // if the current player has revealed, then advance to the next player.
            currentPlayerIndex_ = (currentPlayerIndex_ + 1) % getPlayers().size();
            player = getPlayer(currentPlayerIndex_).getActualPlayer();
            GameContext.log(0, "currentPlayerIndex=" + currentPlayerIndex_);

        }  while (((TrivialPlayer)player).isRevealed());

        return currentPlayerIndex_;
    }

}
