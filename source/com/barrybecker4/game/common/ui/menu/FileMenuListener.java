// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.menu;

/**
 * This interface must be implemented by any class that wants to receive GameMenuChangeEvents
 *
 * @author Barry Becker
 */
public interface FileMenuListener {

    /** Opens a saved file and displays it. */
    void openFile();

    /** Saves the current game to a file. */
    void saveFile();

    /** Saves a picture of the current game in its current state. */
    void saveImage();

}
