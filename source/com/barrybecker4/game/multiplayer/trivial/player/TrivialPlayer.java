/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.player;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.player.PlayerAction;
import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;
import com.barrybecker4.game.multiplayer.common.MultiPlayerMarker;
import com.barrybecker4.game.multiplayer.trivial.TrivialAction;

import java.awt.*;
import java.text.MessageFormat;

/**
 * Represents a trivial player in a game
 *
 * @author Barry Becker
 */
public abstract class TrivialPlayer extends MultiGamePlayer {

    private static final long serialVersionUID = 1;
    private static final int MAX_VALUE = 100;

    private MultiPlayerMarker piece_;
    private int value;
    private TrivialAction action_;


    /** only becomes true when the player decides to reveal his value */
    private boolean revealed_ = false;


    /**
     * Constructor
     */
    TrivialPlayer(String name, Color color, boolean isHuman) {
        super(name, color, isHuman);

        value = (int) (MAX_VALUE * Math.random()) + 1;

        piece_ = new MultiPlayerMarker(this);
        revealed_ = false;
    }

    /**
     * @return a number between 0 and MAX_VALUE.
     */
    public int getValue() {
        return value;
    }

    @Override
    public PlayerAction getAction(MultiGameController pc) {
        return action_;
    }

    @Override
    public void setAction(PlayerAction action) {
        action_ = (TrivialAction) action;
    }

    /**
     * You can only reveal your value, never hide it once revealed.
     */
    public void revealValue() {
        revealed_ = true;
    }

    public boolean isRevealed() {
        return revealed_;
    }

    @Override
    public MultiPlayerMarker getPiece() {
        return piece_;
    }

    /**
     * Factory method for creating players of the appropriate type.
     * @param name name of the trivial player
     * @param color color of the player
     * @param isHuman true if human
     * @return the new player
     */
    public static TrivialPlayer createTrivialPlayer(String name, Color color, boolean isHuman) {
           return isHuman ?
                   new TrivialHumanPlayer(name, color):
                   new TrivialRobotPlayer(name, color);
    }

    /**
     * @param i index of player
     * @return  the default name for player i
     */
    public String getDefaultName(int i) {
        Object[] args = {Integer.toString(i)};
        return MessageFormat.format(GameContext.getLabel("TRIVIAL_DEFAULT_NAME"), args );
    }

    public void setPiece(MultiPlayerMarker piece) {
        piece_ = piece;
    }

}



