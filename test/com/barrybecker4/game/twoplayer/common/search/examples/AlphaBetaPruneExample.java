// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.common.search.examples;

import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;

/**
 * A game tree where some of the moves should get alpha or beta pruned when alpha beta pruning is on.
 * See http://cs.ucla.edu/~rosen/161/notes/alphabeta.html for a very detailed explanation.
 *
 *  the tree is defined by
 *                                     10
 *                      1                            11(p)
 *               9           -8(p)                23          12
 *        5        4(p)     5      6           3(p)   4         6
 *   (((3 17)   (2 12))  ((15)  (25 0)))    (((2 5) (3))     ((2 14)))
 *
 *  Should return 3 with 6 terminal nodes examined.
 *
 * @author Barry Becker
 */
public class AlphaBetaPruneExample extends AbstractGameTreeExample  {

    public AlphaBetaPruneExample(boolean player1PlaysNext, EvaluationPerspective persp) {
        super(persp);

        initialMove = moveCreator.createMove(10, !player1PlaysNext, null);

        // first ply
        TwoPlayerMoveStub move0 = moveCreator.createMove(1, player1PlaysNext, initialMove);
        TwoPlayerMoveStub move1 = moveCreator.createMove(11, player1PlaysNext, initialMove);

        // second ply
        TwoPlayerMoveStub move00 = moveCreator.createMove(9, !player1PlaysNext, move0);
        // right child will be pruned
        TwoPlayerMoveStub move01 = moveCreator.createMove(-8, !player1PlaysNext, move0);

        TwoPlayerMoveStub move10 = moveCreator.createMove(23, !player1PlaysNext, move1);
        TwoPlayerMoveStub move11 = moveCreator.createMove(12, !player1PlaysNext, move1);


        // third ply
        TwoPlayerMoveStub move000 = moveCreator.createMove(5, player1PlaysNext, move00);
        TwoPlayerMoveStub move001 = moveCreator.createMove(4, player1PlaysNext, move00);

        TwoPlayerMoveStub move010 = moveCreator.createMove(5, player1PlaysNext, move01);
        TwoPlayerMoveStub move011 = moveCreator.createMove(6, player1PlaysNext, move01);

        TwoPlayerMoveStub move100 = moveCreator.createMove(3, player1PlaysNext, move10);
        TwoPlayerMoveStub move101 = moveCreator.createMove(4, player1PlaysNext, move10);

        TwoPlayerMoveStub move111 = moveCreator.createMove(12, player1PlaysNext, move11);

        // fourth ply (leaves)
        TwoPlayerMoveStub move0000 = moveCreator.createMove(3, !player1PlaysNext, move000);
        TwoPlayerMoveStub move0001 = moveCreator.createMove(17, !player1PlaysNext, move000);

        TwoPlayerMoveStub move0010 = moveCreator.createMove(2, !player1PlaysNext, move001);
        TwoPlayerMoveStub move0011 = moveCreator.createMove(12, !player1PlaysNext, move001);

        TwoPlayerMoveStub move0100 = moveCreator.createMove(15, !player1PlaysNext, move010);

        TwoPlayerMoveStub move0110 = moveCreator.createMove(25, !player1PlaysNext, move011);
        TwoPlayerMoveStub move0111 = moveCreator.createMove(0, !player1PlaysNext, move011);


        TwoPlayerMoveStub move1000 = moveCreator.createMove(2, !player1PlaysNext, move100);
        TwoPlayerMoveStub move1001 = moveCreator.createMove(5, !player1PlaysNext, move100);

        TwoPlayerMoveStub move1010 = moveCreator.createMove(3, !player1PlaysNext, move101);

        TwoPlayerMoveStub move1110 = moveCreator.createMove(2, !player1PlaysNext, move111);
        TwoPlayerMoveStub move1111 = moveCreator.createMove(14, !player1PlaysNext, move111);


        initialMove.setChildren(createList(move0, move1));

        // ply 1 children
        move0.setChildren(createList(move00, move01));
        move1.setChildren(createList(move10, move11));

        // ply 2 children
        move00.setChildren(createList(move000, move001));
        move01.setChildren(createList(move010, move011));
        move10.setChildren(createList(move100, move101));

        // ply 3 children
        move000.setChildren(createList(move0000, move0001));
        move001.setChildren(createList(move0010, move0011));
        move011.setChildren(createList(move0110, move0111));
        move100.setChildren(createList(move1000, move1001));
        move111.setChildren(createList(move1110, move1111));
    }
}
