/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet
import java.util.List

class TestGroupFinding extends GoTestCase {


    /** where to go for the test files. */
    private val PREFIX: String = "board/analysis/eye/information/FalseEye/"


    // ----------- check that we can find group neighbors -------

    @Test
    def testFindKoGroupNeighbors(): Unit = {
        var b: GoBoard = initializeBoard("false_ko_eye1")

        // white group neighbors
        verifyGroupNeighbors(b, new ByteLocation(4, 5), 3)
        verifyGroupNeighbors(b, new ByteLocation(5, 6), 6)
        verifyGroupNeighbors(b, new ByteLocation(6, 7), 5)
        verifyGroupNeighbors(b, new ByteLocation(7, 5), 7)   // 8?

        // black group neighbors
        verifyGroupNeighbors(b, new ByteLocation(4, 6), 3)
        verifyGroupNeighbors(b, new ByteLocation(8, 8), 6)
        verifyGroupNeighbors(b, new ByteLocation(6, 7), 5)
        verifyGroupNeighbors(b, new ByteLocation(9, 7), 5)
        verifyGroupNeighbors(b, new ByteLocation(8, 6), 5)
    }

    @Test
    def testFindKoGroupNeighbors2(): Unit = {
        var b: GoBoard = initializeBoard("false_ko_eye2")

        // white group neighbors
        verifyGroupNeighbors(b, new ByteLocation(13, 7), 6)
        verifyGroupNeighbors(b, new ByteLocation(12, 8), 3)

        // black group neighbors
        verifyGroupNeighbors(b, new ByteLocation(10, 8), 9)
        verifyGroupNeighbors(b, new ByteLocation(12, 7), 1)

    }

    // ----------- check that we can find groups ----------------

    /**
     * Negative test.
     * The position we give to look from does not contain a stone.
     */
    @Test
    def testFindNoGroup(): Unit = {
        var b: GoBoard = initializeBoard("false_ko_eye1")
        try {
            verifyGroup(b, new ByteLocation(2, 3), 6)
            Assert.fail()
        } catch { case e: NullPointerException =>
            // expected, but maybe we should throw illegal argument exception instead.
        }
    }

    @Test
    def testFindFalseEyeGroup1(): Unit = {
        var b: GoBoard = initializeBoard("false_ko_eye1")
        verifyGroup(b, new ByteLocation(5, 5), 10)
        verifyGroup(b, new ByteLocation(6, 8), 11)
    }

    @Test
    def testFindFalseEyeGroup2(): Unit = {
        var b: GoBoard = initializeBoard("false_ko_eye2")
        verifyGroup(b, new ByteLocation(8, 8), 18)
        verifyGroup(b, new ByteLocation(13, 10), 18)
    }


    private def initializeBoard(file: String): GoBoard = {
        restore(PREFIX  + file)
        return getBoard
    }

    private def verifyGroupNeighbors(board: GoBoard, loc: Location, expectedNumNeighbors: Int): Unit = {
        var na: NeighborAnalyzer = new NeighborAnalyzer(board)
        var position: GoBoardPosition = board.getPosition(loc).asInstanceOf[GoBoardPosition]
        var group: GoBoardPositionSet = na.findGroupNeighbors(position, true)
        Assert.assertEquals("Unexpected number of group neighbors for : "+ position +" \n" + group + "\n",
                expectedNumNeighbors, group.size)
    }

    private def verifyGroup(board: GoBoard, loc: Location, expectedNumStonesInGroup: Int): Unit = {
        var na: NeighborAnalyzer = new NeighborAnalyzer(board)
        var position: GoBoardPosition = board.getPosition(loc).asInstanceOf[GoBoardPosition]
        var group: List[GoBoardPosition] = na.findGroupFromInitialPosition(position)
        Assert.assertEquals("Unexpected number of stones in group: \n" + group + "\n",
                expectedNumStonesInGroup, group.size)
    }

}
