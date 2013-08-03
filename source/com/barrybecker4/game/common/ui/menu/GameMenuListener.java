// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.menu;

/**
 * This interface must be implemented by any class that wants to receive GameMenuChangeEvents
 *
 * @author Barry Becker
 */
public interface GameMenuListener {

    /** The new name of the game selected */
    void gameChanged(String gameName);
}
