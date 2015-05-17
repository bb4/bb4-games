/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.examples;

import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;


/**
 * A simple game tree for testing quiescent search to a maximum depth.
 * cu subscript = cause urgency
 * u subscript indicates an urgent move.
 *
 *                 ____  [6]  _____
 *                /                \
 *            [-8]                  [-2]          // 1
 *         /      \                /     \
 *     [-1cu]     [7]           [8cu]    [2]      // 2
 *    /    \  \                 /    \
 *  [3u]  [2u][1]            [4u]   [5cu]         // 3
 *                                 /  \  \
 *                              [-5cu][6][4cu]    // 4
 *                               /
 *                             [5cu]              // 5
 *                             /    \
 *                          [-6cu]  [-7u]         // 6
 *                          /
 *                        [8cu]                   // 7
 *
 * Move scores are evaluated from player one's perspective.
 * @author Barry Becker
 */
public class LadderQuiescentExample extends AbstractGameTreeExample  {


    public LadderQuiescentExample(boolean player1PlaysNext) {

        initialMove = moveCreator.createMove(6, !player1PlaysNext, null);

        // first ply
        TwoPlayerMoveStub move0 = moveCreator.createMove(-8, player1PlaysNext, initialMove);
        TwoPlayerMoveStub move1 = moveCreator.createMove(-2, player1PlaysNext, initialMove);

        // second ply
        TwoPlayerMoveStub move00 = moveCreator.createMove(-1, !player1PlaysNext, move0);
        move00.setCausedUrgency(true);
        TwoPlayerMoveStub move01 = moveCreator.createMove(7, !player1PlaysNext, move0);

        TwoPlayerMoveStub move10 = moveCreator.createMove(8, !player1PlaysNext, move1);
        move10.setCausedUrgency(true);
        TwoPlayerMoveStub move11 = moveCreator.createMove(2, !player1PlaysNext, move1);

        // third ply
        TwoPlayerMoveStub move000 = moveCreator.createMove(3, player1PlaysNext, move00);
        move000.setUrgent(true);
        TwoPlayerMoveStub move001 = moveCreator.createMove(2, player1PlaysNext, move00);
        move001.setUrgent(true);
        TwoPlayerMoveStub move002 = moveCreator.createMove(1, player1PlaysNext, move00);

        TwoPlayerMoveStub move100 = moveCreator.createMove(4, player1PlaysNext, move10);
        move100.setUrgent(true);
        TwoPlayerMoveStub move101 = moveCreator.createMove(5, player1PlaysNext, move10);
        move101.setCausedUrgency(true);
        move101.setUrgent(true);

        // fourth ply
        TwoPlayerMoveStub move1010 = moveCreator.createMove(-5, !player1PlaysNext, move101);
        move1010.setCausedUrgency(true);
        move1010.setUrgent(true);
        TwoPlayerMoveStub move1011 = moveCreator.createMove(6, !player1PlaysNext, move101);
        TwoPlayerMoveStub move1012 = moveCreator.createMove(4, !player1PlaysNext, move101);
        move1012.setCausedUrgency(true);
        move1012.setUrgent(true);

        // fifth ply
        TwoPlayerMoveStub move10100 = moveCreator.createMove(5, player1PlaysNext, move1010);
        move10100.setCausedUrgency(true);
        move10100.setUrgent(true);

        // sixth ply
        TwoPlayerMoveStub move101000 = moveCreator.createMove(-6, !player1PlaysNext, move10100);
        move101000.setCausedUrgency(true);
        move101000.setUrgent(true);

        TwoPlayerMoveStub move101001 = moveCreator.createMove(-7, !player1PlaysNext, move10100);
        move101001.setUrgent(true);

        // seventh ply
        TwoPlayerMoveStub move1010000 = moveCreator.createMove(8, player1PlaysNext, move101000);
        move1010000.setCausedUrgency(true);
        move1010000.setUrgent(true);

        // parenting
        initialMove.setChildren(createList(move0, move1));

        move0.setChildren(createList(move00, move01));
        move1.setChildren(createList(move10, move11));

        move00.setChildren(createList(move000, move001, move002));
        move10.setChildren(createList(move100, move101));

        move101.setChildren(createList(move1010, move1011, move1012));
        move1010.setChildren(createList(move10100));
        move10100.setChildren(createList(move101000, move101001));
        move101000.setChildren(createList(move1010000));
    }
}
