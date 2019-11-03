/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;


/**
 * @author Barry Becker
 */
public class TestPostRemoveUpdater extends GoTestCase {

    private CaptureCounts captureCounts  = new CaptureCounts();

    public void testTrivialRemove() {

        GoBoard board = new GoBoard(5, 0);

        PostRemoveUpdater removeUpdater = new PostRemoveUpdater(board, captureCounts);

        // a black move on a virgin board.
        Location location = new ByteLocation(2, 2);
        GoMove move = new GoMove(location, 0, new GoStone(true));
        board.makeMove(move);  // the board already has its own update. Should we use that or inject?
        removeUpdater.update(move);

        GoBoardPosition pos = (GoBoardPosition) board.getPosition(location);
        assertEquals("Unexpected captures", 0, captureCounts.getNumCaptures(true));
        assertNull("Unexpected string at now empty position", pos.getString());
        assertEquals("Unexpected number of groups", 0, board.getGroups().size());
    }

}
