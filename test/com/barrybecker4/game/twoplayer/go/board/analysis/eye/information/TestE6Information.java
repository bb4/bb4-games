/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information;

import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer;

import static com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.E6Information.Eye6Type.E112222;

/**
 * Test that we can get the correct type and status for all the different 6 space eyes that can arise.
 *
 * @author Barry Becker
 */
public class TestE6Information extends TestEyeTypeAnalyzer {

    @Override
    protected EyeType getEyeType() {
        return EyeType.E6;
    }

    //////   E112222  (just a sampling of the cases)

    public void testSixStraightEye() throws Exception {
        GoBoard b = initializeBoard("six_straight_eye", 2);

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
    }

    public void testSixBentEye() throws Exception {
        GoBoard b = initializeBoard("six_bent_eye", 2);

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
    }

    public void testVerySixBentEye() throws Exception {
        GoBoard b = initializeBoard("six_very_bent_eye", 2);

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
    }

    public void testSixBentOneMiddleFilled() throws Exception {
        GoBoard b = initializeBoard("six_bent_one_middle_filled", 4);

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
    }

    public void testSixBentTwoMiddleFilled() throws Exception {
        GoBoard b = initializeBoard("six_bent_two_middle_filled", 4);

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
    }

    public void testSixBentThreeMiddleFilled() throws Exception {
        GoBoard b = initializeBoard("six_bent_three_middle_filled", 4);

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
    }

    public void testSixBentFourMiddleFilled() throws Exception {
        GoBoard b = initializeBoard("six_bent_four_middle_filled", 4);

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
    }

    public void testSixBentTwoEndsFilled() throws Exception {
        GoBoard b = initializeBoard("six_bent_two_ends_filled", 6);

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
    }


    public void testSixBentFiveFilled() throws Exception {
        GoBoard b = initializeBoard("six_bent_five_filled", 4);

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
        checkWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
    }

    public void testSixBentOnEdge() throws Exception {
        GoBoard b = initializeBoard("six_bent_on_edge", 2);

        checkEdgeBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
        checkEdgeWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
    }

    public void testSixBentOnEdgeThreeFilled() throws Exception {
        GoBoard b = initializeBoard("six_bent_on_edge_three_filled", 4);

        checkEdgeBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
        checkEdgeWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
    }


    public void testSixBentInCorner() throws Exception {
        GoBoard b = initializeBoard("six_bent_in_corner", 2);

        checkCornerBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
        checkCornerWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
    }

    /** This may be an interesting case that needs more study. */
    public void testSixBentInCornerKoFilled() throws Exception {
        GoBoard b = initializeBoard("six_bent_in_corner_ko_filled", 4);

        checkCornerBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
        checkCornerWhiteEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE);
    }

    public void testSixBlackAliveInAtari() throws Exception {
        GoBoard b = initializeBoard("six_black_alive_in_atari", 4);

        checkBlackEye(b, new E6Information(E112222.toString()), EyeStatus.ALIVE_IN_ATARI);
        checkWhiteEye(b, new TerritorialEyeInformation(), EyeStatus.ALIVE);
    }



    //////   E111223   (just a sampling of the cases)


    //   TODO
    //////   E112233
    //////   E122223
    //////   E222233
    //////   E112224
    //////   E111124
    //////   E111133

}
