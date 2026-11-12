/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import com.barrybecker4.game.twoplayer.go.board.move.GoMove

class TestPostRemoveUpdater extends GoTestCase {


    private var captureCounts: CaptureCounts = new CaptureCounts()

    @Test
    def testTrivialRemove(): Unit = {

        var board: GoBoard = new GoBoard(5, 0)

        var removeUpdater: PostRemoveUpdater = new PostRemoveUpdater(board, captureCounts)

        // a black move on a virgin board.
        var location: Location = new ByteLocation(2, 2)
        var move: GoMove = new GoMove(location, 0, new GoStone(true))
        board.makeMove(move)  // the board already has its own update. Should we use that or inject?
        removeUpdater.update(move)

        var pos: GoBoardPosition = board.getPosition(location).asInstanceOf[GoBoardPosition]
        Assertions.assertEquals(0, captureCounts.getNumCaptures(true), "Unexpected captures")
        Assertions.assertNull(pos.getString, "Unexpected string at now empty position")
        Assertions.assertEquals(0, board.getGroups.size, "Unexpected number of groups")
    }


}
