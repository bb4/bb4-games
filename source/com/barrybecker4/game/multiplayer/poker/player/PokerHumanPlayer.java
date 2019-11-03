/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.player;

import com.barrybecker4.game.common.player.PlayerAction;
import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.poker.model.PokerAction;

import java.awt.*;


/**
 * Represents an Human Poker player.
 *
 * @author Barry Becker
 */
public class PokerHumanPlayer extends PokerPlayer {


    public PokerHumanPlayer(String name,  int money, Color color) {
        super(name, money, color, true);
    }

    @Override
    public PlayerAction getAction(MultiGameController pc) {
        return action;
    }

    @Override
    public void setAction(PlayerAction action) {
        this.action = (PokerAction) action;
    }

}



