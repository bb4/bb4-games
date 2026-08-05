/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E5Information.Eye5Type.*

class TestE5Information extends TestEyeTypeAnalyzer {


    override protected def getEyeType: EyeType = EyeType.E5

    @Test
    def testFiveStraightSpaceEye(): Unit = {
        var b: GoBoard = initializeBoard("five_straight_eye", 2)

        checkBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testFiveBentSpaceEye(): Unit = {
        var b: GoBoard = initializeBoard("five_bent_eye", 2)

        checkBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testFiveBentOneMiddleFilled(): Unit = {
        var b: GoBoard = initializeBoard("five_bent_one_middle_filled", 4)

        checkBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testFiveBentTwoMiddleFilled(): Unit = {
        var b: GoBoard = initializeBoard("five_bent_two_middle_filled", 4)

        checkBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
    }

    /**
     * At first I thought this should be NAKADE, but ALIVE is actually correct because
     * at worst the group should live in seki.
     */
    @Test
    def testFiveBentThreeMiddleFilled(): Unit = {
        var b: GoBoard = initializeBoard("five_bent_three_middle_filled", 4)

        checkBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testFiveBentThreeFilledOnEnd(): Unit = {
        var b: GoBoard = initializeBoard("five_bent_three_filled_on_end", 4)

        checkBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testFiveBentFourFilled(): Unit = {
        var b: GoBoard = initializeBoard("five_bent_four_filled", 4)

        checkBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testFiveBentNearEdge(): Unit = {
        var b: GoBoard = initializeBoard("five_bent_near_edge", 2)

        checkBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testFiveBentOnEdge(): Unit = {
        var b: GoBoard = initializeBoard("five_bent_on_edge", 2)

        checkCornerBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
        checkEdgeWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testFiveBentOnEdgeMiddleThreeFilled(): Unit = {
        var b: GoBoard = initializeBoard("five_bent_on_edge_middle_three_filled", 4)

        checkCornerBlackEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
        checkEdgeWhiteEye(b, new E5Information(E11222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testFiveAliveInAtari(): Unit = {
        var b: GoBoard = initializeBoard("five_alive_in_atari", 6)

        checkWhiteEyeSurrounded(b, new E5Information(E11222.toString()), EyeStatus.ALIVE_IN_ATARI)
    }

    //////   E11123

    @Test
    def testFiveE11123Empty(): Unit = {
        var b: GoBoard = initializeBoard("five_E11123_empty", 2)

        checkBlackEye(b, new E5Information(E11123.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E5Information(E11123.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testFiveE11123OneVitalFilled(): Unit = {
        var b: GoBoard = initializeBoard("five_E11123_one_vital_filled", 4)

        checkBlackEye(b, new E5Information(E11123.toString()), EyeStatus.UNSETTLED)
        checkWhiteEye(b, new E5Information(E11123.toString()), EyeStatus.UNSETTLED)
    }

    @Test
    def testFiveE11123TwoVitalsFilled(): Unit = {
        var b: GoBoard = initializeBoard("five_E11123_two_vitals_filled", 4)

        checkBlackEye(b, new E5Information(E11123.toString()), EyeStatus.NAKADE)
        checkWhiteEye(b, new E5Information(E11123.toString()), EyeStatus.NAKADE)
    }

    @Test
    def testFiveE11123TwoVitalsOneOtherFilled(): Unit = {
        var b: GoBoard = initializeBoard("five_E11123_two_vitals_one_other_filled", 4)

        checkBlackEye(b, new E5Information(E11123.toString()), EyeStatus.NAKADE)
        checkWhiteEye(b, new E5Information(E11123.toString()), EyeStatus.NAKADE)
    }

    @Test
    def testFiveE11123TwoVitalsTwoOthersFilled(): Unit = {
        var b: GoBoard = initializeBoard("five_E11123_two_vitals_two_others_filled", 4)

        checkBlackEye(b, new E5Information(E11123.toString()), EyeStatus.NAKADE)
        checkWhiteEye(b, new E5Information(E11123.toString()), EyeStatus.NAKADE)
    }

    @Test
    def testFiveE11123TwoVitalsAndEndFilled(): Unit = {
        var b: GoBoard = initializeBoard("five_E11123_two_vitals_and_end_filled", 4)

        checkBlackEye(b, new E5Information(E11123.toString()), EyeStatus.NAKADE)
        checkWhiteEye(b, new E5Information(E11123.toString()), EyeStatus.NAKADE)
    }

    //////   E11114

    @Test
    def testFiveStarEmpty(): Unit = {
        var b: GoBoard = initializeBoard("five_star_empty", 2)

        checkBlackEye(b, new E5Information(E11114.toString()), EyeStatus.UNSETTLED)
        checkWhiteEye(b, new E5Information(E11114.toString()), EyeStatus.UNSETTLED)
    }

    @Test
    def testFiveStarVitalFilled(): Unit = {
        var b: GoBoard = initializeBoard("five_star_vital_filled", 4)

        checkBlackEye(b, new E5Information(E11114.toString()), EyeStatus.NAKADE)
        checkWhiteEye(b, new E5Information(E11114.toString()), EyeStatus.NAKADE)
    }

    @Test
    def testFiveStarThreeOthersFilled(): Unit = {
        var b: GoBoard = initializeBoard("five_star_three_others_filled", 4)

        checkBlackEye(b, new E5Information(E11114.toString()), EyeStatus.UNSETTLED)
        checkWhiteEye(b, new E5Information(E11114.toString()), EyeStatus.UNSETTLED)
    }

    @Test
    def testFiveStarVitalAndThreeOthersFilled(): Unit = {
        var b: GoBoard = initializeBoard("five_star_vital_and_three_others_filled", 4)

        checkBlackEye(b, new E5Information(E11114.toString()), EyeStatus.NAKADE)
        checkWhiteEye(b, new E5Information(E11114.toString()), EyeStatus.NAKADE)
    }

    //////   E122223

    @Test
    def testFiveE122223Empty(): Unit = {
        var b: GoBoard = initializeBoard("five_E12223_empty", 2)

        checkBlackEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED)
        checkWhiteEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED)
    }

    @Test
    def testFiveE122223EmptyClose(): Unit = {
        var b: GoBoard = initializeBoard("five_E12223_empty_close", 2)

        checkBlackEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED)
        checkWhiteEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED)
    }

    @Test
    def testFiveE122223VitalFilled(): Unit = {
        var b: GoBoard = initializeBoard("five_E12223_vital_filled", 4)

        checkBlackEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED)
        checkWhiteEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED)
    }

    @Test
    def testFiveE122223VitalAndTwoOthersFilled(): Unit = {
        var b: GoBoard = initializeBoard("five_E12223_vital_and_two_others_filled", 4)

        checkBlackEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED)
        checkWhiteEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED)
    }

    @Test
    def testFiveE122223VitalAndEndFilled(): Unit = {
        var b: GoBoard = initializeBoard("five_E12223_vital_and_end_filled", 4)

        checkBlackEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED)
        checkWhiteEye(b, new E5Information(E12223.toString()), EyeStatus.UNSETTLED)
    }

}
