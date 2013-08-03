/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set;

import com.barrybecker4.game.common.GameOptions;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;

import java.util.Arrays;
import java.util.List;

/**
 * Defines everything the computer needs to know to play Set with multiple players.
 *
 * todo
 *  - selection does not always take
 *  - show sets panel does not scroll.
 *  - add hints
 *
 * @author Barry Becker
 */
public class SetController extends MultiGameController {

    /** represents the deck and cards shown on the board  */
    private SetBoard board_;

    private static final int NO_PLAYER_SELECTED = -1;

    /** currently selected player. -1 if none selected        */
    private int currentPlayerIndex_ = NO_PLAYER_SELECTED;


    /**
     *  Construct the Set game controller
     */
    public SetController() {
        initializeData();
    }

    @Override
    protected SetBoard createTable(int rows, int cols) {
        return new SetBoard(((SetOptions)getOptions()).getInitialNumCardsShown());
    }

    /**
     * Return the game board back to its initial opening state
     */
    @Override
    public void reset() {
        initializeData();
    }

    @Override
    protected void initializeData() {
        board_ = (SetBoard) getBoard(); //createTable(0, 0);
        initPlayers();
        gameChanged();
    }

    /**There is not a concept of turns in set.
     * @return the index of the next player
     */
    @Override
    protected int advanceToNextPlayerIndex() {
        assert false : "not used in set game";
        return 0;
    }

    @Override
    public GameOptions createOptions() {
        return new SetOptions();
    }

    @Override
    public boolean isOnlinePlayAvailable() {
        return false;
    }

    /**
     * @param num  the number of cards to turn face up on the board.
     */
    public void addCards(int num) {
        board_.addCards(num);
        gameChanged();
    }

    /**
     * remove a card from the board and put it back in the deck.
     */
    public void removeCard() {
        board_.removeCard();
        gameChanged();
    }

    /**
     * @return true if legal to remove more cards from the board.
     */
    public boolean canRemoveCards() {
        return board_.canRemoveCards();
    }

    /**
     * @return true if not showing cards remain in deck, or we have not yet reached MAX_CARDS_BEFORE_SET visible cards.
     */
    public boolean hasCardsToAdd() {
        return board_.hasCardsToAdd();
    }


    /**
     * @param cards to remove (usually a set that has been discovered by a player)
     */
    public void removeCards(List<Card> cards) {
        board_.removeCards(cards);
        gameChanged();
    }

    public void setCurrentPlayer(Player player)  {
        if (player == null) {
            currentPlayerIndex_ = NO_PLAYER_SELECTED;
            return;
        }
        for (int i = 0; i < getPlayers().size(); i++) {
            if (player == getPlayers().get(i)) {
                currentPlayerIndex_ = i;
                return;
            }
        }
    }

    public List<Card> getSetsOnBoard()  {
       return board_.getSetsOnBoard();
    }

     /**
      * By default we start with one human and one robot player.
      * After that, they can change manually to get different players.
      */
    @Override
    protected void initPlayers() {

        if (getPlayers() == null) {
            // create the default players. One human and one robot.
            PlayerList players = new PlayerList();
            players.add(SetPlayer.createSetPlayer("Player 1", SetPlayer.getNewPlayerColor(players), true));
            players.add(SetPlayer.createSetPlayer("Player 2", SetPlayer.getNewPlayerColor(players), false));
            players.get(1).setName(players.get(1).getName()+'('+((SetRobotPlayer)players.get(1)).getRobotType()+')');
            setPlayers(players);
        }
    }

    @Override
    public MultiGamePlayer getCurrentPlayer() {
        if (currentPlayerIndex_ != NO_PLAYER_SELECTED) {
            return (MultiGamePlayer)getPlayers().get(currentPlayerIndex_);
        }
        return null;
    }

    public void gameChanged() {
        if (getViewer() != null)  {
            ((GameBoardViewer) getViewer()).sendGameChangedEvent(null);
        }
    }

    @Override
    public void computerMovesFirst() {
        assert false : "No one moves first in set.";
    }

    /**
     * Game is over when there are no more sets to be found.
     *
     * @return true if the game is over.
     */
    @Override
    public boolean isDone() {
        return board_.isDone();
    }

    /**
     * @return the player with the most sets
     */
    @Override
    public List<? extends MultiGamePlayer> determineWinners() {
        PlayerList players = getPlayers();
        SetPlayer winner;

        int first=0;

        winner = (SetPlayer)players.get(first);
        int mostSets = winner.getNumSetsFound();

        for (int i = first+1; i < players.size(); i++) {
            SetPlayer p = (SetPlayer)players.get(i);
            if (p.getNumSetsFound() > mostSets) {
                mostSets = p.getNumSetsFound();
                winner = p;
            }
        }

        return Arrays.asList(winner);
    }
}
