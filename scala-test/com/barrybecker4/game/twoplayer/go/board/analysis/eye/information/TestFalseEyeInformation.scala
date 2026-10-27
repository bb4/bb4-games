/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E5Information.Eye5Type.E11222
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E6Information.Eye6Type.E112222

class TestFalseEyeInformation extends TestEyeTypeAnalyzer {


    override protected def getEyeType: EyeType = EyeType.FalseEye

    @Test
    def testFalseKoEye1(): Unit = {
        var b: GoBoard = initializeBoard("false_ko_eye1")

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.FALSE)
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.FALSE)
    }

    @Test
    def testFalseKoEye2(): Unit = {
        var b: GoBoard = initializeBoard("false_ko_eye2")

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.FALSE)
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.FALSE)
    }

    @Test
    def testFalseBasicEyeTwoDeep(): Unit = {
        var b: GoBoard = initializeBoard("false_basic_eye2")

        checkBlackEye(b, new E2Information(), EyeStatus.NAKADE)
        checkWhiteEye(b, new E2Information(), EyeStatus.NAKADE)
    }

    @Test
    def testFalseBasicEyeThreeDeep(): Unit = {
        var b: GoBoard = initializeBoard("false_basic_eye3")

        checkBlackEye(b, new E3Information(), EyeStatus.UNSETTLED)
        checkWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED)
    }

    @Test
    def testFalseBasicEyeFourDeep(): Unit = {
        var b: GoBoard = initializeBoard("false_basic_eye4")

        checkBlackEye(b, new E4Information(E4Information.Eye4Type.E1122.toString), EyeStatus.ALIVE)
        checkEdgeWhiteEye(b, new E4Information(E4Information.Eye4Type.E1122.toString), EyeStatus.ALIVE)
    }

    @Test
    def testFalseBasicEyeFiveDeep(): Unit = {
        var b: GoBoard = initializeBoard("false_basic_eye5")

        checkBlackEye(b, new E5Information(E11222.toString), EyeStatus.ALIVE)
        checkWhiteEye(b, new E5Information(E11222.toString), EyeStatus.ALIVE)
    }

    @Test
    def testFalseBasicEyeSixDeep(): Unit = {
        var b: GoBoard = initializeBoard("false_basic_eye6")

        checkCornerBlackEye(b, new E6Information(E112222.toString), EyeStatus.ALIVE)
        checkEdgeWhiteEye(b, new E6Information(E112222.toString), EyeStatus.ALIVE)
    }

}
