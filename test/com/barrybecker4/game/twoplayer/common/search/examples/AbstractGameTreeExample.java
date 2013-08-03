/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.examples;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;

import java.util.Arrays;

/**
 * An abstract game tree for testing search strategies.
 * Note that in all the sample trees. Only the leaf values really matter, the non-leaf values should be ignored.
 * Though the non-leaf values may be used for move ordering, they should not effect the final inherited values.
 *
 * @author Barry Becker
 */
public abstract class AbstractGameTreeExample implements GameTreeExample {

    /** the root of the game tree. */
    protected TwoPlayerMoveStub initialMove;

    protected MoveCreator moveCreator;

    public AbstractGameTreeExample(EvaluationPerspective persp) {
        moveCreator = new MoveCreator(persp);
    }

    protected MoveList createList(TwoPlayerMoveStub... moves) {
        MoveList moveList = new MoveList();
        moveList.addAll(Arrays.asList(moves));
        return moveList;
    }

    @Override
    public TwoPlayerMove getInitialMove() {
        return initialMove;
    }

    /**
     * Print the tree in depth first search for debugging purposes
     */
    @Override
    public void print() {
        initialMove.print();
    }
}
