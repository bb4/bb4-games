/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.examples;

import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;


/**
 * This example comes from one of the homework problems in Chapter 8 of
 * Artificial Intelligence for Games by Ian Millington and John Funge.
 *
 * It is a binary tree and all the search trees for all the strategies can be derived from
 * just the leaf nodes.
 *  [17][6]  [46][27]  [48][33]  [10][25] [22][1] [14][6] [2][12]  [24][48]
 *
 * Move scores are evaluated from player one's perspective.
 * Player 1 will want to select the left branch in order to have the best chance of winning, and
 * Player 2 will wan the right.
 *
 * @author Barry Becker
 */
public class FourLevelGameTreeExample extends AbstractGameTreeExample  {


    public FourLevelGameTreeExample(boolean player1PlaysNext) {

        initialMove = moveCreator.createMove(6, !player1PlaysNext, null);

        // first ply
        TwoPlayerMoveStub move0 = moveCreator.createMove(-8, player1PlaysNext, initialMove);
        TwoPlayerMoveStub move1 = moveCreator.createMove(-2, player1PlaysNext, initialMove);

        // second ply
        TwoPlayerMoveStub move00 = moveCreator.createMove(-1, !player1PlaysNext, move0);
        TwoPlayerMoveStub move01 = moveCreator.createMove(7, !player1PlaysNext, move0);

        TwoPlayerMoveStub move10 = moveCreator.createMove(8, !player1PlaysNext, move1);
        TwoPlayerMoveStub move11 = moveCreator.createMove(2, !player1PlaysNext, move1);

        // third ply
        TwoPlayerMoveStub move000 = moveCreator.createMove(-5, player1PlaysNext, move00);
        TwoPlayerMoveStub move001 = moveCreator.createMove(-4, player1PlaysNext, move00);

        TwoPlayerMoveStub move010 = moveCreator.createMove(-6, player1PlaysNext, move01);
        TwoPlayerMoveStub move011 = moveCreator.createMove(-2, player1PlaysNext, move01);

        TwoPlayerMoveStub move100 = moveCreator.createMove(-7, player1PlaysNext, move10);
        TwoPlayerMoveStub move101 = moveCreator.createMove(-8, player1PlaysNext, move10);

        TwoPlayerMoveStub move110 = moveCreator.createMove(-4, player1PlaysNext, move11);
        TwoPlayerMoveStub move111 = moveCreator.createMove(-4, player1PlaysNext, move11);

        // fourth ply   (the leaves. These values get inherited)
        TwoPlayerMoveStub move0000 = moveCreator.createMove(17, !player1PlaysNext, move000);
        TwoPlayerMoveStub move0001 = moveCreator.createMove( 6, !player1PlaysNext, move000);

        TwoPlayerMoveStub move0010 = moveCreator.createMove(46, !player1PlaysNext, move001);
        TwoPlayerMoveStub move0011 = moveCreator.createMove(27, !player1PlaysNext, move001);

        TwoPlayerMoveStub move0100 = moveCreator.createMove(48, !player1PlaysNext, move010);
        TwoPlayerMoveStub move0101 = moveCreator.createMove(33, !player1PlaysNext, move010);

        TwoPlayerMoveStub move0110 = moveCreator.createMove(10, !player1PlaysNext, move011);
        TwoPlayerMoveStub move0111 = moveCreator.createMove(25, !player1PlaysNext, move011);

        TwoPlayerMoveStub move1000 = moveCreator.createMove(22, !player1PlaysNext, move100);
        TwoPlayerMoveStub move1001 = moveCreator.createMove( 1, !player1PlaysNext, move100);

        TwoPlayerMoveStub move1010 = moveCreator.createMove(14, !player1PlaysNext, move101);
        TwoPlayerMoveStub move1011 = moveCreator.createMove( 6, !player1PlaysNext, move101);

        TwoPlayerMoveStub move1100 = moveCreator.createMove( 2, !player1PlaysNext, move110);
        TwoPlayerMoveStub move1101 = moveCreator.createMove(12, !player1PlaysNext, move110);

        TwoPlayerMoveStub move1110 = moveCreator.createMove(24, !player1PlaysNext, move111);
        TwoPlayerMoveStub move1111 = moveCreator.createMove(48, !player1PlaysNext, move111);

        initialMove.setChildren(createList(move0, move1));

        // ply 1 children
        move0.setChildren(createList(move00, move01));
        move1.setChildren(createList(move10, move11));

        // ply 2 children
        move00.setChildren(createList(move000, move001));
        move01.setChildren(createList(move010, move011));

        move10.setChildren(createList(move100, move101));
        move11.setChildren(createList(move110, move111));

        // ply 3 children
        move000.setChildren(createList(move0000, move0001));
        move001.setChildren(createList(move0010, move0011));

        move010.setChildren(createList(move0100, move0101));
        move011.setChildren(createList(move0110, move0111));

        move100.setChildren(createList(move1000, move1001));
        move101.setChildren(createList(move1010, move1011));

        move110.setChildren(createList(move1100, move1101));
        move111.setChildren(createList(move1110, move1111));
    }
}
