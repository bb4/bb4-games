// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move.wall;

import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;

/**
 * Find walls not blocking straight moves.
 *
 * @author Barry Becker
 */
class WallChecker {

    protected BlockadeBoard board;

    /**
     * Constructor
     */
    WallChecker(BlockadeBoard board) {
        this.board = board;
    }

}
