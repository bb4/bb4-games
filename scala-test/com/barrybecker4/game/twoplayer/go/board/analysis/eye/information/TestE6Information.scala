/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E6Information.Eye6Type.E112222

class TestE6Information extends TestEyeTypeAnalyzer {


    override protected def getEyeType: EyeType = EyeType.E6

    //////   E112222  (just a sampling of the cases)

    @Test
    def testSixStraightEye(): Unit = {
        var b: GoBoard = initializeBoard("six_straight_eye", 2)

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testSixBentEye(): Unit = {
        var b: GoBoard = initializeBoard("six_bent_eye", 2)

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testVerySixBentEye(): Unit = {
        var b: GoBoard = initializeBoard("six_very_bent_eye", 2)

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testSixBentOneMiddleFilled(): Unit = {
        var b: GoBoard = initializeBoard("six_bent_one_middle_filled", 4)

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testSixBentTwoMiddleFilled(): Unit = {
        var b: GoBoard = initializeBoard("six_bent_two_middle_filled", 5)

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testSixBentThreeMiddleFilled(): Unit = {
        var b: GoBoard = initializeBoard("six_bent_three_middle_filled", 5)

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testSixBentFourMiddleFilled(): Unit = {
        var b: GoBoard = initializeBoard("six_bent_four_middle_filled", 4)

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testSixBentTwoEndsFilled(): Unit = {
        var b: GoBoard = initializeBoard("six_bent_two_ends_filled", 6)

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
    }


    @Test
    def testSixBentFiveFilled(): Unit = {
        var b: GoBoard = initializeBoard("six_bent_five_filled", 4)

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testSixBentOnEdge(): Unit = {
        var b: GoBoard = initializeBoard("six_bent_on_edge", 2)

        checkEdgeBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
        checkEdgeWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testSixBentOnEdgeThreeFilled(): Unit = {
        var b: GoBoard = initializeBoard("six_bent_on_edge_three_filled", 4)

        checkEdgeBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
        checkEdgeWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
    }


    @Test
    def testSixBentInCorner(): Unit = {
        var b: GoBoard = initializeBoard("six_bent_in_corner", 2)

        checkCornerBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
        checkCornerWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
    }

    /** This may be an interesting case that needs more study. */
    @Test
    def testSixBentInCornerKoFilled(): Unit = {
        var b: GoBoard = initializeBoard("six_bent_in_corner_ko_filled", 4)

        checkCornerBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
        checkCornerWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE)
    }

    @Test
    def testSixBlackAliveInAtari(): Unit = {
        var b: GoBoard = initializeBoard("six_black_alive_in_atari", 4)

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE_IN_ATARI)
        checkWhiteEye(b, new TerritorialEyeInformation(), EyeStatus.ALIVE)
    }



    //////   E111223   (just a sampling of the cases)


    //   TODO
    //////   E112233
    //////   E122223
    //////   E222233
    //////   E112224
    //////   E111124
    //////   E111133


}
