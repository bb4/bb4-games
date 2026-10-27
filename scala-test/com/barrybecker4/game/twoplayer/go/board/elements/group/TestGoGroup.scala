/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.group

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
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

        Assert.assertFalse(group.containsStone(new GoBoardPosition(3, 3, null, new GoStone(true, 0.5f))))
        Assert.assertTrue(group.containsStone(stone))
        Assert.assertEquals("Unexpected bounding box.",
               new Box(new ByteLocation(4, 4), new ByteLocation(4, 4)), group.findBoundingBox())
        Assert.assertEquals("Unexpected number of liberties", 4, group.getNumLiberties(board))
        Assert.assertTrue(group.isOwnedByPlayer1)
    }

}
