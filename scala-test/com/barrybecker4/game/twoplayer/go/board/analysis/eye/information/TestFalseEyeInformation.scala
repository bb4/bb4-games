/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer

class TestFalseEyeInformation extends TestEyeTypeAnalyzer {


    override protected def getEyeType: EyeType = EyeType.FalseEye

    @Test
    def testFalseKoEye1(): Unit = {
        var b: GoBoard = initializeBoard("false_ko_eye1")

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.KO)
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.KO)
    }

    @Test
    def testFalseKoEye2(): Unit = {
        var b: GoBoard = initializeBoard("false_ko_eye2")

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.KO)
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.KO)
    }

    @Test
    def testFalseBasicEyeTwoDeep(): Unit = {
        var b: GoBoard = initializeBoard("false_basic_eye2")

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.KO)
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.KO)
    }

    @Test
    def testFalseBasicEyeThreeDeep(): Unit = {
        var b: GoBoard = initializeBoard("false_basic_eye3")

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED)
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED)
    }

    @Test
    def testFalseBasicEyeFourDeep(): Unit = {
        var b: GoBoard = initializeBoard("false_basic_eye4")

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED)
        checkEdgeWhiteEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED)
    }

    @Test
    def testFalseBasicEyeFiveDeep(): Unit = {
        var b: GoBoard = initializeBoard("false_basic_eye5")

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED)
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED)
    }

    @Test
    def testFalseBasicEyeSixDeep(): Unit = {
        var b: GoBoard = initializeBoard("false_basic_eye6")

        checkCornerBlackEye(b, new FalseEyeInformation(), EyeStatus.NAKADE)
        checkEdgeWhiteEye(b, new FalseEyeInformation(), EyeStatus.NAKADE)
    }

}
