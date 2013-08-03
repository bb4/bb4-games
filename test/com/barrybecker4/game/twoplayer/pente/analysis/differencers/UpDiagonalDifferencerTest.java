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
public class UpDiagonalDifferencerTest extends ValueDifferencerTst  {

    @Override
    protected ValueDifferencer createDifferencer(PenteBoard board, Patterns patterns) {
        return differencerFactory.createValueDifferencer(Direction.UP_DIAGONAL);
    }

    public void testBlank() {
        verifyLine(3, 1, "___");
    }

    public void testLeftSide() {
        verifyLine(4, 1, "___X");
    }


    public void testBottomLeftCorner() {
        verifyLine(10, 1, "_XXOOX");
    }

    public void testBottomRightCorner() {
        verifyLine(10, 8, "_");
    }

    public void testTopLeftCorner() {
        verifyLine(1, 1, "_");
    }

    public void testTopRightCorner() {
        verifyLine(1, 8, "XX_O__");
    }


    public void testTopSide() {
        verifyLine(1, 4, "___X");
    }

    public void testPlayer1() {
        verifyLine(5, 5, "___XX_O_");
    }

    public void testPlayer2() {
        verifyLine(5, 2, "_O_X__");
    }

    public void testPlayer2Single() {
        verifyLine(2, 4, "___O_");
    }

    public void testPlayer1Single() {
        verifyLine(9, 2, "_XXOOX_");
    }

    public void testPlayer1Surrounded() {
        verifyLine(5, 4, "__XX_O__");
    }

    public void testPlayer2Surrounded() {
        verifyLine(7, 4, "_XXOOX__");
    }

    public void testPlayer2Edge() {
        verifyLine(10, 7, "O_");
    }

    public void testPlayer2AmongFriends() {
        verifyLine(8, 7, "__O_");
    }
}