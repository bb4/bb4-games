/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye.potential

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.GoTestCase

class TestRun extends GoTestCase {


    private var run: Run = scala.compiletime.uninitialized

    @Test
    def testRunZeroLength(): Unit = {

        run = new Run(5, 6, 10, false)
        verifyPotential(0.05f)
    }

    @Test
    def testRunOneLength(): Unit = {

        run = new Run(5, 7, 10, false)
        verifyPotential(0.15f)
    }

    @Test
    def testRunOneLengthBounded(): Unit = {

        run = new Run(5, 7, 10, true)
        verifyPotential(0.35f)
    }

    @Test
    def testRunFourLength(): Unit = {

        run = new Run(5, 9, 10, false)
        verifyPotential(0.25f)
    }

    @Test
    def testRunFourLengthBounded(): Unit = {

        run = new Run(5, 9, 10, true)
        verifyPotential(0.3f)
    }

    @Test
    def testRunOneOnEndInternal(): Unit = {

        run = new Run(10, 11, 10, false)
        verifyPotential(0.25f)
    }

    @Test
    def testRunOneAtStartInternal(): Unit = {

        run = new Run(1, 2, 10, false)
        verifyPotential(0.25f)
    }

    private def verifyPotential(expectedPotential: Float): Unit = {

        Assert.assertEquals("Unexpected run potential for " + run , expectedPotential, run.getPotential, 0.0f)
    }

}
