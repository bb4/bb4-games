/** Copyright by Barry G. Becker, 2000-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.math.Range;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions;
import org.junit.Before;
import org.junit.Test;

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

    @Test
    public void testBestMinOneP1() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createSimpleThreeMoveList(PLAYER1));
        assertEquals("Unexpected number of best moves found", 1, mList.getNumMoves());
        assertEquals("Unexpected value", 100, mList.getFirstMove().getValue());
    }

    @Test
    public void testBestMinOneP2() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createSimpleThreeMoveList(PLAYER2));
        assertEquals("Unexpected number of best moves found", 1, mList.getNumMoves());
        assertEquals("Unexpected value", -10, mList.getFirstMove().getValue());
    }

    @Test
    public void testBestMinTwo() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(2);

        MoveList mList = finder.getBestMoves(createSimpleThreeMoveList(PLAYER1));
        assertEquals("Unexpected number of best moves found", 2, mList.getNumMoves());
    }

    @Test
    public void testBestMinBiggerThanTotal() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(5);

        MoveList mList = finder.getBestMoves(createSimpleThreeMoveList(PLAYER1));
        assertEquals("Unexpected number of best moves found", 3, mList.getNumMoves());
    }

    @Test
    public void testMinBestSmallerThanNumberReturned() {
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
        assertEquals("Unexpected number of best moves found", 10, mList.getNumMoves());
        assertEquals("Unexpected value", 20, mList.getFirstMove().getValue());
    }

    @Test
    public void testBest25PercentLessThanBestThreshMovesP1WhenAllPositive() {
        options.setPercentLessThanBestThresh(25);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createRangeMoveList(new Range(1, 40), PLAYER1));
        System.out.println("moves="+ mList);
        assertEquals("Unexpected number of best moves found", 10, mList.getNumMoves());
        assertEquals("Unexpected value", 40, mList.getFirstMove().getValue());
    }

    @Test
    public void testBest20PercentLessThanBestThreshMovesP1() {
        options.setPercentLessThanBestThresh(20);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createRangeMoveList(new Range(-10, 10), PLAYER1));
        assertEquals("Unexpected number of best moves found", 4, mList.getNumMoves());
        assertEquals("Unexpected value", 10, mList.getFirstMove().getValue());
    }

    @Test
    public void testBest20PercentLessThanBestThreshMovesP2() {
        options.setPercentLessThanBestThresh(20);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(createRangeMoveList(new Range(-10, 10), PLAYER2));
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

     private MoveList createSimpleThreeMoveList(boolean player1) {
        MoveList moveList = new MoveList();
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 2), 100, new GamePiece(player1)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 3), 50, new GamePiece(player1)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 4), -10, new GamePiece(player1)));
        return moveList;
    }

    private MoveList createSimpleFiveMoveList(boolean player1) {
        MoveList moveList = new MoveList();
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 2), 1000, new GamePiece(player1)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 3), 50, new GamePiece(player1)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 4), 5, new GamePiece(player1)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 3), 0, new GamePiece(player1)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 3), -5, new GamePiece(player1)));
        return moveList;
    }

    private MoveList createMoveListWithDuplicateScores(boolean player1) {
        MoveList moveList = new MoveList();
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 2), 500, new GamePiece(player1)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 3), 500, new GamePiece(player1)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 4), 5, new GamePiece(player1)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 3), -1000, new GamePiece(player1)));
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
