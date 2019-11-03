/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy.integration;

import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.Progress;

/**
 * The expected moves for a given game for each combination of game progress and player.
 * @author Barry Becker
 */
public class ExpectedMoveMatrix {

    private MoveInfo beginningP1;
    private MoveInfo beginningP2;
    private MoveInfo middleP1;
    private MoveInfo middleP2;
    private MoveInfo endP1;
    private MoveInfo endP2;

    public ExpectedMoveMatrix(MoveInfo beginningPlayer1, MoveInfo beginningPlayer2,
                              MoveInfo middlePlayer1, MoveInfo middlePlayer2,
                              MoveInfo endPlayer1, MoveInfo endPlayer2) {
        beginningP1 = beginningPlayer1;
        beginningP2 = beginningPlayer2;
        middleP1 = middlePlayer1;
        middleP2 = middlePlayer2;
        endP1 = endPlayer1;
        endP2 = endPlayer2;
    }

    /**
     * Use this constructor if you do not have the numMovesConsidered info to include.
     */
    public ExpectedMoveMatrix(TwoPlayerMove beginningPlayer1, TwoPlayerMove beginningPlayer2,
                              TwoPlayerMove middlePlayer1, TwoPlayerMove middlePlayer2,
                              TwoPlayerMove endPlayer1, TwoPlayerMove endPlayer2) {
        beginningP1 = new MoveInfo(beginningPlayer1);
        beginningP2 = new MoveInfo(beginningPlayer2);
        middleP1 = new MoveInfo(middlePlayer1);
        middleP2 = new MoveInfo(middlePlayer2);
        endP1 = new MoveInfo(endPlayer1);
        endP2 = new MoveInfo(endPlayer2);
    }


    public MoveInfo getExpectedMove(Progress progress, boolean player1) {
         MoveInfo expectedMove = null;
         switch (progress) {
            case BEGINNING :
                expectedMove = player1 ?  beginningP1 : beginningP2;
                break;
            case MIDDLE :
                expectedMove = player1 ?  middleP1 : middleP2;
                break;
            case END :
                expectedMove = player1 ?  endP1 : endP2;
                break;
        }
        return expectedMove;
     }
}
