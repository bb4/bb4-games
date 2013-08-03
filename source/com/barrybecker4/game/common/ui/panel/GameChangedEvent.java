/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.panel;

import com.barrybecker4.game.common.IGameController;
import com.barrybecker4.game.common.Move;

import java.util.EventObject;

/**
 * This event gets fired whenever the Game state changes.
 *
 * @see GameChangedListener
 *
 * @author Barry Becker
 */
public final class GameChangedEvent extends EventObject {

    private final Move move_;
    private final IGameController controller_;

    /**
     * constructor
     * @param mv the most recently played move
     * @param controller the controller
     */
    public GameChangedEvent(Move mv, IGameController controller, Object source ) {
        super(source);
        move_ = mv;
        controller_ = controller;
    }

    /**
     * @return the game controller for the viewer that sent the event.
     */
    public IGameController getController() {
        return controller_;
    }

    /**
     * @return the move that just caused the game changed event to fire.
     */
    public Move getMove() {
        return move_;
    }

}


