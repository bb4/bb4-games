/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information;

import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer;

/**
 * Test that we can get the correct type and status for all the different eyes that can arise.
 *
 * @author Barry Becker
 */
public class TestFalseEyeInformation extends TestEyeTypeAnalyzer {

    @Override
    protected EyeType getEyeType() {
        return EyeType.FalseEye;
    }

    public void testFalseKoEye1() throws Exception {
        GoBoard b = initializeBoard("false_ko_eye1");

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.KO);
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.KO);
    }

    public void testFalseKoEye2() throws Exception {
        GoBoard b = initializeBoard("false_ko_eye2");

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.KO);
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.KO);
    }

    public void testFalseBasicEyeTwoDeep() throws Exception {
        GoBoard b = initializeBoard("false_basic_eye2");

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.KO);
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.KO);
    }

    public void testFalseBasicEyeThreeDeep() throws Exception {
        GoBoard b = initializeBoard("false_basic_eye3");

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED);
    }

    public void testFalseBasicEyeFourDeep() throws Exception {
        GoBoard b = initializeBoard("false_basic_eye4");

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED);
        checkEdgeWhiteEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED);
    }

    public void testFalseBasicEyeFiveDeep() throws Exception {
        GoBoard b = initializeBoard("false_basic_eye5");

        checkBlackEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED);
        checkWhiteEye(b, new FalseEyeInformation(), EyeStatus.UNSETTLED);
    }

    public void testFalseBasicEyeSixDeep() throws Exception {
        GoBoard b = initializeBoard("false_basic_eye6");

        checkCornerBlackEye(b, new FalseEyeInformation(), EyeStatus.NAKADE);
        checkEdgeWhiteEye(b, new FalseEyeInformation(), EyeStatus.NAKADE);
    }
}
