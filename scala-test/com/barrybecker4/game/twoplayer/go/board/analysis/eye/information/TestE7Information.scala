/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import org.junit.Assert
import org.junit.Before
import org.junit.After
import org.junit.Test
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E7Information.Eye7Type.E1122222

class TestE7Information extends TestEyeTypeAnalyzer {


    override protected def getEyeType: EyeType = EyeType.E7

    @Test
    def testSevenStraightSpaceEye(): Unit = {
        var b: GoBoard = initializeBoard("seven_space_straight_eye", 2)

        checkBlackEye(b, new E7Information(E1122222.toString()), EyeStatus.ALIVE)
        checkWhiteEye(b, new E7Information(E1122222.toString()), EyeStatus.ALIVE)
    }


    // TODO


}
