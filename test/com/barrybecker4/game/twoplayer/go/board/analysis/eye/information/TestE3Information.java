/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information;

import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer;

/**
 * Test that we can get the correct type and status for all the different 3 eyes that can arise.
 *
 * @author Barry Becker
 */
public class TestE3Information extends TestEyeTypeAnalyzer {

    @Override
    protected EyeType getEyeType() {
        return EyeType.E3;
    }

    public void testThreeSpaceEye() throws Exception {
        GoBoard b = initializeBoard("three_space_eye");

        checkBlackEye(b, new E3Information(), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED);
    }

    public void testThreeSpaceEyeBent() throws Exception {
        GoBoard b = initializeBoard("three_space_eye_bent");

        checkBlackEye(b, new E3Information(), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED);
    }

    public void testThreeSpaceEyeOnEdge() throws Exception {
        GoBoard b = initializeBoard("three_space_eye_on_edge");

        checkEdgeBlackEye(b, new E3Information(), EyeStatus.UNSETTLED);
        checkEdgeWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED);
    }

    public void testThreeSpaceEyeInCorner() throws Exception {
        GoBoard b = initializeBoard("three_space_eye_in_corner");

        checkCornerBlackEye(b, new E3Information(), EyeStatus.UNSETTLED);
        checkCornerWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED);
    }

    public void testThreeSpaceEyeKilled() throws Exception {
        GoBoard b = initializeBoard("three_space_eye_killed", 4);

        checkWhiteEye(b, new E3Information(), EyeStatus.NAKADE);
        checkBlackEye(b, new E3Information(), EyeStatus.NAKADE);
    }

    public void testThreeSpaceEyeBentKilled() throws Exception {
        GoBoard b = initializeBoard("three_space_eye_bent_killed", 4);

        checkBlackEye(b, new E3Information(), EyeStatus.NAKADE);
        checkWhiteEye(b, new E3Information(), EyeStatus.NAKADE);
    }

    public void testThreeSpaceEyeOnEdgeKilled() throws Exception {
        GoBoard b = initializeBoard("three_space_eye_on_edge_killed", 4);

        checkEdgeBlackEye(b, new E3Information(), EyeStatus.NAKADE);
        checkEdgeWhiteEye(b, new E3Information(), EyeStatus.NAKADE);
    }

    public void testThreeSpaceEyeInCornerKilled() throws Exception {
        GoBoard b = initializeBoard("three_space_eye_in_corner_killed", 4);

        checkCornerBlackEye(b, new E3Information(), EyeStatus.NAKADE);
        checkCornerWhiteEye(b, new E3Information(), EyeStatus.NAKADE);
    }

    public void testThreeSpaceEyeWithTwoFilled() throws Exception {
        GoBoard b = initializeBoard("three_space_eye_two_filled", 4);

        checkBlackEye(b, new E3Information(), EyeStatus.NAKADE);
        checkWhiteEye(b, new E3Information(), EyeStatus.NAKADE);
    }

    public void testThreeSpaceEyeWithTwoEndsFilled() throws Exception {
        GoBoard b = initializeBoard("three_space_eye_two_ends_filled", 4);

        checkBlackEye(b, new E3Information(), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED);
    }


    public void testThreeSpaceEyeWithTwoFilledInAtari() throws Exception {
        GoBoard b = initializeBoard("three_space_eye_two_filled_in_atari", 4);

        checkBlackEye(b, new E3Information(), EyeStatus.NAKADE);
        checkWhiteEye(b, new E3Information(), EyeStatus.NAKADE);
    }

    public void testThreeSpaceEyeWithTwoEndsFilledInAtari() throws Exception {
        GoBoard b = initializeBoard("three_space_eye_two_ends_filled_in_atari", 5);

        checkBlackEye(b, new E3Information(), EyeStatus.UNSETTLED);
        //checkWhiteEye(b, new E3Information(), EyeStatus.UNSETTLED); can't make in atari
    }
}
