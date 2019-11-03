/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information;

import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer;

/**
 * Test that we can get the correct type and status for single point eyes.
 *
 * @author Barry Becker
 */
public class TestE1Information extends TestEyeTypeAnalyzer {

    @Override
    protected EyeType getEyeType() {
        return EyeType.E1;
    }

    public void testSingleSpaceEye1() throws Exception {
        GoBoard b = initializeBoard("single_space_eye1");

        checkBlackEye(b, new E1Information(), EyeStatus.NAKADE);
        checkWhiteEye(b, new E1Information(), EyeStatus.NAKADE);
    }

    /** test edge and corner */
    public void testSingleSpaceEyeOnEdge1() throws Exception {
        GoBoard b = initializeBoard("single_space_on_edge_eye1");

        checkBlackEye(b, new E1Information(), EyeStatus.NAKADE);
        checkWhiteEye(b, new E1Information(), EyeStatus.NAKADE);
    }
}
