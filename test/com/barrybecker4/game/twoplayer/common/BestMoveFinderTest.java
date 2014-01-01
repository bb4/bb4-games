/** Copyright by Barry G. Becker, 2000-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.math.Range;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

/**
 * @author Barry Becker
 */
public class BestMoveFinderTest {

    private BestMoveFinder finder;

    private BestMovesSearchOptions options;

    private static final boolean PLAYER1 = true;
    private static final boolean PLAYER2 = false;

    /**
     * common initialization for all test cases.
     */
    @Before
    public void setUp()  {
        options = new BestMovesSearchOptions();
        finder = new BestMoveFinder(options);
    }

    /**
     * if all the thresholds are set to 0, we still get one move (teh best one)
     * because 0 percent less than the best is the best
     */
    @Test
    public void testNoMovesReturnedWhenThresholds0() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createSimpleThreeMoveList(PLAYER2));
        assertEquals("Unexpected number of best moves found", 1, mList.getNumMoves());
    }

    /**
     * If there are no moves to select from, then 0 will be returned.
     */
    @Test
    public void testNoMovesReturnedWhenNoMovesToSelectFrom() {
        options.setPercentLessThanBestThresh(1);
        options.setPercentageBestMoves(1);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(new MoveList());
        assertEquals("Unexpected number of best moves found", 0, mList.getNumMoves());
    }

    @Test
    public void testMinBestOfOneP1() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createSimpleThreeMoveList(PLAYER1));
        assertEquals("Unexpected number of best moves found", 1, mList.getNumMoves());
        assertEquals("Unexpected value", 100, mList.getFirstMove().getValue());
    }

    @Test
    public void testMinBestOfOneP2() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createSimpleThreeMoveList(PLAYER2));
        assertEquals("Unexpected number of best moves found", 1, mList.getNumMoves());
        assertEquals("Unexpected value", -10, mList.getFirstMove().getValue());
    }

    @Test
    public void testMinBestTwoP1() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(2);

        MoveList mList = finder.getBestMoves(createSimpleThreeMoveList(PLAYER1));
        assertEquals("Unexpected number of best moves found", 2, mList.getNumMoves());
        assertEquals("Unexpected value", 100, mList.getFirstMove().getValue());
    }

    @Test
    public void testMinBestTwoP2() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(2);

        MoveList mList = finder.getBestMoves(createSimpleThreeMoveList(PLAYER2));
        assertEquals("Unexpected number of best moves found", 2, mList.getNumMoves());
        assertEquals("Unexpected value", -10, mList.getFirstMove().getValue());
    }

    @Test
    public void testBestMinBiggerThanTotalP1() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(5);

        MoveList mList = finder.getBestMoves(createSimpleThreeMoveList(PLAYER1));
        assertEquals("Unexpected number of best moves found", 3, mList.getNumMoves());
    }

    @Test
    public void testBestMinBiggerThanTotalP2() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(5);

        MoveList mList = finder.getBestMoves(createSimpleThreeMoveList(PLAYER2));
        assertEquals("Unexpected number of best moves found", 3, mList.getNumMoves());
    }

    @Test
    public void testMinBestSmallerThanNumberReturnedP1() {
        options.setPercentLessThanBestThresh(50);
        options.setPercentageBestMoves(100);
        options.setMinBestMoves(2);

        MoveList mList = finder.getBestMoves(createSimpleThreeMoveList(PLAYER1));
        assertEquals("Unexpected number of best moves found", 3, mList.getNumMoves());
    }

    @Test
    public void testBest20PercentMovesP1() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(20);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createRangeMoveList(new Range(-10, 10), PLAYER1));
        assertEquals("Unexpected number of best moves found", 4, mList.getNumMoves());
        assertEquals("Unexpected value", 10, mList.getFirstMove().getValue());
    }

    @Test
    public void testBest20PercentMovesP2() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(20);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createRangeMoveList(new Range(-10, 10), PLAYER2));
        assertEquals("Unexpected number of best moves found", 4, mList.getNumMoves());
        assertEquals("Unexpected value", -10, mList.getFirstMove().getValue());
    }

    @Test
    public void testBest25PercentLessThanBestThreshMovesP1() {
        options.setPercentLessThanBestThresh(25);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createRangeMoveList(new Range(-20, 20), PLAYER1));
        assertEquals("Unexpected number of best moves found", 11, mList.getNumMoves());
        assertEquals("Unexpected value", 20, mList.getFirstMove().getValue());
    }

    @Test
    public void testBest25PercentLessThanBestThreshMovesP1WhenAllPositive() {
        options.setPercentLessThanBestThresh(25);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createRangeMoveList(new Range(1, 40), PLAYER1));
        assertEquals("Unexpected number of best moves found", 10, mList.getNumMoves());
        assertEquals("Unexpected value", 40, mList.getFirstMove().getValue());
    }

    @Test
    public void testBest25PercentLessThanBestThreshMovesP2WhenAllPositive() {
        options.setPercentLessThanBestThresh(25);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createRangeMoveList(new Range(1, 40), PLAYER2));
        assertEquals("Unexpected number of best moves found", 10, mList.getNumMoves());
        assertEquals("Unexpected value", 1, mList.getFirstMove().getValue());
    }

    @Test
    public void testBest25PercentLessThanBestThreshMovesP1WhenAllNegative() {
        options.setPercentLessThanBestThresh(25);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createRangeMoveList(new Range(-40, 1), PLAYER1));
        assertEquals("Unexpected number of best moves found", 11, mList.getNumMoves());
        assertEquals("Unexpected value", 1, mList.getFirstMove().getValue());
    }

    @Test
    public void testBest25PercentLessThanBestThreshMovesP2WhenAllNegative() {
        options.setPercentLessThanBestThresh(25);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createRangeMoveList(new Range(-40, 1), PLAYER2));
        assertEquals("Unexpected number of best moves found", 11, mList.getNumMoves());
        assertEquals("Unexpected value", -40, mList.getFirstMove().getValue());
    }

    @Test
    public void testBest20PercentLessThanBestThreshMovesP1() {
        options.setPercentLessThanBestThresh(20);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createRangeMoveList(new Range(-10, 9), PLAYER1));
        assertEquals("Unexpected number of best moves found", 4, mList.getNumMoves());
        assertEquals("Unexpected value", 9, mList.getFirstMove().getValue());
    }

    @Test
    public void testBest20PercentLessThanBestThreshMovesP2() {
        options.setPercentLessThanBestThresh(20);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createRangeMoveList(new Range(-10, 9), PLAYER2));
        assertEquals("Unexpected number of best moves found", 4, mList.getNumMoves());
        assertEquals("Unexpected value", -10, mList.getFirstMove().getValue());
    }

    @Test
    public void testBestMinMoreThanPercentMovesP1() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(20);
        options.setMinBestMoves(10);

        MoveList mList = finder.getBestMoves(createRangeMoveList(new Range(-10, 10), PLAYER1));
        assertEquals("Unexpected number of best moves found", 10, mList.getNumMoves());
    }

    @Test
    public void testBestMinMoreThanPercentLessThanBestThreshMovesP2() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(20);
        options.setMinBestMoves(10);

        MoveList mList = finder.getBestMoves(createRangeMoveList(new Range(-10, 10), PLAYER1));
        assertEquals("Unexpected number of best moves found", 10, mList.getNumMoves());
    }

    @Test
    public void test50PercentLessThanBest5P1() {
        options.setPercentLessThanBestThresh(50);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createSimpleFiveMoveList(PLAYER1));
        assertEquals("Unexpected number of best moves found", 1, mList.getNumMoves());
        assertEquals("Unexpected value", 1000, mList.getFirstMove().getValue());
    }

    @Test
    public void test50PercentLessThanBest5P2() {
        options.setPercentLessThanBestThresh(50);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createSimpleFiveMoveList(PLAYER2));
        assertEquals("Unexpected number of best moves found", 4, mList.getNumMoves());
        assertEquals("Unexpected value", -5, mList.getFirstMove().getValue());
    }

    @Test
    public void test50PercentLessThanBestWithDuplicatesP1() {
        options.setPercentLessThanBestThresh(50);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createMoveListWithDuplicateScores(PLAYER1));
        assertEquals("Unexpected number of best moves found", 1, mList.getNumMoves());
        assertEquals("Unexpected value", 1000, mList.getFirstMove().getValue());
    }

    @Test
    public void test50PercentLessThanBestWithDuplicatesP2() {
        options.setPercentLessThanBestThresh(50);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createMoveListWithDuplicateScores(PLAYER2));
        assertEquals("Unexpected number of best moves found", 3, mList.getNumMoves());
        assertEquals("Unexpected value", 5, mList.getFirstMove().getValue());
    }

    @Test
    public void testGetAllWhen100PercentLessThanBestP1() {
        options.setPercentLessThanBestThresh(100);
        options.setPercentageBestMoves(1);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createSimpleFiveMoveList(PLAYER1));
        assertEquals("Unexpected number of best moves found", 5, mList.getNumMoves());
    }

    @Test
    public void testGetAllWhen100PercentLessThanBestP2() {
        options.setPercentLessThanBestThresh(100);
        options.setPercentageBestMoves(1);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createSimpleFiveMoveList(PLAYER2));
        assertEquals("Unexpected number of best moves found", 5, mList.getNumMoves());
    }

     private MoveList createSimpleThreeMoveList(boolean player1) {
          MoveList moveList = new MoveList();
          moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 3), 50, new GamePiece(player1)));
          moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 2), 100, new GamePiece(player1)));
          moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 4), -10, new GamePiece(player1)));
          return moveList;
    }

    private MoveList createSimpleFiveMoveList(boolean player1) {
        MoveList moveList = new MoveList();
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 3), 0, new GamePiece(player1)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 2), 1000, new GamePiece(player1)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 3), 50, new GamePiece(player1)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 3), -5, new GamePiece(player1)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 4), 5, new GamePiece(player1)));
        return moveList;
    }

    private MoveList createMoveListWithDuplicateScores(boolean player1) {
        MoveList moveList = new MoveList();
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 2), 500, new GamePiece(player1)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 3), 1000, new GamePiece(player1)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 3), 500, new GamePiece(player1)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 4), 5, new GamePiece(player1)));
        return moveList;
    }


    /**
     * @return creates a move with every value in the range
     */
    private MoveList createRangeMoveList(Range range, boolean player1) {
        MoveList moveList = new MoveList();
        for (int i = (int) range.getMin(); i <= range.getMax(); i++) {
             moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(i / 40, i % 40), i, new GamePiece(player1)));
        }
        return moveList;
    }

}
