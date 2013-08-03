/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information;

import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer;

/**
 * Test that we can get the correct type and status for all the different two space eyes that can arise.
 *
 * @author Barry Becker
 */
public class TestE2Information extends TestEyeTypeAnalyzer {

    @Override
    protected EyeType getEyeType() {
        return EyeType.E2;
    }

    public void testTwoSpaceEye1() throws Exception {
        GoBoard b = initializeBoard("two_space_eye1");

        checkBlackEye(b, new E2Information(), EyeStatus.NAKADE);
        checkWhiteEye(b, new E2Information(), EyeStatus.NAKADE);
    }

    /** checks edge and corner */
    public void testTwoSpaceEyeOnEdge1() throws Exception {
        GoBoard b = initializeBoard("two_space_eye_on_edge1");

        checkBlackEye(b, new E2Information(), EyeStatus.NAKADE);
        checkWhiteEye(b, new E2Information(), EyeStatus.NAKADE);
    }
}