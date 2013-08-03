/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.examples;

import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;

/**
 * A simple game tree where one of the moves should get alpha pruned when alpha beta pruning is on.
 * Player1 to move next.
 *
 *        7
 *       /  \
 *      3    2
 *     / \  /  \
 *    5  9  4  3
 *
 * @author Barry Becker
 */
public class AlphaPruneExample extends AbstractGameTreeExample  {

    public AlphaPruneExample(boolean player1PlaysNext, EvaluationPerspective persp) {
        super(persp);

        initialMove = moveCreator.createMove(7, !player1PlaysNext, null);

        // first ply
        TwoPlayerMoveStub move0 = moveCreator.createMove(3, player1PlaysNext, initialMove);

        TwoPlayerMoveStub move1 = moveCreator.createMove(2, player1PlaysNext, initialMove);

        // second ply
        TwoPlayerMoveStub move00 = moveCreator.createMove(5, !player1PlaysNext, move0);
        TwoPlayerMoveStub move01 = moveCreator.createMove(9, !player1PlaysNext, move0);

        TwoPlayerMoveStub move10 = moveCreator.createMove(4, !player1PlaysNext, move1);
        // this should get alpha pruned when player1 is to play
        TwoPlayerMoveStub move11 = moveCreator.createMove(3, !player1PlaysNext, move1);


        initialMove.setChildren(createList(move0, move1));

        move0.setChildren(createList(move00, move01));
        move1.setChildren(createList(move10, move11));
    }
}
