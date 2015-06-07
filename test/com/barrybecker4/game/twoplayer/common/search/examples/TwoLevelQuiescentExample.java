/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.examples;

import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;


/**
 * A simple game tree for testing search strategies.
 * It looks something like this.
 * cu subscipt = cause urgency
 * u subscript indicates an urgetn move.
 *
 *                 ____ [6] ____
 *                /             \
 *            [-8]              [-2]
 *          /     \            /     \
 *     [-1cu]     [7]       [8cu]   [2]
 *    /    \  \              /        \
 *  [3u]  [2u][1]          [4u]      [5cu]
 *                                  /  \  \
 *                               [5u]  [6][4u]
 *
 * Move scores are evaluated from player one's perspective.
 * @author Barry Becker
 */
public class TwoLevelQuiescentExample extends AbstractGameTreeExample  {


    public TwoLevelQuiescentExample(boolean player1PlaysNext) {

        initialMove = moveCreator.createMove(6, !player1PlaysNext, null);

        // first ply
        TwoPlayerMoveStub move0 = moveCreator.createMove(-8, player1PlaysNext, initialMove);
        TwoPlayerMoveStub move1 = moveCreator.createMove(-2, player1PlaysNext, initialMove);

        // second ply
        TwoPlayerMoveStub move00 = moveCreator.createMove(-1, !player1PlaysNext, move0);
        move00.setCausesUrgency(true);
        TwoPlayerMoveStub move01 = moveCreator.createMove(7, !player1PlaysNext, move0);

        TwoPlayerMoveStub move10 = moveCreator.createMove(8, !player1PlaysNext, move1);
        move10.setCausesUrgency(true);
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
        move101.setCausesUrgency(true);
        move101.setUrgent(true);

        // fourth ply
        TwoPlayerMoveStub move1010 = moveCreator.createMove(5, !player1PlaysNext, move101);
        move1010.setUrgent(true);
        TwoPlayerMoveStub move1011 = moveCreator.createMove(6, !player1PlaysNext, move101);
        TwoPlayerMoveStub move1012 = moveCreator.createMove(4, !player1PlaysNext, move101);
        move1011.setUrgent(true);
    }
}
