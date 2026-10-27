/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.neighbor

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import java.util.List

class TestGroupNeighborAnalyzer extends GoTestCase {


    private val PREFIX: String = "board/analysis/neighbor/"

    /** instance under test */
    var groupAnalyzer_ : GroupNeighborAnalyzer = scala.compiletime.uninitialized
    var board_ : GoBoard = scala.compiletime.uninitialized

    @Test
    def testGroupNbrs_None(): Unit = {
        initializeAnalyzer("groupNbr_none")
        verifyFriendGroupNbrs(5, 5, true, 0)
        verifyFriendGroupNbrs(5, 5, false, 0)
        verifyAllGroupNbrs(5, 5, true, 0)
        verifyAllGroupNbrs(5, 5, false, 0)
    }

    @Test
    def testGroupNbrs_OneDiagonalFriendPartialCut(): Unit = {
        initializeAnalyzer("groupNbr_oneDiagonalFriendPartialCut")
        verifyFriendGroupNbrs(5, 5, true, 1)
        verifyFriendGroupNbrs(5, 8, false, 1)
    }

    @Test
    def testGroupNbrs_OneDiagonalFriendCut(): Unit = {
        initializeAnalyzer("groupNbr_oneDiagonalFriendCut")
        verifyFriendGroupNbrs(5, 5, true, 0)
        verifyFriendGroupNbrs(5, 8, false, 0)
    }

    @Test
    def testGroupNbrs_OneDiagonalEnemy(): Unit = {
        initializeAnalyzer("groupNbr_oneDiagonalEnemy")
        verifyAllGroupNbrs(5, 5, true, 2)
        verifyAllGroupNbrs(5, 8, false, 2)
    }

    @Test
    def testGroupNbrs_OneDiagonalEnemyCut(): Unit = {
        initializeAnalyzer("groupNbr_oneDiagonalEnemyCut")
        verifyAllGroupNbrs(5, 5, true, 3)
        verifyAllGroupNbrs(5, 8, false, 2)
    }

    @Test
    def testGroupNbrs_OneOneSpaceJumpFriendCut(): Unit = {
        initializeAnalyzer("groupNbr_oneOneSpaceJumpFriendCut")
        verifyFriendGroupNbrs(5, 5, true, 0)
        verifyFriendGroupNbrs(5, 8, false, 0)
    }

    @Test
    def testGroupNbrs_OneOneSpaceJumpFriendAtariCut(): Unit = {
        initializeAnalyzer("groupNbr_oneOneSpaceJumpFriendAtariCut")
        verifyFriendGroupNbrs(5, 5, true, 1)
        verifyFriendGroupNbrs(5, 8, false, 1)
    }

    @Test
    def testGroupNbrs_OneKogeimaFriendCut(): Unit = {
        initializeAnalyzer("groupNbr_oneKogeimaFriendCut")
        verifyFriendGroupNbrs(5, 5, true, 0)
        verifyFriendGroupNbrs(5, 8, false, 0)
    }

    /**
     *    x--
     *    O-X   this should not be a cut of the knights move.
     */
    @Test
    def testGroupNbrs_OneKogeimaFriendPartialCut(): Unit = {
        initializeAnalyzer("groupNbr_oneKogeimaFriendPartialCut")
        verifyFriendGroupNbrs(5, 5, true, 1)
        verifyFriendGroupNbrs(5, 8, false, 1)
    }

    /**
     *   This should not be a cut of the knights move.
     */
    @Test
    def testGroupNbrs_OneKogeimaFriendPartialCutInCorner(): Unit = {
        initializeAnalyzer("groupNbr_cornerGroupKogeima")
        verifyFriendGroupNbrs(1, 7, false, 1)
        verifyFriendGroupNbrs(2, 5, false, 1)
    }


    @Test
    def testGroupNbrs_OneFriend(): Unit = {
        initializeAnalyzer("groupNbr_oneFriend")
        verifyFriendGroupNbrs(5, 5, true, 1)
        verifyFriendGroupNbrs(5, 8, false, 1)
        verifyAllGroupNbrs(5, 5, true, 1)
        verifyAllGroupNbrs(5, 8, false, 1)
    }

    @Test
    def testGroupNbrs_OneNotFriend(): Unit = {
        initializeAnalyzer("groupNbr_oneNotFriend")
        verifyFriendGroupNbrs(5, 5, true, 0)
        verifyFriendGroupNbrs(5, 8, false, 0)
        verifyAllGroupNbrs(5, 5, true, 1)
        verifyAllGroupNbrs(5, 8, false, 1)
    }

    @Test
    def testGroupNbrs_Mixed(): Unit = {
        initializeAnalyzer("groupNbr_mixed")
        verifyFriendGroupNbrs(5, 5, true, 2)
        verifyFriendGroupNbrs(5, 8, false, 2)
        verifyAllGroupNbrs(5, 5, true, 3)
        verifyAllGroupNbrs(5, 8, false, 3)
    }

    @Test
    def testGroupNbrs_nobiOnly(): Unit = {
        initializeAnalyzer("groupNbr_nobiOnly")
        verifyFriendGroupNbrs(5, 5, true, 4)
        verifyFriendGroupNbrs(5, 8, false, 4)
    }

    // Only pure group neigbors present.
    @Test
    def testGroupNbrs_pureOnly(): Unit = {
        initializeAnalyzer("groupNbr_pureOnly")
        verifyFriendGroupNbrs(5, 5, true, 16)
        verifyFriendGroupNbrs(9, 9, false, 16)
    }

    @Test
    def testGroupNbrs_all20Mixed(): Unit = {
        initializeAnalyzer("groupNbr_all20Mixed")
        verifyFriendGroupNbrs(5, 5, true, 9)
        verifyFriendGroupNbrs(9, 9, false, 12)
        verifyAllGroupNbrs(5, 5, true, 10)
        verifyAllGroupNbrs(9, 9, false, 12)
    }

    @Test
    def testGroupNbrs_randomMixed(): Unit = {
        initializeAnalyzer("groupNbr_randomMixed")
        verifyFriendGroupNbrs(5, 5, true, 1)
        verifyFriendGroupNbrs(9, 9, false, 2)
        verifyAllGroupNbrs(5, 5, true, 4)
        verifyAllGroupNbrs(9, 9, false, 3)
    }


    /**
     * Corner group on a 7*7 board from a real game.
     */
    @Test
    def testFindGroupFromInitialPosition(): Unit = {
        initializeAnalyzer("groupNbr_cornerGroupKogeima")
        var pos1: GoBoardPosition = board_.getPosition(2, 5).asInstanceOf[GoBoardPosition]
        var group1: List[GoBoardPosition] = groupAnalyzer_.findGroupFromInitialPosition(pos1, true)


        var pos2: GoBoardPosition = board_.getPosition(1, 7).asInstanceOf[GoBoardPosition]
        var group2: List[GoBoardPosition] = groupAnalyzer_.findGroupFromInitialPosition(pos2, true)

        System.out.println("group1=" + group1)
        System.out.println("group2=" + group2)
        Assert.assertEquals("Group1 did not have expected size.", 1, group1.size)
        Assert.assertEquals("Groups did not have the same size", group1.size, group2.size)
    }


    private def initializeAnalyzer(file: String): Unit = {
        restore(PREFIX +file)
        board_ = getBoard
        groupAnalyzer_ = new GroupNeighborAnalyzer(board_)
    }

    private def verifyFriendGroupNbrs(row: Int, col: Int, friendP1: Boolean, expectedNumNbrs: Int): Unit = {
        verifyGroupNbrs(row, col, friendP1, true, expectedNumNbrs)
    }

    private def verifyAllGroupNbrs(row: Int, col: Int, friendP1: Boolean, expectedNumNbrs: Int): Unit = {
        verifyGroupNbrs(row, col, friendP1, false, expectedNumNbrs)
    }

    private def verifyGroupNbrs(row: Int, col: Int, friendP1: Boolean, samePlayerOnly: Boolean, expectedNumNbrs: Int): Unit = {

        var pos: GoBoardPosition = board_.getPosition(row, col).asInstanceOf[GoBoardPosition]
        var numNbrs: Int = groupAnalyzer_.findGroupNeighbors(pos, friendP1, samePlayerOnly).size
        Assert.assertEquals("Unexpected number of group neigbors found.",
                expectedNumNbrs, numNbrs)
    }

}
