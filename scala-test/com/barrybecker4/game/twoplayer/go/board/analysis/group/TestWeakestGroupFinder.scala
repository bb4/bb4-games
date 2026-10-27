/* Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.common.Move
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.GoBoardConfigurator
import com.barrybecker4.game.twoplayer.go.board.analysis.WorthCalculator
import com.barrybecker4.game.twoplayer.go.board.elements.group.GoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import com.barrybecker4.game.twoplayer.go.options.GoWeights

class TestWeakestGroupFinder extends GoTestCase {


    /** where to go for the test files. */
    private val PREFIX: String = "board/analysis/group/weakest_group_finder_"

    private val WEIGHTS: GoWeights = new GoWeights()

    /** instance under test. */
    private var finder: WeakestGroupFinder = scala.compiletime.uninitialized
    private var analyzerMap: GroupAnalyzerMap = new GroupAnalyzerMap()


    @Test
    def testFindGroupOnEmpytyBoard(): Unit = {
        var boardConfig: GoBoardConfigurator = new GoBoardConfigurator(9)

        boardConfig.setPositions(new GoBoardPosition(2, 3, null, new GoStone(true)))
        var board: GoBoard = boardConfig.getBoard
        finder = createFinder(board)

        var groupStones: GoBoardPositionSet = new GoBoardPositionSet()
        groupStones.add(board.getPosition(2, 3).asInstanceOf[GoBoardPosition])

        var group: GoGroup = finder.findWeakestGroup(groupStones)
        Assert.assertNull("There should not be a weakest group if the board is empty", group)
    }

    @Test
    def testFindGroupWithSingleStoneNeighbor(): Unit = {
        var board: GoBoard = initFinder("single_stone")

        var groupStones: GoBoardPositionSet = new GoBoardPositionSet()
        groupStones.add(board.getPosition(4, 3).asInstanceOf[GoBoardPosition])

        var group: GoGroup = finder.findWeakestGroup(groupStones)
        Assert.assertEquals("Unexpected number of stones found.", 1, group.getNumStones)
    }

    @Test
    def testFindWeakestGroupWhenTwoCompetingGroups(): Unit = {
        var board: GoBoard = initFinder("two_groups")

        var groupStones: GoBoardPositionSet = new GoBoardPositionSet()
        // the two stone black group.
        groupStones.add(board.getPosition(5, 6).asInstanceOf[GoBoardPosition])
        groupStones.add(board.getPosition(6, 6).asInstanceOf[GoBoardPosition])

        var group: GoGroup = finder.findWeakestGroup(groupStones)
        Assert.assertEquals("most alive enemy is the larger group", 5, group.getNumStones)
    }

    @Test
    def testFindWeakestGroupWhenTwoCompetingGroups2(): Unit = {
        var board: GoBoard = initFinder("two_groups2")

        var groupStones: GoBoardPositionSet = new GoBoardPositionSet()
        // the two stone black group.
        groupStones.add(board.getPosition(5, 6).asInstanceOf[GoBoardPosition])
        groupStones.add(board.getPosition(6, 6).asInstanceOf[GoBoardPosition])

        var group: GoGroup = finder.findWeakestGroup(groupStones)
        Assert.assertEquals("most alive enemy is the larger group", 7, group.getNumStones)
    }


    private def initFinder(fileSuffix: String): GoBoard = {
        restore(PREFIX  + fileSuffix)
        var board: GoBoard = getBoard

        finder = createFinder(board)
        /** need something to update the scores for the group  */
        var move: Move = controller.getLastMove
        var calculator: WorthCalculator = new WorthCalculator(board, analyzerMap)
        calculator.worth(move, WEIGHTS.getDefaultWeights)
        System.out.println("AMAP="+ analyzerMap)

        board
    }

    private def createFinder(board: GoBoard): WeakestGroupFinder = {
        finder = new WeakestGroupFinder(board, analyzerMap)
        finder
    }

}
