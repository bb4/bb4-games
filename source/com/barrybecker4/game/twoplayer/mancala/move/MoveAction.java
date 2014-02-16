/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.move;

import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard;

/**
 * Abstract class for classes that apply or undo mancala moves to the board.
 *
 * @author Barry Becker
 */
public class MoveAction {

    protected MancalaBoard board;

    /**
     * Constructor
     * @param board the mancala board
     */
    public MoveAction(MancalaBoard board) {
        this.board = board;
    }

}
