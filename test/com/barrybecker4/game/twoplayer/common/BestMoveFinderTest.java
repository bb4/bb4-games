/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.math.Range;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions;
import junit.framework.TestCase;

/**
 * @author Barry Becker
 */
public class BestMoveFinderTest extends TestCase {

    private BestMoveFinder finder;

    private BestMovesSearchOptions options;

    private static final boolean PLAYER1 = true;
    private static final boolean PLAYER2 = false;

    /**
     * Constructor
     */
    public BestMoveFinderTest() {
    }

    /**
     * common initialization for all test cases.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        options = new BestMovesSearchOptions();
        finder = new BestMoveFinder(options);
    }

    public void testBestMinOneP1() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(PLAYER1, createSimpleThreeMoveList());
        assertEquals("Unexpected number of best moves found", 1, mList.getNumMoves());
        assertEquals("Unexpected value", 100, mList.getFirstMove().getValue());
    }

    public void testBestMinOneP2() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(PLAYER2, createSimpleThreeMoveList());
        assertEquals("Unexpected number of best moves found", 1, mList.getNumMoves());
        assertEquals("Unexpected value", -10, mList.getFirstMove().getValue());
    }

    public void testBestMinTwo() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(2);

        MoveList mList = finder.getBestMoves(PLAYER1, createSimpleThreeMoveList());
        assertEquals("Unexpected number of best moves found", 2, mList.getNumMoves());
    }

    public void testBestMinBiggerThanTotal() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(5);

        MoveList mList = finder.getBestMoves(PLAYER1, createSimpleThreeMoveList());
        assertEquals("Unexpected number of best moves found", 3, mList.getNumMoves());
    }

    public void testBest20PercentMovesP1() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(20);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(PLAYER1, createRangeMoveList(new Range(-10, 10)));
        assertEquals("Unexpected number of best moves found", 4, mList.getNumMoves());
        assertEquals("Unexpected value", 10, mList.getFirstMove().getValue());
    }

    public void testBest20PercentMovesP2() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(20);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(PLAYER2, createRangeMoveList(new Range(-10, 10)));
        assertEquals("Unexpected number of best moves found", 4, mList.getNumMoves());
        assertEquals("Unexpected value", -10, mList.getFirstMove().getValue());
    }

    public void testBest20PercentLessThanBestThreshMovesP1() {
        options.setPercentLessThanBestThresh(20);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(PLAYER1, createRangeMoveList(new Range(-10, 10)));
        assertEquals("Unexpected number of best moves found", 4, mList.getNumMoves());
        assertEquals("Unexpected value", 10, mList.getFirstMove().getValue());
    }

    public void testBest20PercentLessThanBestThreshMovesP2() {
        options.setPercentLessThanBestThresh(20);
        options.setPercentageBestMoves(0);
        options.setMinBestMoves(1);

        MoveList mList = finder.getBestMoves(PLAYER2, createRangeMoveList(new Range(-10, 10)));
        assertEquals("Unexpected number of best moves found", 4, mList.getNumMoves());
        assertEquals("Unexpected value", -10, mList.getFirstMove().getValue());
    }

    private MoveList createSimpleThreeMoveList() {
        MoveList moveList = new MoveList();
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 2), 100, new GamePiece(true)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 3), 50, new GamePiece(true)));
        moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(1, 4), -10, new GamePiece(true)));
        return moveList;
    }

    public void testBestMinMoreThanPercentMovesP1() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(20);
        options.setMinBestMoves(10);

        MoveList mList = finder.getBestMoves(PLAYER1, createRangeMoveList(new Range(-10, 10)));
        assertEquals("Unexpected number of best moves found", 10, mList.getNumMoves());
    }

    public void testBestMinMoreThanPercentLessThanBestThreshMovesP2() {
        options.setPercentLessThanBestThresh(0);
        options.setPercentageBestMoves(20);
        options.setMinBestMoves(10);

        MoveList mList = finder.getBestMoves(PLAYER2, createRangeMoveList(new Range(-10, 10)));
        assertEquals("Unexpected number of best moves found", 10, mList.getNumMoves());
    }

    /**
     * @return creates a move with every value in the range
     */
    private MoveList createRangeMoveList(Range range) {
        MoveList moveList = new MoveList();
        for (int i=(int)range.getMin(); i<=range.getMax(); i++) {
             moveList.add(TwoPlayerMoveStub.createMove(new ByteLocation(i / 40, i % 40), i, new GamePiece(true)));
        }
        return moveList;
    }

}
