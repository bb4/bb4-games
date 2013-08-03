/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.examples;

import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;


/**
 * A simple game tree for testing search strategies.
 * It looks something like this
 *                 ____   [6]  _____
 *                /                \
 *            [-8]                  [-2]
 *         /      \               /      \
 *     [-1]       [7]           [8]       [2]
 *    /   \      /   \         /  \       /  \
 *  [-5] [-4]  [-6] [-2]    [-7] [-8]  [-4] [-4]
 *
 * Move scores are evaluated from player one's perspective.
 * Some search algorithms evaluate from the current players perspective.
 * This example comes from page 688 in AI for Games by Millington and Funge
 * Internal nodes intentionally do not match what is in the book because the book shows
 * inherited values.
 *
 * @author Barry Becker
 */
public class ThreeLevelGameTreeExample extends AbstractGameTreeExample  {


    public ThreeLevelGameTreeExample(boolean player1PlaysNext, EvaluationPerspective persp) {

        super(persp);

        initialMove = moveCreator.createMove(6, !player1PlaysNext, null);

        // first ply
        TwoPlayerMoveStub move0 = moveCreator.createMove(-8, player1PlaysNext, initialMove);
        TwoPlayerMoveStub move1 = moveCreator.createMove(-2, player1PlaysNext, initialMove);

        // second ply
        TwoPlayerMoveStub move00 = moveCreator.createMove(-1, !player1PlaysNext, move0);
        TwoPlayerMoveStub move01 = moveCreator.createMove(7, !player1PlaysNext, move0);

        TwoPlayerMoveStub move10 = moveCreator.createMove(8, !player1PlaysNext, move1);
        TwoPlayerMoveStub move11 = moveCreator.createMove(2, !player1PlaysNext, move1);

        // third ply   (the leaves. These values get inherited)
        TwoPlayerMoveStub move000 = moveCreator.createMove(-5, player1PlaysNext, move00);
        TwoPlayerMoveStub move001 = moveCreator.createMove(-4, player1PlaysNext, move00);

        TwoPlayerMoveStub move010 = moveCreator.createMove(-6, player1PlaysNext, move01);
        TwoPlayerMoveStub move011 = moveCreator.createMove(-2, player1PlaysNext, move01);

        TwoPlayerMoveStub move100 = moveCreator.createMove(-7, player1PlaysNext, move10);
        TwoPlayerMoveStub move101 = moveCreator.createMove(-8, player1PlaysNext, move10);

        TwoPlayerMoveStub move110 = moveCreator.createMove(-4, player1PlaysNext, move11);
        TwoPlayerMoveStub move111 = moveCreator.createMove(-4, player1PlaysNext, move11);


        initialMove.setChildren(createList(move0, move1));

        move0.setChildren(createList(move00, move01));
        move1.setChildren(createList(move10, move11));

        move00.setChildren(createList(move000, move001));
        move01.setChildren(createList(move010, move011));

        move10.setChildren(createList(move100, move101));
        move11.setChildren(createList(move110, move111));
    }
}
