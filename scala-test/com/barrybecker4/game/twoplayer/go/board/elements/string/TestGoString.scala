/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.string

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone

class TestGoString extends GoTestCase {



    @Test
    def testStringConstruction(): Unit = {

        var stone: GoBoardPosition = new GoBoardPosition(4, 4, null, new GoStone(true, 0.5f))
        var board: GoBoard = new GoBoard(9, 0)
        var string: GoString = new GoString(stone, board)

        Assertions.assertFalse(string.isUnconditionallyAlive)
        Assertions.assertFalse(string.areAnyBlank)
        Assertions.assertEquals(4, string.getNumLiberties(board), "Unexpected number of liberties")
        Assertions.assertTrue(string.isOwnedByPlayer1)
    }


    @Test
    def testStringLiberties(): Unit = {
        var stone: GoBoardPosition = new GoBoardPosition(4, 4, null, new GoStone(true, 0.5f))
        var board: GoBoard = new GoBoard(9, 0)
        var string: GoString = new GoString(stone, board)

        Assertions.assertEquals(4, string.getNumLiberties(board), "Unexpected number fo liberties")
    }

}
