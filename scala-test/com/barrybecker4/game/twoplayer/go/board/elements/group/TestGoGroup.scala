/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.group

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import com.barrybecker4.common.geometry.Box
import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import com.barrybecker4.game.twoplayer.go.board.elements.string.GoString

class TestGoGroup extends GoTestCase {


    @Test
    def testGroupConstruction(): Unit = {

        var stone: GoBoardPosition = new GoBoardPosition(4, 4, null, new GoStone(true, 0.5f))
        var board: GoBoard = new GoBoard(9, 0)
        var string: GoString = new GoString(stone, board)
        var group: GoGroup = new GoGroup(string)

        Assertions.assertFalse(group.containsStone(new GoBoardPosition(3, 3, null, new GoStone(true, 0.5f))))
        Assertions.assertTrue(group.containsStone(stone))
        Assertions.assertEquals(new Box(new ByteLocation(4, 4), new ByteLocation(4, 4)), group.findBoundingBox(), "Unexpected bounding box.")
        Assertions.assertEquals(4, group.getNumLiberties(board), "Unexpected number of liberties")
        Assertions.assertTrue(group.isOwnedByPlayer1)
    }

}
