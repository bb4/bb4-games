// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis.differencers;

import com.barrybecker4.game.twoplayer.gomoku.GoMokuBoard;
import com.barrybecker4.game.twoplayer.gomoku.analysis.Direction;
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns;
import org.junit.Test;


/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
public class DownDiagonalDifferencerTest extends ValueDifferencerTst  {

    @Override
    protected ValueDifferencer createDifferencer(GoMokuBoard board, Patterns patterns) {
        return differencerFactory.createValueDifferencer(board, Direction.DOWN_DIAGONAL);
    }

    @Test
    public void testBlank() {
        verifyLine(2, 1, "___XO_");
    }

    @Test
    public void testLeftEdge() {
        verifyLine(4, 1, "_OXOO_");
    }

    @Test
    public void testBottomLeftCorner() {
        verifyLine(10, 1, "_");
    }

    @Test
    public void testBottomRightCorner() {
        verifyLine(10, 8, "_X_O__");
    }

    @Test
    public void testTopLeftCorner() {
        verifyLine(1, 1, "____X_");
    }

    @Test
    public void testTopRightCorner() {
        verifyLine(1, 8, "_");
    }

    @Test
    public void testPlayer1() {
        verifyLine(1, 4, "X_O__");
    }

    @Test
    public void testPlayer2() {
        verifyLine(2, 4, "_O__X_");
    }

    @Test
    public void testPlayer2Single() {
        verifyLine(5, 2, "_OXOO_O");
    }

    @Test
    public void testPlayer1Single() {
        verifyLine(9, 2, "_X_");
    }

    @Test
    public void testPlayer1Surrounded() {
        verifyLine(6, 3, "_OXOO_O");
    }

    @Test
    public void testPlayer2Surrounded() {
        verifyLine(7, 4, "_OXOO_O");
    }

    @Test
    public void testPlayer2Edge() {
        verifyLine(10, 7, "OXOO_O");
    }

    @Test
    public void testPlayer2AmongFriends() {
        verifyLine(8, 7, "__XO_O_");
    }
}