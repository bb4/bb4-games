/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer

class TestE3Information extends TestEyeTypeAnalyzer {


    override protected def getEyeType: EyeType = EyeType.E3

    @Test
    def testThreeSpaceEye(): Unit = {
        var b: GoBoard = initializeBoard("three_space_eye")

        checkBlackEye(b, new E3Information(), EyeStatus.UNSETTLED)
        checkWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED)
    }

    @Test
    def testThreeSpaceEyeBent(): Unit = {
        var b: GoBoard = initializeBoard("three_space_eye_bent")

        checkBlackEye(b, new E3Information(), EyeStatus.UNSETTLED)
        checkWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED)
    }

    @Test
    def testThreeSpaceEyeOnEdge(): Unit = {
        var b: GoBoard = initializeBoard("three_space_eye_on_edge")

        checkEdgeBlackEye(b, new E3Information(), EyeStatus.UNSETTLED)
        checkEdgeWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED)
    }

    @Test
    def testThreeSpaceEyeInCorner(): Unit = {
        var b: GoBoard = initializeBoard("three_space_eye_in_corner")

        checkCornerBlackEye(b, new E3Information(), EyeStatus.UNSETTLED)
        checkCornerWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED)
    }

    @Test
    def testThreeSpaceEyeKilled(): Unit = {
        var b: GoBoard = initializeBoard("three_space_eye_killed", 4)

        checkWhiteEye(b, new E3Information(), EyeStatus.NAKADE)
        checkBlackEye(b, new E3Information(), EyeStatus.NAKADE)
    }

    @Test
    def testThreeSpaceEyeBentKilled(): Unit = {
        var b: GoBoard = initializeBoard("three_space_eye_bent_killed", 4)

        checkBlackEye(b, new E3Information(), EyeStatus.NAKADE)
        checkWhiteEye(b, new E3Information(), EyeStatus.NAKADE)
    }

    @Test
    def testThreeSpaceEyeOnEdgeKilled(): Unit = {
        var b: GoBoard = initializeBoard("three_space_eye_on_edge_killed", 4)

        checkEdgeBlackEye(b, new E3Information(), EyeStatus.NAKADE)
        checkEdgeWhiteEye(b, new E3Information(), EyeStatus.NAKADE)
    }

    @Test
    def testThreeSpaceEyeInCornerKilled(): Unit = {
        var b: GoBoard = initializeBoard("three_space_eye_in_corner_killed", 4)

        checkCornerBlackEye(b, new E3Information(), EyeStatus.NAKADE)
        checkCornerWhiteEye(b, new E3Information(), EyeStatus.NAKADE)
    }

    @Test
    def testThreeSpaceEyeWithTwoFilled(): Unit = {
        var b: GoBoard = initializeBoard("three_space_eye_two_filled", 4)

        checkBlackEye(b, new E3Information(), EyeStatus.NAKADE)
        checkWhiteEye(b, new E3Information(), EyeStatus.NAKADE)
    }

    @Test
    def testThreeSpaceEyeWithTwoEndsFilled(): Unit = {
        var b: GoBoard = initializeBoard("three_space_eye_two_ends_filled", 6)

        checkBlackEye(b, new E3Information(), EyeStatus.UNSETTLED)
        checkWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED)
    }


    @Test
    def testThreeSpaceEyeWithTwoFilledInAtari(): Unit = {
        var b: GoBoard = initializeBoard("three_space_eye_two_filled_in_atari", 4)

        checkBlackEye(b, new E3Information(), EyeStatus.NAKADE)
        checkWhiteEye(b, new E3Information(), EyeStatus.NAKADE)
    }

    @Test
    def testThreeSpaceEyeWithTwoEndsFilledInAtari(): Unit = {
        var b: GoBoard = initializeBoard("three_space_eye_two_ends_filled_in_atari", 7)

        checkBlackEye(b, new E3Information(), EyeStatus.UNSETTLED)
        //checkWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED); can't make in atari
    }

}
