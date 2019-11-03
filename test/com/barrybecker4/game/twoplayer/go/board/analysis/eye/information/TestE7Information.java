/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information;

import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer;

import static com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E7Information.Eye7Type.E1122222;

/**
 * Test that we can get the correct type and status for all the different 7 space eyes that can arise.
 *
 * @author Barry Becker
 */
public class TestE7Information extends TestEyeTypeAnalyzer {

    @Override
    protected EyeType getEyeType() {
        return EyeType.E7;
    }

    public void testSevenStraightSpaceEye() throws Exception {
        GoBoard b = initializeBoard("seven_space_straight_eye", 2);

        checkBlackEye(b, new E7Information(E1122222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E7Information(E1122222.toString()), EyeStatus.ALIVE);
    }


    // TODO

}
