/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.neighbor

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.GoBoardConfigurator
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionList

class TestNobiNeighborAnalyzer extends GoTestCase {


    private val PREFIX: String = "board/analysis/neighbor/"

    /** instance under test */
    private var nobiAnalyzer_ : NobiNeighborAnalyzer = scala.compiletime.uninitialized


    @Test
    def testNoOccupiedNobiNbrs(): Unit = {
        verifyOccupiedNobiNbrs("nobiNbr_NoneOccupied", Array(new ByteLocation(5, 5)), 0)
    }

    @Test
    def testBlackOccupiedNobiNbrs(): Unit = {
        verifyOccupiedNobiNbrs("nobiNbr_BlackOccupied", Array(new ByteLocation(7, 7)), 3)
    }

    @Test
    def testMixOccupiedNobiNbrs(): Unit = {
        verifyOccupiedNobiNbrs("nobiNbr_MixOccupied", Array(new ByteLocation(7, 7)), 3)
    }

    @Test
    def testMixOccupiedNobiNbrsOnEdge(): Unit = {
        verifyOccupiedNobiNbrs("nobiNbr_MixOccupiedOnEdge", Array(new ByteLocation(5, 1)), 2)
    }

    @Test
    def testNobiNbrsOnEdge(): Unit = {
        verifyOccupiedNobiNbrs("nobiNbr_MixOccupiedOnEdge", Array(new ByteLocation(5, 1)), 2)
    }


    @Test
    def testUnoccupiedNobiNbrs(): Unit = {
        verifyNobiNbrs("nobiNbr_Unoccupied", 5, 5, false, NeighborType.UNOCCUPIED, 2)
    }

    @Test
    def testUnoccupiedNobiNbrsInCorner(): Unit = {
        verifyNobiNbrs("nobiNbr_UnoccupiedInCorner", 1, 1, false, NeighborType.UNOCCUPIED, 2)
    }

    @Test
    def testFriendNobiNbrsOneBlack(): Unit = {
        verifyNobiNbrs("nobiNbr_OneBlackFriend", 5, 5, true, NeighborType.FRIEND, 1)
    }

    @Test
    def testFriendNobiNbrsFourWhite(): Unit = {
        verifyNobiNbrs("nobiNbr_FourWhiteFriends", 5, 5, false, NeighborType.FRIEND, 4)
    }

    @Test
    def testEnemyNobiNbrsOneWhite(): Unit = {
        verifyNobiNbrs("nobiNbr_OneWhiteEnemy", 5, 5, true, NeighborType.ENEMY, 1)
    }

    @Test
    def testEnemyNobiNbrsFourBlack(): Unit = {
        verifyNobiNbrs("nobiNbr_FourBlackEnemies", 5, 5, false, NeighborType.ENEMY, 4)
    }

    @Test
    def testNotFriendNobiNbrsTwo(): Unit = {
        verifyNobiNbrs("nobiNbr_TwoNotFriends", 5, 5, false, NeighborType.NOT_FRIEND, 2)
    }

    @Test
    def testNotFriendNobiNbrsThree(): Unit = {
        verifyNobiNbrs("nobiNbr_ThreeNotFriends", 5, 5, true, NeighborType.NOT_FRIEND, 3)
    }

    private def verifyOccupiedNobiNbrs(file: String, empties: Array[Location], expectedNumOccupiedNbrs: Int): Unit = {
        restore(PREFIX +file)

        var board: GoBoard = getBoard
        nobiAnalyzer_ = new NobiNeighborAnalyzer(board)
        var emptyList: GoBoardPositionList = GoBoardConfigurator.createPositionList(empties)

        var numOccupiedNbrs: Int = nobiAnalyzer_.findOccupiedNobiNeighbors(emptyList).size

        Assert.assertEquals("Unexpected number of occupied neigbors.",
                expectedNumOccupiedNbrs, numOccupiedNbrs)
    }

    private def verifyNobiNbrs(file: String, row: Int, col: Int, friendOwnedByP1: Boolean, nbrType: NeighborType, expectedNumNbrs: Int): Unit = {
        restore(PREFIX +file)

        var board: GoBoard = getBoard
        nobiAnalyzer_ = new NobiNeighborAnalyzer(board)
        var pos: GoBoardPosition = board.getPosition(row, col).asInstanceOf[GoBoardPosition]

        var numNbrs: Int = nobiAnalyzer_.getNobiNeighbors(pos, friendOwnedByP1, nbrType).size

        Assert.assertEquals("Unexpected number of neigbors.",
                expectedNumNbrs, numNbrs)
    }

}
