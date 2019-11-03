/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.player;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;
import com.barrybecker4.game.multiplayer.poker.PokerController;
import com.barrybecker4.game.multiplayer.poker.hand.Hand;
import com.barrybecker4.game.multiplayer.poker.hand.HandScore;
import com.barrybecker4.game.multiplayer.poker.hand.PokerHandScorer;
import com.barrybecker4.game.multiplayer.poker.model.PokerAction;
import com.barrybecker4.game.multiplayer.poker.model.PokerRound;
import com.barrybecker4.game.multiplayer.poker.ui.render.PokerPlayerMarker;

import java.awt.Color;
import java.text.MessageFormat;

/**
 * Represents a Player in a poker game
 *
 * @author Barry Becker
 */
public abstract class PokerPlayer extends MultiGamePlayer {

    /** this player's home planet. (like earth is for humans)  */
    private Hand hand;
    private PokerPlayerMarker piece;

    /** the players current cash amount in dollars */
    private int cash;

    /** becomes true once the player has folded */
    private boolean hasFolded;

    /** the score of the current hand */
    private HandScore handScore;

    /** this becomes true when the player has no money left or not enough to ante up. */
    private boolean outOfGame;

    // the amount that this player has contributed to the pot
    private int contribution;

    PokerAction action;


    PokerPlayer(String name, int money, Color color, boolean isHuman) {
        super(name, color, isHuman);
        cash = money;
        contribution = 0;
        hasFolded = false;
        outOfGame = false;
        piece = new PokerPlayerMarker(this);
    }

    /**
     * Factory method for creating poker players of the appropriate type.
     * @return new poker player
     */
    public static PokerPlayer createPokerPlayer(String name, int money, Color color, boolean isHuman) {
       if (isHuman)
           return new PokerHumanPlayer(name, money, color);
        else
           return PokerRobotPlayer.getSequencedRobotPlayer(name, money, color);
    }

    /**
     *
     * @param i index of player
     * @return  the default name for player i
     */
    public String getDefaultName(int i) {
        Object[] args = {Integer.toString(i)};
        return MessageFormat.format(GameContext.getLabel("POKER_DEFAULT_NAME"), args );
    }

    public Hand getHand() {
        return hand;
    }

    public HandScore getHandScore() {
        return handScore;
    }

    public void setHand( Hand hand ) {
        this.hand = hand;
        this.handScore = new PokerHandScorer().getScore(hand);
    }

    public int getCash() {
        return cash;
    }

    public void setFold(boolean folded) {
        hasFolded = folded;
    }

    public boolean hasFolded() {
        return hasFolded;
    }

    public boolean isOutOfGame() {
        return outOfGame || (cash <= 0);
    }

    @Override
    public PokerPlayerMarker getPiece() {
        return piece;
    }

    public void setPiece(PokerPlayerMarker piece) {
        this.piece = piece;
    }

    /**
     * Have this player contribute some amount to the pot.
     * of course the amount must be less than they have altogether.
     * If it is greater, then he/she is out of the game.
     * @param round the current poker round
     * @param amount amount contributed to the pot.
     */
    public void contributeToPot(PokerRound round, int amount) {

        if (cash < amount)  {
            // the player is out of the game if he is asked to contribute more than he has.
            outOfGame = true;
            hasFolded = true; // anyone out of the game is also considered folded
            return;
        }
        if (amount > 0) {
            cash -= amount;
            contribution += amount;
            round.addToPot(amount);
        }
    }

    public int getCallAmount(PokerController pc) {
        int currentMaxContrib = pc.getCurrentMaxContribution();
        return (currentMaxContrib - getContribution());
    }

    /**
     * start the player over fresh for a new round
     */
    public void resetPlayerForNewRound()  {
        hasFolded = false;
        contribution = 0;
        outOfGame = false;
    }

    /**
     * @return  the amount this player has currently put in the pot this round.
     */
    public int getContribution() {
        return contribution;
    }

    /**
     * the pot goes to this player
     */
    public void claimPot(int potAmount)  {
        cash += potAmount;
    }

    @Override
    protected String additionalInfo() {
        StringBuilder sb = new StringBuilder();
        if (getHand() != null) {
            sb.append(" Hand: ").append(getHand());
        }
        sb.append(" Money: ").append(cash);
        return sb.toString();
    }

}



