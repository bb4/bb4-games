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
public class StraightVerticalDifferencerTest extends ValueDifferencerTst  {

    @Override
    protected ValueDifferencer createDifferencer(PenteBoard board, Patterns patterns) {
        return differencerFactory.createValueDifferencer(Direction.VERTICAL);
    }


    public void testBlank() {
        verifyLine(4, 1, "_________");
    }


    public void testBottomLeftCorner() {
        verifyLine(10, 1, "______");
    }

    public void testBottomRightCorner() {
        verifyLine(10, 8, "______");
    }

    public void testTopLeftCorner() {
        verifyLine(1, 1, "______");
    }

    public void testTopRightCorner() {
        verifyLine(1, 8, "______");
    }


    public void testPlayer1() {
        verifyLine(1, 4, "XOX_XX");
    }

    public void testPlayer2() {
        verifyLine(2, 4, "XOX_XXO");
    }

    public void testPlayer2Single() {
        verifyLine(5, 2, "____O___X_");
    }

    public void testPlayer1Single() {
        verifyLine(9, 2, "_O___X_");
    }

    public void testPlayer1Surrounded() {
        verifyLine(3, 4, "XOX_XXO_");
    }

    public void testPlayer2Surrounded() {
        verifyLine(2, 4, "XOX_XXO");
    }

    public void testPlayer2Edge() {
        verifyLine(10, 7, "X__O_O");
    }

    public void testPlayer2AmongFriends() {
        verifyLine(8, 7, "O_X__O_O");
    }
}