/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer;
import com.barrybecker4.game.multiplayer.galactic.ui.GalaxyViewer;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines everything the computer needs to know to play Galactic Empire.
 * TODO:
 *  - mousing over menu item should highlight planet in the viewer
 *  - mouse over should still work when entering orders (non-modal?)
 *  - have robot players with different strategy.
 *  - have button to show stats dialog (basically same as tally dialog) on toolbar.
 *    See PlanetDetailsDialog, StatsDialog.
 *  - in addition to order dialog should be able to directly click on source, then dest planet,
 *    then enter number of ships.
 *
 *  - Brian's wish list
 *    - show pictures of humans and aliens in battle.
 *    -
 *
 * bugs
 *  - after click on fight, it should change immediately to close
 *  - lines showing armada trajectories do not always line up with planet centers
 *  - remove selected source planet from dest list.
 *  - summary dialog should show number of years.
 *
 * fixed:
 *    don't allow computer or players to send ships to distant planets if they will not arrive before end of the game.
 *    the number of ships available to send does not change when you change the source planet in orders dlg.
 *    after a battle the board does not refresh to show the captured planet.
 *    order dialog should always be in front (modal?)
 *    highlight the planet where a battle is occurring.
 *    window not popping up in the right spot.
 *
 * @author Barry Becker
 */
public class GalacticController extends MultiGameController {

    private static final int DEFAULT_NUM_ROWS = 16;
    private static final int DEFAULT_NUM_COLS = 16;

    /**
     *  Construct the Galactic game controller
     */
    public GalacticController() {
        super(DEFAULT_NUM_ROWS, DEFAULT_NUM_COLS );
    }

    /**
     *  Construct the Galactic game controller given an initial board size
     */
    @Override
    protected Galaxy createTable(int nrows, int ncols ) {
        return new Galaxy( nrows, ncols );
    }

     /**
      * by default we start with one human and one robot player.
      */
    @Override
    protected void initPlayers() {
        // we just init the first time.
        // After that, they can change manually to get different players.
        if (getPlayers() == null) {
            // create the default players. One human and one robot.
            PlayerList players = new PlayerList();

            Planet homePlanet = new Planet('A', GalacticPlayer.DEFAULT_NUM_SHIPS, 10, new ByteLocation(5, 5));
            players.add(GalacticPlayer.createGalacticPlayer("Admiral 1",
                                      homePlanet, MultiGamePlayer.getNewPlayerColor(players), true));
            homePlanet.setOwner((GalacticPlayer)players.get(0));

            homePlanet = new Planet('B', GalacticPlayer.DEFAULT_NUM_SHIPS, 10, new ByteLocation(10, 10));
            players.add(GalacticPlayer.createGalacticPlayer("Admiral 2",
                                      homePlanet, MultiGamePlayer.getNewPlayerColor(players), false));
            homePlanet.setOwner((GalacticPlayer)players.get(1));
            setPlayers(players);
        }
        currentPlayerIndex_ = 0;

        ((Galaxy)getBoard()).initPlanets(getPlayers(), (GalacticOptions)getOptions());
    }

    /**
     *
     * @return the number of years (turns) remaining in the game.
     */
    public int getNumberOfYearsRemaining() {
        Move m = getLastMove();
        return ((GalacticOptions)getOptions()).getMaxYearsToPlay() - ((m != null)? this.getNumMoves() : 0) - 2;
    }

    @Override
    public boolean isOnlinePlayAvailable() {return false; }



    /**
     * @return true if the game is over.
     */
    @Override
    public boolean isDone() {
        if (getLastMove()==null) {
            return false;
        }
        // add one so indexed by 1 instead of 0, add 1 because its the "last" move
        if ((this.getNumMoves() + 2) >= ((GalacticOptions)getOptions()).getMaxYearsToPlay()) {
            return true; // done
        }
        return Galaxy.allPlanetsOwnedByOnePlayer();
    }

    /**
     * @return the player with the most planets
     */
    @Override
    public List<? extends MultiGamePlayer> determineWinners() {

        GalacticPlayer winner;
        double maxCriteria = -1.0;
        Scorer scorer = new Scorer();

        // first find the highest score
        for (Player player : getPlayers()) {
            double criteria = scorer.score((GalacticPlayer) player);

            if (criteria > maxCriteria) {
                maxCriteria = criteria;
            }
        }
        // then find all players with the highest score
        List<GalacticPlayer> winners = new ArrayList<GalacticPlayer>();
        for (Player p : getPlayers()) {
            GalacticPlayer player = (GalacticPlayer) p;
            double criteria = scorer.score(player);
            if (criteria == maxCriteria) {
                winners.add(player);
            }
        }
        return winners;
    }

    /**
     * Advance to the next player turn in order.
     */
    @Override
    protected void doAdvanceToNextPlayer() {
        GalaxyViewer gviewer  = (GalaxyViewer)this.getViewer();

        // show message when done.
        if (isDone()) {
            GameContext.log(1, "advanceToNextPlayer done");
            ((GameBoardViewer)getViewer()).sendGameChangedEvent(null);
        }
        advanceToNextPlayerIndex();

        if (getCurrentPlayer() == getPlayers().getFirstPlayer()) {

            // @@ I would really like to
            // Precalculate the battle sequence on the server and store it in the move, then send
            // the result in the move to the client.
            // however, there are problems with that, so I just calculate it in the veiwer for now.

            GalacticTurn gmove = gviewer.createMove(getLastMove());
            //gviewer.showMove(gmove);

            // records the result on the board.
            makeMove(gmove);
        }

        if (!getCurrentPlayer().isHuman()) {
            gviewer.doComputerMove(getCurrentPlayer());
        }

        // fire game changed event
        ((GameBoardViewer)getViewer()).sendGameChangedEvent(null);
    }

    /**
     * make it the next players turn
     * @return the index of the next player
     */
    @Override
    protected int advanceToNextPlayerIndex() {
        currentPlayerIndex_ = (currentPlayerIndex_+1) % getPlayers().size();
        return currentPlayerIndex_;
    }

    @Override
    public GameOptions createOptions() {
         return new GalacticOptions();
    }
}
