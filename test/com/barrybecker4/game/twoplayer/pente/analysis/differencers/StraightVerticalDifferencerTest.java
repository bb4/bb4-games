// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente.analysis.differencers;

import com.barrybecker4.game.twoplayer.pente.PenteBoard;
import com.barrybecker4.game.twoplayer.pente.analysis.Direction;
import com.barrybecker4.game.twoplayer.pente.pattern.Patterns;
import org.junit.Test;


/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
public class StraightVerticalDifferencerTest extends ValueDifferencerTst  {

    @Override
    protected ValueDifferencer createDifferencer(PenteBoard board, Patterns patterns) {
        return differencerFactory.createValueDifferencer(board, Direction.VERTICAL);
    }

    @Test
    public void testBlank() {
        verifyLine(4, 1, "_________");
    }

    @Test
    public void testBottomLeftCorner() {
        verifyLine(10, 1, "______");
    }

    @Test
    public void testBottomRightCorner() {
        verifyLine(10, 8, "______");
    }

    @Test
    public void testTopLeftCorner() {
        verifyLine(1, 1, "______");
    }

    @Test
    public void testTopRightCorner() {
        verifyLine(1, 8, "______");
    }

    @Test
    public void testPlayer1() {
        verifyLine(1, 4, "XOX_XX");
    }

    @Test
    public void testPlayer2() {
        verifyLine(2, 4, "XOX_XXO");
    }

    @Test
    public void testPlayer2Single() {
        verifyLine(5, 2, "____O___X_");
    }

    @Test
    public void testPlayer1Single() {
        verifyLine(9, 2, "_O___X_");
    }

    @Test
    public void testPlayer1Surrounded() {
        verifyLine(3, 4, "XOX_XXO_");
    }

    @Test
    public void testPlayer2Surrounded() {
        verifyLine(2, 4, "XOX_XXO");
    }

    @Test
    public void testPlayer2Edge() {
        verifyLine(10, 7, "X__O_O");
    }

    @Test
    public void testPlayer2AmongFriends() {
        verifyLine(8, 7, "O_X__O_O");
    }
}