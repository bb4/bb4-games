/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import com.barrybecker4.game.twoplayer.go.board.move.GoMove

class TestWorthCalculator5 extends WorthCalculatorBase {


    override protected def getBoardSize: Int = 5

    /** verify that we get the expected worth value. */
    @Test
    def testFindSimpleWorth(): Unit = {
        verifyWorth("worth5x5", -56)
    }

    /** verify that we get the expected worth value after a move redo. */
    @Test
    def testFindWorthAfterRedo(): Unit = {

        restore(PREFIX  + "worth5x5")

        var move: GoMove = controller.undoLastMove()
        controller.makeMove(move)

        var actWorth: Int = calculator.worth(move, WEIGHTS.getDefaultWeights)

        Assertions.assertEquals(-56, actWorth, "Unexpected worth.")
    }

    /**
     * If we arrive at the same exact board position from two different paths,
     * we should calculate the same worth value.
     */
    @Test
    def testSamePositionFromDifferentPathsEqual(): Unit = {

        compareWorths("worth5x5_A", "worth5x5_B", -99)
    }


}
