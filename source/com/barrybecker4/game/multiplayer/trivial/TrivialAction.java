/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial;

import com.barrybecker4.game.common.player.PlayerAction;

/**
 * This is what will get sent between client and server as an action for a particular player.
 * Encapsulates the state change.
 *
 * @author Barry Becker
 */
public class TrivialAction extends PlayerAction {

    public enum Name {KEEP_HIDDEN, REVEAL}

    private Name name_;


    public TrivialAction(String playerName, Name name)  {
        super(playerName);
        name_ = name;
    }

    public Name getActionName() {
        return name_;
    }

    public String toString() {
        return "[TrivialAction name=" + name_ +" for "+ getPlayerName() +"]";
    }
}
