// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common;

import com.barrybecker4.game.common.board.IBoard;

/**
 * The GameController communicates with the viewer via this interface.
 * Alternatively we could use RMI or events, but for now the minimal interface is
 * defined here and called directly by the controller.
 *
 * @author Barry Becker
 */
public interface IGameBoardViewer {

    /**
     * return the game to its original state.
     */
    void reset(IBoard board);

   /**
     * repaint the game board ui
     */
    void refresh();

    /**
     * In some cases the viewer is used to show games only.
     * In this case, no user interaction is allowed with th board.
     */
    void setViewOnly(boolean viewOnly);
}
