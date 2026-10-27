/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.neighbor

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionList

class TestNeighborAnalyzer extends GoTestCase {


    private val PREFIX: String = "board/analysis/neighbor/"

    /** instance under test */
    private var nbrAnalyzer_ : NeighborAnalyzer = scala.compiletime.uninitialized

    // test group neighbor detection
    @Test
    def testGetGroupNbrs1(): Unit = {
        verifyGroupNbrs("groupNbr1", 3, 3, 1, 1)
    }

    @Test
    def testGetGroupNbrs2(): Unit = {
        verifyGroupNbrs("groupNbr2", 4, 4, 6, 8)
    }

    @Test
    def testGetGroupNbrs3(): Unit = {
        verifyGroupNbrs("groupNbr3", 5, 4, 1, 1)
     }

    @Test
    def testGetGroupNbrs4(): Unit = {
        verifyGroupNbrs("groupNbr4", 4, 4, 5, 5)
    }

    /**
     * Note that only nobi and diagonal enemy nbrs are considered group neighbors
     * while all 20 possible group nbrs are considered for friendly stones.
     */
    @Test
    def testGetGroupNbrs5(): Unit = {
        verifyGroupNbrs("groupNbr5", 4, 4, 0, 1)
    }

    @Test
    def testGetGroupNbrs6(): Unit = {
        verifyGroupNbrs("groupNbr6", 4, 4, 0, 3)
    }

    @Test
    def testGetGroupNbrs7(): Unit = {
        verifyGroupNbrs("groupNbr7", 4, 4, 0, 4)
    }

     @Test
    def testGetGroupNbrs8(): Unit = {
        verifyGroupNbrs("groupNbr8", 4, 4, 4, 6)
    }

    @Test
    def testGetGroupNbrs9(): Unit = {
        verifyGroupNbrs("groupNbr9", 4, 4, 2, 6)
    }

    @Test
    def testGetGroupNbrs10(): Unit = {
        verifyGroupNbrs("groupNbr10", 4, 4, 0, 1)
    }

    @Test
    def testGetGroupNbrs11(): Unit = {
        verifyGroupNbrs("groupNbr11", 4, 4, 2, 3)
    }

    @Test
    def testGetGroupNbrs12(): Unit = {
        verifyGroupNbrs("groupNbr12", 4, 4, 5, 5)
    }

    @Test
    def testGetGroupNbrs13(): Unit = {
        verifyGroupNbrs("groupNbr13", 2, 6, 2, 6)  // 4, 5?
    }

    private def verifyGroupNbrs(file: String, row: Int, col: Int, expectedNumSameNbrs: Int, expectedNumNbrs: Int): Unit = {
        restore(PREFIX +file)

        var board: GoBoard = getBoard
        nbrAnalyzer_ = new NeighborAnalyzer(board)
        var pos: GoBoardPosition = board.getPosition(row, col).asInstanceOf[GoBoardPosition]
        var numSameNbrs: Int = nbrAnalyzer_.findGroupNeighbors(pos, true).size
        var numNbrs: Int = nbrAnalyzer_.findGroupNeighbors(pos, false).size

        Assert.assertTrue("numSameNbrs="+numSameNbrs+" expected "+expectedNumSameNbrs, numSameNbrs == expectedNumSameNbrs)
        Assert.assertTrue("numNbrs="+numNbrs+" expected "+expectedNumNbrs, numNbrs == expectedNumNbrs)
    }

    @Test
    def testFindOccupiedNbrs(): Unit = {

        var file: String = "occupiedNbrs1"
        restore(PREFIX + file)
        var board: GoBoard = getBoard

        var empties: GoBoardPositionList = new GoBoardPositionList()
        empties.add(board.getPosition(3, 3).asInstanceOf[GoBoardPosition])
        empties.add(board.getPosition(3, 4).asInstanceOf[GoBoardPosition])
        empties.add(board.getPosition(4, 3).asInstanceOf[GoBoardPosition])
        empties.add(board.getPosition(4, 4).asInstanceOf[GoBoardPosition])

        verifyOccupiedNbrs(board, empties, 6) // or 9?
    }

    private def verifyOccupiedNbrs(board: GoBoard, empties: GoBoardPositionList, expectedNumNbrs: Int): Unit = {

        nbrAnalyzer_ = new NeighborAnalyzer(board)
        var numNbrs: Int = nbrAnalyzer_.findOccupiedNobiNeighbors(empties).size

        Assert.assertTrue("numNbrs="+numNbrs+" expected "+expectedNumNbrs, numNbrs == expectedNumNbrs)
    }


}
