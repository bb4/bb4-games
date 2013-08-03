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

    EvaluationPerspective evalPerspective;

    int moveCount = 0;

    public MoveCreator(EvaluationPerspective evalPersp) {
        evalPerspective = evalPersp;
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

    /**
     * We can tell our depth in the tree by looking at the number of ancestors we have.
     * @param parent parent move
     * @return depth in game tree.
     */
    private int getDepth(TwoPlayerMoveStub parent) {
        int depth = 0;
        TwoPlayerMoveStub nextParent = parent;
        while (nextParent != null) {
            nextParent = nextParent.getParent();
            depth++;
        }
        return depth;
    }
}
