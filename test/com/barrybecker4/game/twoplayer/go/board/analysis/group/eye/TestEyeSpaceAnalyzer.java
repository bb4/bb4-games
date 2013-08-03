// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye;

import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E1Information;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeStatus;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeType;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.TerritorialEyeInformation;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap;
import com.barrybecker4.game.twoplayer.go.board.elements.eye.GoEyeSet;
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;

import java.util.Arrays;

/**
 * Verify that we come up with reasonable eye potential values (likelihood of making eyes in the group).
 *
 * @author Barry Becker
 */
public class TestEyeSpaceAnalyzer extends GoTestCase {

    private static final String PREFIX = "board/analysis/group/eye/eyespace/";


    public void testEyeSpace_SingleIsolatedStone() throws Exception {

        restoreGame("single_isolated_stone");

        verifyBlackEyes(createEyeSet());
        verifyWhiteEyes(createEyeSet());
    }

    public void testEyeSpace_SingleEye() throws Exception {

        restoreGame("single_eye");

        IGoEye blackEye = new StubGoEye(true, EyeStatus.NAKADE, new E1Information(), 0, 0, false, 1);
        IGoEye whiteEye = new StubGoEye(false, EyeStatus.NAKADE, new E1Information(), 0, 1, false, 1);
        verifyBlackEyes(createEyeSet(blackEye));
        verifyWhiteEyes(createEyeSet(whiteEye));
    }

    /**
     * For black eye we have:
     *   C
     *   CCE
     * and for white we have
     *   ECC
     * where C's are counted as being edges as well as corner.
     */
    public void testEyeSpace_EyesInCorner() throws Exception {

        restoreGame("eyes_in_corner");

        IGoEye blackEye = new StubGoEye(true, EyeStatus.ALIVE, EyeType.E6.getInformation("E222233"), 3, 4, false, 6);
        IGoEye whiteEye = new StubGoEye(false, EyeStatus.UNSETTLED, EyeType.E3.getInformation("E112"), 2, 3, false, 3);
        verifyBlackEyes(createEyeSet(blackEye));
        verifyWhiteEyes(createEyeSet(whiteEye));
    }

    public void testEyeSpace_EyesInCornerComplex() throws Exception {

        restoreGame("eyes_in_corner_complex");

        IGoEye blackEye = new StubGoEye(true, EyeStatus.ALIVE, EyeType.E7.getInformation("E1122222"), 3, 6, false, 7);
        IGoEye whiteEye = new StubGoEye(false, EyeStatus.ALIVE, new TerritorialEyeInformation(), 3, 5, false, 8);
        verifyBlackEyes(createEyeSet(blackEye));
        verifyWhiteEyes(createEyeSet(whiteEye));
    }

    public void testEyeSpace_TwoEyesUnconditional() throws Exception {

        restoreGame("two_unconditional_eyes");

        IGoEye blackEye1 = new StubGoEye(true, EyeStatus.NAKADE, EyeType.E1.getInformation("E1"), 0, 0, false, 1);
        IGoEye blackEye2 = new StubGoEye(true, EyeStatus.NAKADE, EyeType.E1.getInformation("E1"), 1, 1, false, 1);
        IGoEye whiteEye1 = new StubGoEye(false, EyeStatus.NAKADE, EyeType.E1.getInformation("E1"), 0, 0, false, 1);
        IGoEye whiteEye2 = new StubGoEye(false, EyeStatus.NAKADE, EyeType.E1.getInformation("E1"), 0, 0, false, 1);

        verifyBlackEyes(createEyeSet(blackEye1, blackEye2));
        verifyWhiteEyes(createEyeSet(whiteEye1, whiteEye2));
    }


    public void testEyeSpace_TunnelButNoEye() throws Exception {

        restoreGame("tunnels_no_eyes");

        verifyBlackEyes(createEyeSet());
        verifyWhiteEyes(createEyeSet());
    }

    public void testEyeSpace_tunnelButNoEye2() throws Exception {

        restoreGame("tunnels_no_eyes2");

        verifyBlackEyes(createEyeSet());
        verifyWhiteEyes(createEyeSet());
    }

    public void testEyeSpace_tunnelButNoEye3() throws Exception {

        restoreGame("tunnels_no_eyes2");

        verifyBlackEyes(createEyeSet());
        verifyWhiteEyes(createEyeSet());
    }

    public void testEyeSpace_tunnelsWithEyes() throws Exception {

        restoreGame("tunnels_with_eyes");

        // This eye (if it is one) does not get noticed because it is a really odd edge case.
        //IGoEye blackEye = new StubGoEye(true, EyeStatus.ALIVE, new TerritorialEyeInformation(), 5, 15, false, 15);
        IGoEye whiteEye1 = new StubGoEye(false, EyeStatus.ALIVE, new TerritorialEyeInformation(), 3, 5, false, 9);
        IGoEye whiteEye2 = new StubGoEye(false, EyeStatus.ALIVE, EyeType.E5.getInformation("E11222"), 0, 1, false, 5);

        verifyBlackEyes(createEyeSet());
        verifyWhiteEyes(createEyeSet(whiteEye1, whiteEye2));
    }

    public void testEyeSpace_tunnelsWithEyes2() throws Exception {

        restoreGame("tunnels_with_eyes2");

        IGoEye blackEye = new StubGoEye(true, EyeStatus.ALIVE, new TerritorialEyeInformation(), 4, 14, false, 14);
        IGoEye whiteEye1 = new StubGoEye(false, EyeStatus.ALIVE, EyeType.E7.getInformation("E1122222"), 3, 5, false, 7);
        IGoEye whiteEye2 = new StubGoEye(false, EyeStatus.NAKADE, new E1Information(), 0, 0, false, 1);
        IGoEye whiteEye3 = new StubGoEye(false, EyeStatus.ALIVE, EyeType.E5.getInformation("E11222"), 0, 1, false, 5);

        verifyBlackEyes(createEyeSet(blackEye));
        verifyWhiteEyes(createEyeSet(whiteEye1, whiteEye2, whiteEye3));
    }


    private void restoreGame(String file) throws Exception {
        restore(PREFIX + file);
    }

    private void verifyBlackEyes(GoEyeSet expectedEyes) {
        verifyEyes(true, expectedEyes);
    }

    private void verifyWhiteEyes(GoEyeSet expectedEyes) {
        verifyEyes(false, expectedEyes);
    }


    private GoEyeSet createEyeSet(IGoEye... eyes) {
        GoEyeSet eyeList = new GoEyeSet();
        eyeList.addAll(Arrays.asList(eyes));
        return eyeList;
    }

    /**
     * Use EyeSpaceAnalyzer to find eyes and match against the expected set of eyes.
     */
    private void verifyEyes(boolean forBlackGroup, GoEyeSet expectedEyes) {

        IGoGroup group = getBiggestGroup(forBlackGroup);

        EyeSpaceAnalyzer analyzer = new EyeSpaceAnalyzer(group, new GroupAnalyzerMap());
        analyzer.setBoard(getBoard());
        GoEyeSet eyes = analyzer.determineEyes();

        boolean matched = compareEyeSets(expectedEyes, eyes);
        if (!matched) {
            System.err.println("we expected: \n" + expectedEyes);
            System.err.println("but got: \n" + eyes);
        }
        assertTrue("The actual eyes did not match the expected.", matched);
    }

    /**
     * @return true if both sets of eyes are the same
     */
    private boolean compareEyeSets(GoEyeSet expEyes, GoEyeSet actEyes) {

        assertEquals("Unexpected number of eyes in group.", expEyes.size(), actEyes.size());

        // assuming the number of eyes match, check for 1-1 correspondence
        boolean allFound = true;
        for (IGoEye actEye : actEyes) {
            boolean found = false;
            for (IGoEye eye : expEyes)  {
                StubGoEye expEye = (StubGoEye) eye;
                if (expEye.isMatch(actEye)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                allFound = false;
                break;
            }
        }
        return allFound;
    }

}
