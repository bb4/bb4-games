/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.player;

import java.awt.*;

/**
 * Represents an Human Trivial player.
 *
 * @author Barry Becker
 */
public class TrivialHumanPlayer extends TrivialPlayer {

    private static final long serialVersionUID = 1;


    public TrivialHumanPlayer(String name,  Color color) {
        super(name, color, true);
    }
}



