/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.neighbor

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.common.geometry.Box
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionList

class TestStringNeighborAnalyzer extends GoTestCase {


    private val PREFIX: String = "board/analysis/neighbor/"

    /** instance under test */
    var stringAnalyzer_ : StringNeighborAnalyzer = scala.compiletime.uninitialized
    var board_ : GoBoard = scala.compiletime.uninitialized

    @Test
    def testPushStringNbrsNone(): Unit = {
        initializeAnalyzer("stringNbr_Unoccupied")
        verifyPushStringNbrs(5, 5, true, true, 0)
        verifyPushStringNbrs(5, 5, false, true, 0)
        verifyPushStringNbrs(5, 5, true, false, 0)
        verifyPushStringNbrs(5, 5, false, false, 0)
    }

    @Test
    def testPushStringNbrsOne(): Unit = {
        initializeAnalyzer("stringNbr_oneStone")
        verifyPushStringNbrs(5, 5, true, true, 1)
        verifyPushStringNbrs(5, 8, false, true, 1)
        verifyPushStringNbrs(5, 5, true, false, 1)
        verifyPushStringNbrs(5, 8, false, false, 2)
    }

    @Test
    def testfindStringOne(): Unit = {
        initializeAnalyzer("findString_oneStone")
        verifyFindFriendString(5, 5, true, 1)
        verifyFindFriendString(5, 8, false, 1)
    }

    @Test
    def testfindStringTwo(): Unit = {
        initializeAnalyzer("findString_twoStones")
        verifyFindFriendString(5, 5, true, 2)
        verifyFindFriendString(5, 8, false, 2)
    }

    @Test
    def testfindStringFour(): Unit = {
        initializeAnalyzer("findString_fourStones")
        verifyFindFriendString(5, 5, true, 4)
        verifyFindFriendString(5, 8, false, 4)
    }

    @Test
    def testfindStringClosedLoop(): Unit = {
        initializeAnalyzer("findString_closedLoop")
        verifyFindFriendString(5, 5, true, 10)
        verifyFindFriendString(5, 8, false, 12)
    }

    @Test
    def testfindStringClump(): Unit = {
        initializeAnalyzer("findString_clump")
        verifyFindFriendString(5, 5, true, 9)
        verifyFindFriendString(5, 8, false, 10)
    }

    @Test
    def testfindEyeStringThree(): Unit = {
        initializeAnalyzer("findEyeString_three")
        verifyFindNonFriendString(5, 5, true, 3)
        verifyFindNonFriendString(5, 8, false, 3)
    }

    @Test
    def testfindEyeStringLarge(): Unit = {
        initializeAnalyzer("findEyeString_large")
        verifyFindNonFriendString(5, 5, true, 11)
        verifyFindNonFriendString(5, 8, false, 9)
    }

    private def initializeAnalyzer(file: String): Unit = {
        restore(PREFIX +file)
        board_ = controller.getBoard.asInstanceOf[GoBoard]
        stringAnalyzer_ = new StringNeighborAnalyzer(board_)
    }

    private def verifyPushStringNbrs(row: Int, col: Int, friendP1: Boolean, samePlayerOnly: Boolean, expectedNumNbrs: Int): Unit = {
        var pos: GoBoardPosition = board_.getPosition(row, col).asInstanceOf[GoBoardPosition]

        var stack: GoBoardPositionList = new GoBoardPositionList()
        var numNbrs: Int = stringAnalyzer_.pushStringNeighbors(pos, friendP1, stack, samePlayerOnly)

        Assert.assertEquals("Unexpected number of neigbors pushed on stack.",
                expectedNumNbrs, numNbrs)
    }

    private def verifyFindFriendString(row: Int, col: Int, friendP1: Boolean, expectedNumNbrs: Int): Unit = {
        // same result for both neighbor types
        verifyFindString(row, col, friendP1, NeighborType.FRIEND, expectedNumNbrs)
        verifyFindString(row, col, friendP1, NeighborType.OCCUPIED, expectedNumNbrs)
    }

    private def verifyFindNonFriendString(row: Int, col: Int, friendP1: Boolean, expectedNumNbrs: Int): Unit = {
        verifyFindString(row, col, friendP1, NeighborType.NOT_FRIEND, expectedNumNbrs)
    }

    private def verifyFindString(row: Int, col: Int, friendP1: Boolean, nbrType: NeighborType, expectedNumNbrs: Int): Unit = {

        var pos: GoBoardPosition = board_.getPosition(row, col).asInstanceOf[GoBoardPosition]
        var box: Box = Box(1, 1, board_.getNumRows, board_.getNumCols)
        var numNbrs: Int =
                stringAnalyzer_ .findStringFromInitialPosition(pos, friendP1, true, nbrType, box).size

        Assert.assertEquals("Unexpected number of stones in string.",
                expectedNumNbrs, numNbrs)
    }

}
