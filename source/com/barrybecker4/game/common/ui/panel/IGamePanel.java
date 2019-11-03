/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.panel;

import javax.swing.*;

/**
 *
 * @author Barry Becker
 */
public interface IGamePanel {

    /**
     * common initialization in the event that there are multiple constructors.
     */
    void init(JFrame parent);

    /** Open a new game. */
    void openGame();

    /** Save the current game. */
    void saveGame();

    /**
     * @return the title for the applet/application window.
     */
    String getTitle();
}
