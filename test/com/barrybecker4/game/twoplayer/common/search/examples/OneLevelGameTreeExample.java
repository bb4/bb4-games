/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.examples;

import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;


/**
 * A simple game tree for testing search strategies.
 * It looks something like this
 *                 ___[6]____
 *                /          \
 *             [-8]         [-2]
 *
 * Move scores are evaluated from player one's perspective.
 * So for this game, if player 1 is going first, he would bick the node on the right, even though it
 * is suspected to be a losing move (less than 0), but its better than the one on the left.
 *
 * @author Barry Becker
 */
public class OneLevelGameTreeExample extends AbstractGameTreeExample  {


    public OneLevelGameTreeExample(boolean player1PlaysNext) {

        initialMove = moveCreator.createMove(6, !player1PlaysNext, null);

        // first ply
        TwoPlayerMoveStub move0 = moveCreator.createMove(-8, player1PlaysNext, initialMove);
        TwoPlayerMoveStub move1 = moveCreator.createMove(-2, player1PlaysNext, initialMove);

        initialMove.setChildren(createList(move0, move1));
    }
}
