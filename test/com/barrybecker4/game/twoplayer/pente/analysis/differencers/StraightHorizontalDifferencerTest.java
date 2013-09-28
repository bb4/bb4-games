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
public class StraightHorizontalDifferencerTest extends ValueDifferencerTst  {

    @Override
    protected ValueDifferencer createDifferencer(PenteBoard board, Patterns patterns) {
        return differencerFactory.createValueDifferencer(board, Direction.HORIZONTAL);
    }

    @Test
    public void testBlank() {
        verifyLine(4, 1, "______");
    }

    @Test
    public void testBottomLeftCorner() {
        verifyLine(10, 1, "______");
    }

    @Test
    public void testBottomRightCorner() {
        verifyLine(10, 8, "____O_");
    }

    @Test
    public void testTopLeftCorner() {
        verifyLine(1, 1, "___X__");
    }

    @Test
    public void testTopRightCorner() {
        verifyLine(1, 8, "_X____");
    }

    @Test
    public void testPlayer1() {
        verifyLine(1, 4, "___X____");
    }

    @Test
    public void testPlayer2() {
        verifyLine(2, 4, "___O____");
    }

    @Test
    public void testPlayer2Single() {
        verifyLine(5, 2, "_O_XXXX");
    }

    @Test
    public void testPlayer1Single() {
        verifyLine(9, 2, "_X_____");
    }

    @Test
    public void testPlayer1Surrounded() {
        verifyLine(3, 4, "___X_OO_");
    }

    @Test
    public void testPlayer2Surrounded() {
        verifyLine(6, 5, "__XXO___");
    }

    @Test
    public void testPlayer2Edge() {
        verifyLine(10, 7, "_____O_");
    }

    @Test
    public void testPlayer2AmongFriends() {
        verifyLine(8, 6, "__X_OOO_");
    }
}