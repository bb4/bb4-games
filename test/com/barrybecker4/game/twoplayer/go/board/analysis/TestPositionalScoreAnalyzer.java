/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.PositionalScore;
import com.barrybecker4.game.twoplayer.go.options.GoWeights;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Test positional score analysis.
 *
 * @author Barry Becker
 */
public class TestPositionalScoreAnalyzer extends GoTestCase {

    /** we can just reuse one of the other file sets */
    private static final String PREFIX = "board/analysis/scoring/";

    private GoWeights goWeights;

    private PositionalScoreAnalyzer scoreAnalyzer_;

    private static final double TOLERANCE = 0.001;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        goWeights = new GoWeights();
        ParameterArray params = goWeights.getPlayer1Weights();
        for (int i=0; i<params.size(); i++) {
            params.get(i).setValue(2.0);
        }

        goWeights.setPlayer1Weights(params);
    }

    /**
     *  | XO
     *  | X
     *  |
     *  |     O
     */
    public void testOccupiedPositionalScoreNoEye() throws Exception {

        initializeBoard("positional_score_no_eye");

        PositionalScore totalScore = PositionalScore.createZeroScore();
                                                                            //    badShp  posScore health
        verifyPositionalScore(new ByteLocation(2,2), PositionalScore.createOccupiedScore(0.0, 0.025, 0.0), totalScore);
        verifyPositionalScore(new ByteLocation(2,3), PositionalScore.createOccupiedScore(0.0, -0.016667, 0.0), totalScore);
        verifyPositionalScore(new ByteLocation(3,3), PositionalScore.createOccupiedScore(0.0, 0.0, 0.0), totalScore);
        verifyPositionalScore(new ByteLocation(3,2), PositionalScore.createOccupiedScore(0.0, 0.01667, 0.0), totalScore);

        verifyScoresEqual(PositionalScore.createOccupiedScore(0.0, 0.025, 0.0), totalScore);
        assertEquals("Unexpected final position score.  ",
                0.025, totalScore.getPositionScore(), TOLERANCE);
    }

    public void testOverallPositionalScoreNoEye() throws Exception {

        initializeBoard("positional_score_no_eye");
        verifyExpectedOverallScore(0.025f);
    }

    /**
     *  |   X
     *  |  OX
     *  |XXXX
     *  |
     *  |                  O
     */
    public void testOccupiedPositionalScoreStoneInEye() throws Exception {

        initializeBoard("positional_score_stone_in_eye");

        PositionalScore totalScore = PositionalScore.createZeroScore();
                                                                         //    badShp  posScore health
        verifyPositionalScore(new ByteLocation(2,2), PositionalScore.createOccupiedScore(0.0, 0.0, 0.0), totalScore);
        verifyPositionalScore(new ByteLocation(2,3), PositionalScore.createOccupiedScore(0.0, -0.01667, 0.0), totalScore);
        verifyPositionalScore(new ByteLocation(3,2), PositionalScore.createOccupiedScore(0.0, 0.01667, 0.0), totalScore);
        verifyPositionalScore(new ByteLocation(3,3), PositionalScore.createOccupiedScore(0.0, 0.25, 0.0), totalScore);

        verifyScoresEqual(PositionalScore.createOccupiedScore(0.0, 0.25, 0.0), totalScore);
        assertEquals("Unexpected final position score.  ",
                0.25, totalScore.getPositionScore(), TOLERANCE);
    }

    public void testOverallPositionalScoreInEye() throws Exception {

        initializeBoard("positional_score_stone_in_eye");
        verifyExpectedOverallScore(-0.0666667f);
    }

    /**
     *  | X    OO |
     *  |XXX   O O|
     *  |  X   OO |
     *  |XX
     */
    public void testOccupiedPositionalAlive() throws Exception {

        initializeBoard("positional_score_alive");

        PositionalScore totalScore = PositionalScore.createZeroScore();
                                                                           //       badShp  posScore health
        verifyPositionalScore(new ByteLocation(2,2), PositionalScore.createOccupiedScore(-1.0f, 0.025f, 0.0f), totalScore);
        verifyPositionalScore(new ByteLocation(2,3), PositionalScore.createOccupiedScore(-0.666667f, .01666667f, 0.0f), totalScore);
        verifyPositionalScore(new ByteLocation(3,3), PositionalScore.createOccupiedScore(-0.333333f, 0.25f, 0.0f), totalScore);
        verifyPositionalScore(new ByteLocation(3,2), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore);

        verifyPositionalScore(new ByteLocation(8,2), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore);
        verifyPositionalScore(new ByteLocation(8,3), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore);
        verifyPositionalScore(new ByteLocation(9,3), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore);
        verifyPositionalScore(new ByteLocation(9,2), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore);

        verifyScoresEqual(PositionalScore.createOccupiedScore(-2.0f, 0.2917f, 0.0f), totalScore);
        assertEquals("Unexpected final position score.  ",
                -1.7083f, totalScore.getPositionScore(), TOLERANCE);
    }

    public void testOverallPositionalScoreAlive() throws Exception {

        initializeBoard("positional_score_alive");
        verifyExpectedOverallScore(-0.975f);
    }

    /**
     * @param file saved sgf game file to load
     */
    protected void initializeBoard(String file) throws Exception {
        restore(PREFIX + file);

        GoBoard board = getBoard();
        scoreAnalyzer_ = new PositionalScoreAnalyzer(board);
    }

    /**
     * Verify candidate move generation.
     */
    private void verifyPositionalScore(Location loc, PositionalScore expScore, PositionalScore totalScore) {

        PositionalScore actScore =
                scoreAnalyzer_.determineScoreForPosition(loc.getRow(), loc.getCol(),
                        goWeights.getPlayer1Weights());
        verifyScoresEqual(expScore, actScore);
        totalScore.incrementBy(actScore);
    }

    private void verifyScoresEqual(PositionalScore expScore, PositionalScore actScore) {
        assertEquals("Unexpected eye space score. " + actScore,
                expScore.getEyeSpaceScore(), actScore.getEyeSpaceScore(), TOLERANCE);
        assertEquals("Unexpected posScore. " + actScore,
                expScore.getPosScore(), actScore.getPosScore(), TOLERANCE);
        assertEquals("Unexpected badShape score. " + actScore,
                expScore.getBadShapeScore(), actScore.getBadShapeScore(), TOLERANCE);
        assertEquals("Unexpected deadStone score.  " + actScore,
                expScore.getDeadStoneScore(), actScore.getDeadStoneScore(), TOLERANCE);
        assertEquals("Unexpected health score.  " + actScore,
                expScore.getHealthScore(), actScore.getHealthScore(), TOLERANCE);
    }


    private void verifyExpectedOverallScore(float expectedScore)  {

        int size = getBoard().getNumRows();

        PositionalScore score = PositionalScore.createZeroScore();

        for (int row=1; row<=size; row++) {
            for (int col=1; col<=size; col++) {
               score.incrementBy(scoreAnalyzer_.determineScoreForPosition(row, col, goWeights.getPlayer1Weights()));
            }
        }
        assertEquals(expectedScore, score.getPositionScore(), TOLERANCE);
    }
}
