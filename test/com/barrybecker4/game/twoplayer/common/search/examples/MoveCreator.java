/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.examples;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;

/**
 * Create stub two player moves
 * Factory.
 *
 * @author Barry Becker
 */
public class MoveCreator {

    private static final int FAKE_BOARD_SIZE = 19;

    int moveCount = 0;

    public MoveCreator() {
    }

    public TwoPlayerMoveStub createMove(int value, boolean player1Move, TwoPlayerMoveStub parent) {

        return new TwoPlayerMoveStub(value, player1Move, createToLocation(), parent);
    }

    /**
     * The location is not really used, just give it something unique so the hash works.
     * @return new to location
     */
    private Location createToLocation() {
        moveCount++;
        return new ByteLocation(moveCount / FAKE_BOARD_SIZE, moveCount % FAKE_BOARD_SIZE);
    }

}
