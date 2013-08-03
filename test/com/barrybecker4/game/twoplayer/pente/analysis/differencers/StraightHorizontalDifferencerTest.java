// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente.analysis.differencers;

import com.barrybecker4.game.twoplayer.pente.PenteBoard;
import com.barrybecker4.game.twoplayer.pente.analysis.Direction;
import com.barrybecker4.game.twoplayer.pente.pattern.Patterns;

/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
public class StraightHorizontalDifferencerTest extends ValueDifferencerTst  {

    @Override
    protected ValueDifferencer createDifferencer(PenteBoard board, Patterns patterns) {
        return differencerFactory.createValueDifferencer(Direction.HORIZONTAL);
    }


    public void testBlank() {
        verifyLine(4, 1, "______");
    }


    public void testBottomLeftCorner() {
        verifyLine(10, 1, "______");
    }

    public void testBottomRightCorner() {
        verifyLine(10, 8, "____O_");
    }

    public void testTopLeftCorner() {
        verifyLine(1, 1, "___X__");
    }

    public void testTopRightCorner() {
        verifyLine(1, 8, "_X____");
    }


    public void testPlayer1() {
        verifyLine(1, 4, "___X____");
    }

    public void testPlayer2() {
        verifyLine(2, 4, "___O____");
    }

    public void testPlayer2Single() {
        verifyLine(5, 2, "_O_XXXX");
    }

    public void testPlayer1Single() {
        verifyLine(9, 2, "_X_____");
    }

    public void testPlayer1Surrounded() {
        verifyLine(3, 4, "___X_OO_");
    }

    public void testPlayer2Surrounded() {
        verifyLine(6, 5, "__XXO___");
    }

    public void testPlayer2Edge() {
        verifyLine(10, 7, "_____O_");
    }

    public void testPlayer2AmongFriends() {
        verifyLine(8, 6, "__X_OOO_");
    }
}