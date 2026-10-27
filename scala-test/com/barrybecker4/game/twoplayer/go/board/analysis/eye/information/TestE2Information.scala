/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer

class TestE2Information extends TestEyeTypeAnalyzer {


    override protected def getEyeType: EyeType = EyeType.E2

    @Test
    def testTwoSpaceEye1(): Unit = {
        var b: GoBoard = initializeBoard("two_space_eye1")

        checkBlackEye(b, new E2Information(), EyeStatus.NAKADE)
        checkWhiteEye(b, new E2Information(), EyeStatus.NAKADE)
    }

    /** checks edge and corner */
    @Test
    def testTwoSpaceEyeOnEdge1(): Unit = {
        var b: GoBoard = initializeBoard("two_space_eye_on_edge1")

        checkBlackEye(b, new E2Information(), EyeStatus.NAKADE)
        checkWhiteEye(b, new E2Information(), EyeStatus.NAKADE)
    }

}
