/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.group;

import com.barrybecker4.common.geometry.Box;
import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;
import com.barrybecker4.game.twoplayer.go.board.elements.string.GoString;


/**
 * @author Barry Becker
 */
public class TestGoGroup extends GoTestCase {

    public void testGroupConstruction() {

        GoBoardPosition stone = new GoBoardPosition(4, 4, null, new GoStone(true, 0.5f));
        GoBoard board = new GoBoard(9, 0);
        GoString string = new GoString(stone, board);
        GoGroup group = new GoGroup(string);

        assertFalse(group.containsStone(new GoBoardPosition(3, 3, null, new GoStone(true, 0.5f))));
        assertTrue(group.containsStone(stone));
        assertEquals("Unexpected bounding box.",
               new Box(new ByteLocation(4, 4), new ByteLocation(4, 4)), group.findBoundingBox());
        assertEquals("Unexpected number of liberties", 4, group.getNumLiberties(board));
        assertTrue(group.isOwnedByPlayer1());
    }
}