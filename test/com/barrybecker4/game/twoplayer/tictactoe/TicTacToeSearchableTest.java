/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.ISearchableHelper;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerSearchableBaseTst;
import org.junit.Test;

import static com.barrybecker4.game.twoplayer.tictactoe.ExpectedSearchableResults.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Verify that all the methods in GoMokuSearchable work as expected
 * @author Barry Becker
 */
public class TicTacToeSearchableTest extends TwoPlayerSearchableBaseTst {

    @Override
    protected ISearchableHelper createSearchableHelper() {
        return new TicTacToeHelper();
    }

    /**
     * @return an initial move by player one.
     */
    @Override
    protected  TwoPlayerMove createInitialMove() {
        return  TwoPlayerMove.createMove(2, 2,  0, new GamePiece(true));
    }

    @Override
    protected int getDebugLevel() {
        return 0;
    }

    /** at the very start only the center move is a candidate move */
    @Override
    protected int getExpectedNumGeneratedMovesBeforeFirstMove() {
       return 9;
   }

    @Override
    public void testNotDoneMidGame() throws Exception {
        restore("midGameCenterO");
        assertFalse("Did not expect done in the middle of the game. ",
                searchable.done((TwoPlayerMove)getController().getLastMove(), false));
    }

    @Override
    public void testDoneForMidGameWin() throws Exception {
        restore("wonGameO");
        assertTrue("Expected done state for this game. ",
                searchable.done((TwoPlayerMove)getController().getLastMove(), false));
    }

    /** Load a game at the last move and verify that the next move results in done == true  */
    @Override
    public void testDoneEndGame() throws Exception {
         restore("endGameNoMoreMoves");
        assertTrue("Expected done state for this game because there are no more moves. ",
                searchable.done((TwoPlayerMove)getController().getLastMove(), false));
    }

    /** Load a game in the middle and verify that we can get reasonable next moves. */
    @Override
    public void testGenerateAllP1MovesMidGame() throws Exception {
        checkGeneratedMoves("midGameCenterX", EXPECTED_ALL_MIDDLE_GAME_MOVES_CENTER_P1);
        checkGeneratedMoves("midGameCornerX", EXPECTED_ALL_MIDDLE_GAME_MOVES_CORNER_P1);
        checkGeneratedMoves("midGameEdgeX", EXPECTED_ALL_MIDDLE_GAME_MOVES_EDGE_P1);
    }

    /** Load a game in the middle and verify that we can get the expected high value next moves. */
    @Override
    public void testGenerateTopP1MovesMidGame() throws Exception {
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("midGameCenterX", EXPECTED_TOP_MIDDLE_GAME_MOVES_CENTER_P1);
        checkGeneratedMoves("midGameCornerX", EXPECTED_TOP_MIDDLE_GAME_MOVES_CORNER_P1);
        checkGeneratedMoves("midGameEdgeX", EXPECTED_TOP_MIDDLE_GAME_MOVES_EDGE_P1);
    }

    /**
      * Load a game at the end and verify that there are no valid next moves.
      * Of particular interest here is that we can generate moves that lead to a win.
      */
    @Override
    public void testGenerateAllP1MovesEndGame() throws Exception {
         checkGeneratedMoves("endGameX", EXPECTED_ALL_END_GAME_MOVES_P1);
    }

    /** Load a game at the end and verify that we can get all the high value next moves. */
    @Override
    public void testGenerateTopP1MovesEndGame() throws Exception {
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("endGameX", EXPECTED_TOP_END_GAME_MOVES_P1);
    }

    /**  Load a game in the middle and verify that we can get reasonable next moves. */
    @Override
    public void testGenerateAllP2MovesMidGame() throws Exception {
        checkGeneratedMoves("midGameCenterO", EXPECTED_ALL_MIDDLE_GAME_MOVES_CENTER_P2);
        checkGeneratedMoves("midGameCornerO", EXPECTED_ALL_MIDDLE_GAME_MOVES_CORNER_P2);
        checkGeneratedMoves("midGameEdgeO", EXPECTED_ALL_MIDDLE_GAME_MOVES_EDGE_P2);
    }

    /**  Load a game in the middle and verify that we can get the expected high value next moves. */
    @Override
    public void testGenerateTopP2MovesMidGame() throws Exception {
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("midGameCenterO", EXPECTED_TOP_MIDDLE_GAME_MOVES_CENTER_P2);
        checkGeneratedMoves("midGameCornerO", EXPECTED_TOP_MIDDLE_GAME_MOVES_CORNER_P2);
        checkGeneratedMoves("midGameEdgeO", EXPECTED_TOP_MIDDLE_GAME_MOVES_EDGE_P2);
    }

    /**
     * Load a game at the end and verify generated endgame moves.
     * Of particular interest here is that we can generate moves that lead to a win.
     */
    @Override
    public void testGenerateAllP2MovesEndGame() throws Exception {
        checkGeneratedMoves("endGameO", EXPECTED_ALL_END_GAME_MOVES_P2);
    }

    /** Load a game at the end and verify that we can get all the high value next moves. */
    @Override
    public void testGenerateTopP2MovesEndGame() throws Exception {
        checkGeneratedMoves("endGameO", EXPECTED_TOP_END_GAME_MOVES_P2);
    }

    /**  Verify that we generate a correct list of urgent moves.  */
    @Override
    public void testGenerateUrgentMoves() throws Exception {

        restore("urgentMoves");
        // there should not be any urgent moves at the very start of the game.
        System.out.println("lastMove="+getController().getLastMove() );   // 1,1
        MoveList moves =
            searchable.generateUrgentMoves((TwoPlayerMove)getController().getLastMove(), weights());

        checkMoveListAgainstExpected("urgentMoves", EXPECTED_URGENT_MOVES, moves);
    }

    /**  Verify that we generate a correct list of urgent moves for the other player.  */
    @Test
    public void testGenerateUrgentMovesP2() throws Exception {

        restore("urgentMoves");
        // there should not be any urgent moves at the very start of the game.
        System.out.println("lastMove=" + getController().getLastMove() );   // 1,1
        MoveList moves =
            searchable.generateUrgentMoves((TwoPlayerMove)getController().getLastMove(), weights());

        checkMoveListAgainstExpected("urgentMoves: " + moves, EXPECTED_URGENT_MOVES, moves);
    }


    private void checkGeneratedMoves(String fileName, TwoPlayerMove[] expectedMoves) throws Exception {
        restore(fileName);
        TwoPlayerMove lastMove = (TwoPlayerMove) getController().getLastMove();
        MoveList moves =
                getController().getSearchable().generateMoves(lastMove, weights());

        checkMoveListAgainstExpected(fileName, expectedMoves, moves);
    }
}
