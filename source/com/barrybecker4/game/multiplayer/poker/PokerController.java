/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker;

import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;
import com.barrybecker4.game.multiplayer.poker.model.Dealer;
import com.barrybecker4.game.multiplayer.poker.model.PokerRound;
import com.barrybecker4.game.multiplayer.poker.model.PokerTable;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;
import com.barrybecker4.game.multiplayer.poker.player.PokerRobotPlayer;
import com.barrybecker4.game.multiplayer.poker.ui.PokerGameViewer;

import java.util.List;

/**
 * Defines everything the computer needs to know to play Poker.
 *
 * ToDo list
 * - for chat, you should only chat with those at your table if you are in a game, else chat only with those not in a game.
 * - something screwed up adding players out of order
 * - fix TrivialMarker not showing number.
 * - All players should have an action that they perform (for all games. This action is like a move in a 2 player game.)
 * - add host and port to game options
 * - use real faces for players
 * - Improve HandScore structure based on Peter Norvig's CS class
 * - Allow for ties and split pot between winners.
 *
 *  - options dialog
 *     - Texas hold'em
 *     - N card stud
 *          - num cards for each
 *          - whether to use jokers
 *          - allow n exchanges
 *          - raise limit (eg $20)
 *   - summary dlg
 *      - show who gets pot
 *      - show the pot
 *      - give option to start another round with same players
 *      - unless really done, only then can you exit.
 *
 *  bugs
 *     - at end of game the winning players winnings are not added to his cash.
 *     - robot player keeps adding last raise amount even though competitor is calling.
 *     - Raise amount not always matched! seems to happen in a multiplayer game when robots involved.
 *       this is because it should only include the callAmount if the player has not already gone.
 *
 * @author Barry Becker
 */
public class PokerController extends MultiGameController {

    private static final int DEFAULT_NUM_ROWS = 32;
    private static final int DEFAULT_NUM_COLS = 32;

    private static final int POKER_SERVER_PORT = 4443;

    private PokerRound round;

    /**
     * Construct the Poker game controller
     */
    public PokerController() {
        super( DEFAULT_NUM_ROWS, DEFAULT_NUM_COLS );
    }

    /**
     * @return the board representation object.
     */
    @Override
    public PokerTable getBoard() {
        return (PokerTable)super.getBoard();
    }

    /**
     * Return the game board back to its initial opening state
     */
    @Override
    public void reset() {
        super.reset();
        initializeData();
        round = getBoard().getRound();
        round.anteUp(getPlayers(), ((PokerOptions)getOptions()).getAnte());
    }

    @Override
    public GameOptions createOptions() {
        return new PokerOptions();
    }

    public MoveList getMoveList() {
        return getBoard().getMoveList();
    }

    /**
     * @return the poker game table given an initial board size
     */
    @Override
    protected PokerTable createTable(int nrows, int ncols ) {
        return new PokerTable(nrows, ncols);
    }

    public PokerRound getRound() {
        return round;
    }

    /**
     * By default we start with one human and one robot player.
     * We just init the first time,
     * after that, they can change manually to get different players.
     */
    @Override
    protected void initPlayers() {

        if (getPlayers() == null) {
            // create the default players. One human and one robot.
            PlayerList players = new PlayerList();


            players.add(PokerPlayer.createPokerPlayer("Player 1",
                                       100, MultiGamePlayer.getNewPlayerColor(players), true));


            players.add(PokerPlayer.createPokerPlayer("Player 2",
                                       100, PokerPlayer.getNewPlayerColor(players), false));
            players.get(1).setName(players.get(1).getName()+'('+((PokerRobotPlayer)players.get(1)).getType()+')');
            setPlayers(players);
        }

        deal();
        currentPlayerIndex_ = 0;

        getBoard().initPlayers(getPlayers());
    }

    /**
     * @return the maximum contribution made by any player so far this round
     */
    public int getCurrentMaxContribution() {
        return round.getCurrentMaxContribution(getPlayers());
    }

    /**
     * @return the min number of chips of any player
     */
    public int getAllInAmount() {
        return round.getAllInAmount(getPlayers());
    }

    public int getPotValue() {
        return round.getPotValue();
    }

    public void clearPot() {
        round.clearPot();
    }

    @Override
    public int getServerPort() {
        return POKER_SERVER_PORT;
    }

    /**
     * Game is over when only one player has enough money left.
     *
     * @return true if the game is over.
     */
    @Override
    public boolean isDone() {
        if (getLastMove() == null)
            return false;
        int numPlayersStillPlaying = 0;
        for (Player p : getPlayers()) {
            PokerPlayer player = (PokerPlayer) p.getActualPlayer();
            if (!player.isOutOfGame()) {
                numPlayersStillPlaying++;
            }
        }
        return (numPlayersStillPlaying == 1);
    }

    /**
     * Advance to the next player turn in order.
     */
    @Override
    protected void doAdvanceToNextPlayer() {

        PokerGameViewer pviewer = (PokerGameViewer) getViewer();
        pviewer.refresh();
        advanceToNextPlayerIndex();

        if (round.roundOver(getPlayers(), playIndex_)) {
            // every player left in the game has called.
            //PokerRound round = pviewer.createMove(getLastMove());
            // records the result on the board.
            makeMove(round);
            pviewer.refresh();

            doRoundOverBookKeeping(pviewer);
        }

        // show message when done.
        // moved from above.
        if (isDone()) {
            pviewer.sendGameChangedEvent(null);
        }

        if (!getCurrentPlayer().isHuman() && !isDone()) {
            pviewer.doComputerMove(getCurrentPlayer());
        }

        // fire game changed event
        pviewer.sendGameChangedEvent(null);
    }

    /**
     * Take care of distributing the pot, dealing, anteing up.
     * In the rare case of a tie the pot will get split evenly among the winners.
     * @param pviewer poker viewer
     */
    private void doRoundOverBookKeeping(PokerGameViewer pviewer) {

        List<PokerPlayer> winners = determineWinners();
        int numWinners = winners.size();
        // house gets the remainder if there is any
        int winnings = this.getPotValue() / numWinners;

        for (PokerPlayer winner : winners) {
            winner.claimPot(winnings);
        }
        this.clearPot();
        pviewer.showRoundOver(winners, winnings);

        // if round not over yet, start a new round deal new cards and ante
        if (!isDone()) {
            deal();
            round.anteUp(getPlayers(), ((PokerOptions)getOptions()).getAnte());
            // the player to start the betting in the next round is the next player who still has some money left.
            do {
               startingPlayerIndex_ = (++startingPlayerIndex_) % this.getPlayers().getNumPlayers();
            }
            while (((PokerPlayer)getPlayer(startingPlayerIndex_)).isOutOfGame());

            currentPlayerIndex_ = startingPlayerIndex_;
            playIndex_ = 0;
        }
    }

    /**
     * @return the player(s) with the best poker hand(s) for this round. In some very rare situations there
     * may be ties.
     */
    @Override
    public List<PokerPlayer> determineWinners() {
        return round.determineWinners(getPlayers());
    }

    /**
     * make it the next players turn
     * @return the index of the next player
     */
    @Override
    protected int advanceToNextPlayerIndex() {
        playIndex_++;
        currentPlayerIndex_ = (currentPlayerIndex_+1) % getPlayers().size();
        while (((PokerPlayer) getPlayer(currentPlayerIndex_).getActualPlayer()).hasFolded()) {
            currentPlayerIndex_ = (currentPlayerIndex_+1) % getPlayers().size();
        }
        return currentPlayerIndex_;
    }

    /**
     * @param players  the players currently playing the game
     */
    @Override
    public void setPlayers( PlayerList players ) {
        super.setPlayers(players);
        // deal cards to the players
        deal();
    }

    private void deal() {
        new Dealer().dealCardsToPlayers(getPlayers(), 5);
    }
}
