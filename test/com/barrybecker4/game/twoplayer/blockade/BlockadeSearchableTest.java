/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.ISearchableHelper;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerSearchableBaseTst;
import com.barrybecker4.optimization.parameter.ParameterArray;

import java.util.List;


/**
 * Verify that all the methods in blockadeSearchable work as expected
 * @author Barry Becker
 */
public class BlockadeSearchableTest extends TwoPlayerSearchableBaseTst {

    @Override
    protected ISearchableHelper createSearchableHelper() {
        return new BlockadeHelper();
    }

    /**
     * @return an initial move by player one.
     */
    @Override
    protected  TwoPlayerMove createInitialMove() {
        return new BlockadeMove(new ByteLocation(5, 5), new ByteLocation(5, 5),
                                0, new GamePiece(true),
                                new BlockadeWall(new BlockadeBoardPosition(1, 1), new BlockadeBoardPosition(2, 1)));
    }


    @Override
    protected int getDebugLevel() {
        return 0;
    }

    @Override
    public void testNotDoneMidGame() {
        // @@
        assertFalse(false);
    }

    @Override
    public void testDoneForMidGameWin() {
        // @@
        assertFalse(false);
    }

    @Override
    public void testDoneEndGame() {
        // @@
        assertFalse(false);
    }

    @Override
    protected int getExpectedNumGeneratedMovesBeforeFirstMove() {
       return 40;  //53 //101; // 144
   }

    /**  Load a game in the middle and verify that we can get reasonable next moves. */
    @Override
    public void testGenerateAllP1MovesMidGame() throws Exception {
         checkGeneratedMoves("middleGameP1Turn", ExpectedSearchableResults.EXPECTED_ALL_MIDDLE_GAME_MOVES_P1);
    }

    /**  Load a game in the middle and verify that we can get the expected high value next moves. */
    @Override
    public void testGenerateTopP1MovesMidGame() throws Exception {
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("middleGameP1Turn", ExpectedSearchableResults.EXPECTED_TOP_MIDDLE_GAME_MOVES_P1);
    }

    /**
      * Load a game at the end and verify that there are no valid next moves.
      * Of particular interest here is that we can generate moves that lead to a win.
      */
    @Override
    public void testGenerateAllP1MovesEndGame() throws Exception {
         checkGeneratedMoves("endGameP1Turn", ExpectedSearchableResults.EXPECTED_ALL_END_GAME_MOVES_P1);
    }

    /** Load a game at the end and verify that we can get all the high value next moves. */
    @Override
    public void testGenerateTopP1MovesEndGame() throws Exception{
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("endGameP1Turn", ExpectedSearchableResults.EXPECTED_TOP_END_GAME_MOVES_P1);
    }


    /**  Load a game in the middle and verify that we can get reasonable next moves. */
    @Override
    public void testGenerateAllP2MovesMidGame() throws Exception {
         checkGeneratedMoves("middleGameP2Turn", ExpectedSearchableResults.EXPECTED_ALL_MIDDLE_GAME_MOVES_P2);
    }

    /**  Load a game in the middle and verify that we can get the expected high value next moves. */
    @Override
    public void testGenerateTopP2MovesMidGame() throws Exception {
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("middleGameP2Turn", ExpectedSearchableResults.EXPECTED_TOP_MIDDLE_GAME_MOVES_P2);
    }

    /**
      * Load a game at the end and verify that there are no valid next moves.
      * Of particular interest here is that we can generate moves that lead to a win.
      */
    @Override
    public void testGenerateAllP2MovesEndGame() throws Exception {
         checkGeneratedMoves("endGameP2Turn", ExpectedSearchableResults.EXPECTED_ALL_END_GAME_MOVES_P2);
    }

     /** Load a game at the end and verify that we can get all the high value next moves. */
     @Override
     public void testGenerateTopP2MovesEndGame() throws Exception {
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("endGameP2Turn", ExpectedSearchableResults.EXPECTED_TOP_END_GAME_MOVES_P2);
    }


    private void checkGeneratedMoves(String fileName, BlockadeMove[] expectedMoves) throws Exception {
        restore(fileName);
        ParameterArray wts = weights();
        TwoPlayerMove lastMove = (TwoPlayerMove) getController().getLastMove();
        MoveList moves =
                getController().getSearchable().generateMoves(lastMove, wts);

        if (expectedMoves.length != moves.size()) {
            printMoves( fileName, moves);
        }

        assertEquals("Unexpected number of generated moves.",
                expectedMoves.length, moves.size());

        StringBuilder diffs = new StringBuilder("");
        for (int i=0; i<moves.size(); i++) {
            BlockadeMove move = (BlockadeMove) moves.get(i);
            BlockadeMove expMove = expectedMoves[i];
            if (!move.equals(expMove)) {
                diffs.append(i).append(") Unexpected moves.\n Expected ").append(expMove).
                        append(" \nBut got ").append(move).append("\n");
            }
        }
        if (diffs.length() > 0) {
            printMoves( fileName, moves);
        }
        assertTrue("There were unexpected generated moves for " + fileName +"\n" + diffs,
                    diffs.length()==0);
    }

    /**  Verify that we generate a correct list of urgent moves.  */
    @Override
    public void  testGenerateUrgentMoves() {
         // there should not be any urgent moves at the very start of the game
         List moves = searchable.generateUrgentMoves(null, weights());
         assertTrue("We expect move list to be empty since generateUrgentMoves is not implemented for Blockade.",
                 moves.isEmpty());
    }
}
