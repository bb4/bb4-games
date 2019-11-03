/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.string;

import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;


/**
 * @author Barry Becker
 */
public class TestGoString extends GoTestCase {


    public void testStringConstruction() {

        GoBoardPosition stone = new GoBoardPosition(4, 4, null, new GoStone(true, 0.5f));
        GoBoard board = new GoBoard(9, 0);
        GoString string = new GoString(stone, board);

        assertFalse(string.isUnconditionallyAlive());
        assertFalse(string.areAnyBlank());
        assertEquals("Unexpected number of liberties", 4, string.getNumLiberties(board));
        assertTrue(string.isOwnedByPlayer1());
    }


    public void testStringLiberties() {
        GoBoardPosition stone = new GoBoardPosition(4, 4, null, new GoStone(true, 0.5f));
        GoBoard board = new GoBoard(9, 0);
        GoString string = new GoString(stone, board);

        assertEquals("Unexpected number fo liberties", 4, string.getNumLiberties(board));
    }
}
