/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer

class TestE1Information extends TestEyeTypeAnalyzer {


    override protected def getEyeType: EyeType = EyeType.E1

    @Test
    def testSingleSpaceEye1(): Unit = {
        var b: GoBoard = initializeBoard("single_space_eye1")

        checkBlackEye(b, new E1Information(), EyeStatus.NAKADE)
        checkWhiteEye(b, new E1Information(), EyeStatus.NAKADE)
    }

    /** test edge and corner */
    @Test
    def testSingleSpaceEyeOnEdge1(): Unit = {
        var b: GoBoard = initializeBoard("single_space_on_edge_eye1")

        checkBlackEye(b, new E1Information(), EyeStatus.NAKADE)
        checkWhiteEye(b, new E1Information(), EyeStatus.NAKADE)
    }

}
