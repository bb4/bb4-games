// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.set;

import com.barrybecker4.game.common.board.IBoard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines the what is on the Set game board.
 * Maintains the decks and cards shown.
 *
 * @author Barry Becker
 */
public class SetBoard implements IBoard {

    /** the deck is like the board or model  */
    private List<Card> deck_;

    /** num cards to be shown on the board initially.  */
    private int initialNumCardsShown_;

    /** num cards on the board at the current moment.  */
    private int numCardsShown_;

    /** the maximum number of cards you can have and still not have a set (exceedingly rare). */
    private static final int MAX_CARDS_BEFORE_SET = 20;


    /**
     *  Construct the Set game controller
     */
    public SetBoard(int initialNumCardsShown) {
        initialNumCardsShown_ = initialNumCardsShown;
        initializeData();
    }

    /**
     * Return the game board back to its initial opening state
     */
    @Override
    public void reset() {
        initializeData();
    }

    @Override
    public SetBoard copy() {
        SetBoard b =  new SetBoard(initialNumCardsShown_);
        b.numCardsShown_ = this.numCardsShown_;
        return b;
    }

    protected void initializeData() {
        deck_ = new Deck();
        numCardsShown_ = initialNumCardsShown_;
    }

    /**
     *
     * @return the deck of cards (numCardsShown of which are shown face up on the board)
     */
    public List<Card> getDeck()  {
        return deck_;
    }

    /**
     * @return the number of face up cards on the board.
     */
    public int getNumCardsShowing() {
        return numCardsShown_;
    }

    /**
     * @param num  the number of cards to turn face up on the board.
     */
    public void addCards(int num) {
        for (int i=0; i<num; i++) {
            if (hasCardsToAdd()) {
                numCardsShown_++;
            }
        }
    }

    /**
     * remove a card from the board and put it back in the deck.
     */
    public void removeCard() {
        if (canRemoveCards()) {
            numCardsShown_--;
        }
    }

    /**
     * @return true if legal to remove more cards from the board.
     */
    public boolean canRemoveCards() {
        return (numCardsShown_ > 3);
    }

    /**
     * @return true if not showing cards remain in deck, or we have not yet reached MAX_CARDS_BEFORE_SET visible cards.
     */
    public boolean hasCardsToAdd() {
        return (numCardsShown_ < deck_.size() && numCardsShown_ < MAX_CARDS_BEFORE_SET);
    }


    /**
     * @param cards to remove (usually a set that has been discovered by a player)
     */
    public void removeCards(List<Card> cards) {
        deck_.removeAll(cards);
        numCardsShown_ -= 3;
    }

    private List<Card> getCardsOnBoard() {
        List<Card> cardsOnBoard = new LinkedList<>();
        for (int i = 0; i < getNumCardsShowing(); i++ ) {
            cardsOnBoard.add(getDeck().get(i));
        }
        return cardsOnBoard;
    }

    public List<Card> getSetsOnBoard()  {
       return Card.getSets(getCardsOnBoard());
    }

    public List<Card> getSelectedCards(){
        List<Card> selected = new ArrayList<>();
        for (int i = 0; i<getNumCardsShowing(); i++ ) {
            Card card = getDeck().get(i);
            if (card.isSelected()) {
                selected.add(card);
            }
        }
        return selected;
    }

    /**
     * @return  the number of sets that are currently on the board and have not yet been discovered.
     */
    public int getNumSetsOnBoard() {
        return Card.numSets(getCardsOnBoard());
    }

    /**
     * Game is over when there are no more sets to be found.
     *
     * @return true if the game is over.
     */
    public boolean isDone() {
        return !Card.hasSet(deck_);
    }
}
