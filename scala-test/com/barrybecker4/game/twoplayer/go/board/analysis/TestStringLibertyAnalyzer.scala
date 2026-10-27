/* Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.GoBoardConfigurator
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionList
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import com.barrybecker4.game.twoplayer.go.board.elements.string.GoString

class TestStringLibertyAnalyzer extends GoTestCase {


    private var boardConfig: GoBoardConfigurator = scala.compiletime.uninitialized


    @Before
    override def setUp(): Unit = {
        super.setUp()
        boardConfig = new GoBoardConfigurator(9, 0)
    }

    @Test
    def testSingleStoneLiberties(): Unit = {

        var list: GoBoardPositionList = new GoBoardPositionList()
        list.add(createBlackStone(4, 4))

        verifyNumberOfLiberties(4, list)
    }

    @Test
    def testTwoStringLiberties(): Unit = {

        var list: GoBoardPositionList = new GoBoardPositionList()
        list.add(createBlackStone(4, 4))
        list.add(createBlackStone(4, 5))

        verifyNumberOfLiberties(6, list)
    }

    /**
     *  XX
     *   X
     */
    @Test
    def testThreeStringLiberties(): Unit = {

        var list: GoBoardPositionList = new GoBoardPositionList()
        list.add(createBlackStone(4, 4))
        list.add(createBlackStone(4, 5))
        list.add(createBlackStone(5, 5))

        verifyNumberOfLiberties(7, list)
    }

    /**
     *  OO
     *  XX
     *   XO
     */
    @Test
    def testThreeStringLibertiesWithOther(): Unit = {

        var list: GoBoardPositionList = new GoBoardPositionList()
        list.add(createBlackStone(4, 4))
        list.add(createBlackStone(4, 5))
        list.add(createBlackStone(5, 5))

        var otherList: GoBoardPositionList = new GoBoardPositionList()
        otherList.add(createWhiteStone(3, 4))
        otherList.add(createWhiteStone(3, 5))
        otherList.add(createWhiteStone(5, 6))

        verifyNumberOfLibertiesWithOther(4, list, otherList)
    }

    /**
     *  XXX
     *  X X
     *  XXX
     */
    @Test
    def testOStringLiberties(): Unit = {

        var list: GoBoardPositionList = new GoBoardPositionList()
        list.add(createBlackStone(4, 4))
        list.add(createBlackStone(4, 5))
        list.add(createBlackStone(4, 6))
        list.add(createBlackStone(5, 4))
        list.add(createBlackStone(5, 6))
        list.add(createBlackStone(6, 4))
        list.add(createBlackStone(6, 5))
        list.add(createBlackStone(6, 6))

        verifyNumberOfLiberties(13, list)
    }

    /**
     *  ___
     *  X |
     *  XX|
     */
    @Test
    def testInCornerStringLiberties(): Unit = {

        var list: GoBoardPositionList = new GoBoardPositionList()
        list.add(createBlackStone(1, 8))
        list.add(createBlackStone(2, 8))
        list.add(createBlackStone(2, 9))

        verifyNumberOfLiberties(5, list)
    }

    private def verifyNumberOfLiberties(expectedNumber: Int, list: GoBoardPositionList): Unit = {

        verifyNumberOfLibertiesWithOther(expectedNumber, list, null)
    }

    private def verifyNumberOfLibertiesWithOther(expectedNumber: Int, list: GoBoardPositionList, otherList: GoBoardPositionList): Unit = {

        var board: GoBoard = boardConfig.getBoard
        var string: GoString = new GoString(list, board)
        boardConfig.setPositions(list)
        if (otherList != null) {
            boardConfig.setPositions(otherList)
        }
        var analyzer: StringLibertyAnalyzer = new StringLibertyAnalyzer(board, string)
        Assert.assertEquals("Unexpected number of liberties",
                expectedNumber, analyzer.getLiberties.size)
    }

    /**
     * create a black stone to add to the string.
     */
    private def createBlackStone(row: Int, col: Int): GoBoardPosition = new GoBoardPosition(row, col, null, new GoStone(true, 0.5f))

    /**
     * create a white stone to for other spots on the board.
     */
    private def createWhiteStone(row: Int, col: Int): GoBoardPosition = new GoBoardPosition(row, col, null, new GoStone(false, 0.5f))

}
