/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition

class TestStringShapeAnalyzer extends GoTestCase {



    @Test
    def testShape1(): Unit = {
        restore("shape/problem_shape1")

        checkShape(4, 4, 0)
        checkShape(9, 4, 0)

        checkShape(10, 8, 1)
        checkShape(11, 8, 1)
        checkShape(11, 9, 1)
    }

    @Test
    def testShape2(): Unit = {
            restore("shape/problem_shape1")

            checkShape(4, 9, 3)
            checkShape(5, 9, 6)
            checkShape(5, 10, 6)
            checkShape(6, 10, 3)
        }

    private def checkShape(r: Int, c: Int, expectedShapeScore: Int): Unit = {
        var board: GoBoard = getBoard
        var position: GoBoardPosition = board.getPosition(r, c).asInstanceOf[GoBoardPosition]
        var sa: StringShapeAnalyzer = new StringShapeAnalyzer(board)
        var n: Int = sa.formsBadShape(position)
        Assert.assertTrue("Expected "+expectedShapeScore+" but got "+n+" for "+position, n == expectedShapeScore)
    }

}
