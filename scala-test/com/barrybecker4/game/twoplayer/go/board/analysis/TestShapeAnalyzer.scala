/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition

class TestShapeAnalyzer extends GoTestCase {


    private val PREFIX: String = "board/badshape/"

    @Test
    def testBadShape1(): Unit = {
        verifyBadShape("badShape1", 4, 4, 3)
    }

    @Test
    def testBadShape2(): Unit = {
        verifyBadShape("badShape2", 4, 4, 1)
    }

    @Test
    def testBadShape3(): Unit = {
        verifyBadShape("badShape3", 4, 4, 8)
    }

    def verifyBadShape(file: String, row: Int, col: Int, expected: Int): Unit = {
        restore(PREFIX + file)

        var board: GoBoard = getBoard
        var pos: GoBoardPosition = board.getPosition(row, col).asInstanceOf[GoBoardPosition]
        var sa: StringShapeAnalyzer = new StringShapeAnalyzer(board)
        var badShapeScore: Int = sa.formsBadShape(pos)
        Assertions.assertTrue(badShapeScore == expected, "badShapeScore="+badShapeScore+" expected="+expected)
    }


}
