/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E4Information.Eye4Type.E1113
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E4Information.Eye4Type.E1122
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E4Information.Eye4Type.E2222

class TestE4Information extends TestEyeTypeAnalyzer {


    override protected def getEyeType: EyeType = EyeType.E4

    // All in a row E1122

    @Test
    def testFourBentSpaceEye(): Unit = {
        var b: GoBoard = initializeBoard("four_space_bent_eye", 2)

        checkEdgeBlackEye(b, new E4Information(E1122.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testFourBentOneVitalFilled(): Unit = {
        var b: GoBoard = initializeBoard("four_space_bent_one_vital_filled", 4)

        checkBlackEye(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED)
        checkWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED)
    }

    @Test
    def testFourBentTwoVitalsFilled(): Unit = {
        var b: GoBoard = initializeBoard("four_space_bent_two_vitals_filled", 4)

        checkBlackEye(b, new E4Information(E1122.toString()), EyeStatus.NAKADE)
        checkWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.NAKADE)
    }

    @Test
    def testFourBentOneEndFilled(): Unit = {
        var b: GoBoard = initializeBoard("four_space_bent_one_end_filled", 4)

        checkEdgeBlackEye(b, new E4Information(E1122.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testFourBentTwoEndsFilled(): Unit = {
        var b: GoBoard = initializeBoard("four_space_bent_two_ends_filled", 6)

        checkBlackEye(b, new E4Information(E1122.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.ALIVE)
    }


    @Test
    def testFourBentTwoEndsFilledCut(): Unit = {
        var b: GoBoard = initializeBoard("four_space_bent_two_ends_filled_cut", 7)

        checkWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.ALIVE)
        //checkBlackEye(b, new E4Information(E1122.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testFourBentThreeFilled(): Unit = {
        var b: GoBoard = initializeBoard("four_space_bent_three_filled", 5)

        checkBlackEye(b, new E4Information(E1122.toString()), EyeStatus.NAKADE)
        checkWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED)
    }

    @Test
    def testFourAliveInAtariVitalsSplit(): Unit = {
        var b: GoBoard = initializeBoard("four_space_bent_in_atari_vitals_split", 6)

        checkBlackEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.NAKADE)  // ALIVE_IN_ATARI?
        checkWhiteEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.NAKADE)  // ALIVE_IN_ATARI?
    }

    @Test
    def testFourInAtariVitalsStandard(): Unit = {
        var b: GoBoard = initializeBoard("four_space_bent_in_atari_vitals_standard", 7)

        checkBlackEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.NAKADE)
        checkWhiteEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.NAKADE)
    }

    @Test
    def testFourAliveInAtariEndsStandard(): Unit = {
        var b: GoBoard = initializeBoard("four_space_bent_in_atari_ends_standard", 8)

        checkBlackEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED)
        checkWhiteEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED)
    }

    @Test
    def testFourAliveInAtariEndsSeparate(): Unit = {
        var b: GoBoard = initializeBoard("four_space_bent_in_atari_ends_separate", 8)

        checkBlackEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED)
        checkWhiteEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED)
    }

    @Test
    def testFourAliveInAtariEndsSplit(): Unit = {
        var b: GoBoard = initializeBoard("four_space_bent_in_atari_ends_split", 10)

        checkBlackEyeSurrounded(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED)
        Assert.assertEquals("jump-connected white stones do not share the eye", 0, countGroupsWithOneEye(b, false))
    }

    @Test
    def testFourBentEdgeKo(): Unit = {
        var b: GoBoard = initializeBoard("four_space_bent_edge_ko", 5)

        checkEdgeBlackEye(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED)
        checkEdgeWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED)
    }

    @Test
    def testFourBentCornerKo(): Unit = {
        var b: GoBoard = initializeBoard("four_space_bent_corner_ko", 6)

        checkCornerBlackEye(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED) //ALIVE_IN_ATARI or KO?)
        checkCornerWhiteEye(b, new E4Information(E1122.toString()), EyeStatus.UNSETTLED) // KO
    }


    // pyramid

    @Test
    def testFourPyramidSpaceEye(): Unit = {
        var b: GoBoard = initializeBoard("four_space_pyramid_eye", 2)

        checkBlackEye(b, new E4Information(E1113.toString()), EyeStatus.UNSETTLED)
        checkWhiteEye(b, new E4Information(E1113.toString()), EyeStatus.UNSETTLED)
    }

    @Test
    def testFourPyramidSpaceVitalFilled(): Unit = {
        var b: GoBoard = initializeBoard("four_space_pyramid_vital_filled", 4)

        checkBlackEye(b, new E4Information(E1113.toString()), EyeStatus.NAKADE)
        checkWhiteEye(b, new E4Information(E1113.toString()), EyeStatus.NAKADE)
    }


    // clump

    @Test
    def testFourClumpSpaceEye(): Unit = {
        var b: GoBoard = initializeBoard("four_space_clump_eye", 2)

        checkBlackEye(b, new E4Information(E2222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E4Information(E2222.toString()), EyeStatus.ALIVE)
    }

}
