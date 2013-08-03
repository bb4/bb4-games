/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common;

import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.common.Move;

/**
 * The TwoPlayerController communicates with the viewer via this interface.
 * Alternatively, we could use RMI or events, but for now the minimal interface is
 * defined here and called directly by the controller.
 *
 * @author Barry Becker
 */
public interface TwoPlayerViewModel extends GameViewModel {

    /**
     * Called when the controller has found the next computer move and needs to make the viewer aware of it.
     * @param m the computers move
     */
    void computerMoved(Move m);

    /**
     * Used when the computer is playing against itself, and you want the game to show up in the viewer and
     * be synchronous (block and not in separate thread).
     */
    void doComputerVsComputerGame();

    /**
     * Currently this does not actually step forward just one search step, but instead
     * stops after PROGRESS_STEP_DELAY more milliseconds of searching.
     */
    void step();

    /**
     * resume searching for the next move at full speed.
     */
    void continueProcessing();

}
