/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.analysis.group.LifeAnalyzer
import com.barrybecker4.game.twoplayer.go.board.analysis.group.StubLifeAnalyzer

class TestEyeHealthEvaluator extends GoTestCase {


    private val PREFIX: String = "board/analysis/group/eye/potential/"

    private val TOLERANCE: Double = 0.001


    @Test
    def testZeroEyes_OneLiberty_OneStone(): Unit = {

        verifyEyePotential(0, 1, 1, -0.7f)
    }

    @Test
    def testZeroEyes_TwoLiberties_OneStone(): Unit = {

        verifyEyePotential(0, 2, 1, 0.02f)
    }

    @Test
    def testOneEyes_TwoLiberties(): Unit = {

        var numStones: Int = 0 // var used: not = scala.compiletime.uninitialized
        verifyEyePotential(1, 2, numStones, -0.3f)
    }

    @Test
    def testOneEyes_ThreeLiberties(): Unit = {

        var numStones: Int = 0 // var used: not = scala.compiletime.uninitialized
        verifyEyePotential(1, 3, numStones, -0.1f)
    }

    @Test
    def testOneEyes_FourLiberties(): Unit = {

            var numStones: Int = 0 // var used: not = scala.compiletime.uninitialized
            verifyEyePotential(1, 4, numStones, 0.01f)
    }

    @Test
    def testOneEyes_FiveLiberties(): Unit = {

            var numStones: Int = 0 // var used: not = scala.compiletime.uninitialized
            verifyEyePotential(1, 5, numStones, 0.05f)
    }


    private def restoreGame(file: String): Unit = {
        restore(PREFIX + file)
    }

    private def verifyEyePotential(numEyes: Int, numLiberties: Int, numStones: Int, expEyeHealth: Float): Unit = {
        verifyEyeHealth(1.0f, numEyes, numLiberties, numStones, expEyeHealth)
        verifyEyeHealth(-1.0f, numEyes, numLiberties, numStones, -expEyeHealth)
    }


    /**
     * Use EyePotentialAnalyzer to find potential of making eyes.
     */
    private def verifyEyeHealth(forBlackGroup: Float, numEyes: Int, numLiberties: Int, numStones: Int, expEyeHealth: Float): Unit = {

        var isUnconditionallyAlive: Boolean = false
        var lifeAnalyzer: LifeAnalyzer = new StubLifeAnalyzer(isUnconditionallyAlive)
        var evaluator: EyeHealthEvaluator = new EyeHealthEvaluator(lifeAnalyzer)

        var actEyeHealth: Float = evaluator.determineHealth(forBlackGroup, numEyes, numLiberties, numStones)

        Assert.assertEquals("Unexpected eyeHealth", expEyeHealth, actEyeHealth, TOLERANCE)
    }


}
