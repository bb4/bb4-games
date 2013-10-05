/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.pente;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.ISearchableHelper;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerSearchableBaseTst;

import static com.barrybecker4.game.twoplayer.pente.ExpectedSearchableResults.*;


/**
 * Verify that all the methods in PenteSearchable work as expected
 * @author Barry Becker
 */
public class PenteSearchableTest extends TwoPlayerSearchableBaseTst {

    @Override
    protected ISearchableHelper createSearchableHelper() {
        return new PenteHelper();
    }

    /**
     * @return an initial move by player one.
     */
    @Override
    protected TwoPlayerMove createInitialMove() {
        return TwoPlayerMove.createMove(5, 5,   0, new GamePiece(true));
    }

    /** At the very start, only the center move is a candidate move */
    @Override
    protected int getExpectedNumGeneratedMovesBeforeFirstMove() {
       return 1;
   }

    @Override
    public void testNotDoneMidGame() throws Exception {
        restore("midGameP1ToPlay");
        assertFalse("Did not expect done in the middle of the game. ",
                searchable.done((TwoPlayerMove)getController().getLastMove(), false));
    }

    public void testAlmostWonP1ToPlay() throws Exception {
        restore("almostWonP1ToPlay");
        assertFalse("Did not expect done even though very close. ",
                searchable.done((TwoPlayerMove)getController().getLastMove(), false));
    }

    public void testAlmostWonP2ToPlay() throws Exception {
        restore("almostWonP2ToPlay");
        assertFalse("Did not expect done even though very close. ",
                searchable.done((TwoPlayerMove)getController().getLastMove(), false));
    }

    /** Expected done here because there are 5 in a row and score should exceed the win threshold. */
    @Override
    public void testDoneForMidGameWin() throws Exception {
        restore("wonGameP1");
        assertTrue("Expected done state for this game. P1 should have won.",
                searchable.done((TwoPlayerMove)getController().getLastMove(), false));

        restore("wonGameP2");
        assertTrue("Expected done state for this game. P2 should have won.",
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
        checkGeneratedMoves("midGameP1ToPlay8x8", EXPECTED_ALL_MIDDLE_GAME_MOVES_P1);
    }

    /** Load a game in the middle and verify that we can get the expected high value next moves. */
    @Override
    public void testGenerateTopP1MovesMidGame() throws Exception {
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("midGameP1ToPlay8x8", EXPECTED_TOP_MIDDLE_GAME_MOVES_P1);
    }

    /**
      * Load a game at the end and verify the generated endgame moves.
      * Of particular interest here is that we can generate moves that lead to a win.
      */
    @Override
    public void testGenerateAllP1MovesEndGame() throws Exception {
         checkGeneratedMoves("endGameP1ToPlay8x8", EXPECTED_ALL_END_GAME_MOVES_P1);
    }

    /** Load a game at the end and verify that we can get all the high value next moves. */
    @Override
    public void testGenerateTopP1MovesEndGame() throws Exception {
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("endGameP1ToPlay8x8", EXPECTED_TOP_END_GAME_MOVES_P1);
    }

    /**  Load a game in the middle and verify that we can get reasonable next moves. */
    @Override
    public void testGenerateAllP2MovesMidGame() throws Exception {
        checkGeneratedMoves("midGameP2ToPlay8x8", EXPECTED_ALL_MIDDLE_GAME_MOVES_P2);
    }

    /**  Load a game in the middle and verify that we can get the expected high value next moves. */
    @Override
    public void testGenerateTopP2MovesMidGame() throws Exception {
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("midGameP2ToPlay8x8", EXPECTED_TOP_MIDDLE_GAME_MOVES_P2);
    }

    /**
     * Load a game at the end and verify that there are no valid next moves.
     * Of particular interest here is that we can generate moves that lead to a win.
     */
    @Override
    public void testGenerateAllP2MovesEndGame() throws Exception {
        checkGeneratedMoves("endGameP2ToPlay8x8", EXPECTED_ALL_END_GAME_MOVES_P2);
    }

     /** Load a game at the end and verify that we can get all the high value next moves. */
     @Override
     public void testGenerateTopP2MovesEndGame() throws Exception {
        getBestMovesOptions().setPercentageBestMoves(20);
        checkGeneratedMoves("endGameP2ToPlay8x8", EXPECTED_TOP_END_GAME_MOVES_P2);
    }

    /**  Verify that we generate a correct list of urgent moves.  */
    @Override
    public void testGenerateUrgentMoves() throws Exception {

        restore("urgentMoveP1ToPlay");
        // there should not be any urgent moves at the very start of the game.
        MoveList moves =
            searchable.generateUrgentMoves((TwoPlayerMove)getController().getLastMove(), weights());

        checkMoveListAgainstExpected("urgentMoveP1ToPlay", EXPECTED_URGENT_MOVES_P1, moves);
    }

    /**  Verify that we generate a correct list of urgent moves.  */
    public void testGenerateUrgentMovesP2() throws Exception {

        restore("urgentMoveP2ToPlay");
        // there should not be any urgent moves at the very start of the game.
        MoveList moves =
            searchable.generateUrgentMoves((TwoPlayerMove)getController().getLastMove(), weights());

        checkMoveListAgainstExpected("urgentMoveP2ToPlay", EXPECTED_URGENT_MOVES_P2, moves);
    }


    private void checkGeneratedMoves(String fileName, TwoPlayerMove[] expectedMoves) throws Exception {
        restore(fileName);
        TwoPlayerMove lastMove = (TwoPlayerMove) getController().getLastMove();
        MoveList moves =
                getController().getSearchable().generateMoves(lastMove, weights());

        checkMoveListAgainstExpected(fileName, expectedMoves, moves);
    }
}
