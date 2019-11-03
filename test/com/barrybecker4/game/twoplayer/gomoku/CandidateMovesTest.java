/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * @author Barry Becker
 */
public class CandidateMovesTest {

    /** instance under test */
    private CandidateMoves candidates;

    /** The center position should be a candidate if no moves have been played yet. */
    @Test
    public void testCandidatesWhenEmpty10x10Board() throws Exception {

        GoMokuBoard board = new GoMokuBoard(10, 10);

        candidates = board.getCandidateMoves();

        assertTrue("Missing candidate move.", candidates.isCandidateMove(6, 6));
        assertFalse("Unexpected candidate.", candidates.isCandidateMove(5, 6));
        assertFalse("Unexpected candidate.", candidates.isCandidateMove(4, 7));
    }

    /** The center position should be a candidate if no moves have been played yet. */
    @Test
    public void testCandidatesWhenEmpty2x2Board() throws Exception {

        GoMokuBoard board = new GoMokuBoard(2, 2);

        candidates = board.getCandidateMoves();

        assertTrue("Missing candidate move.", candidates.isCandidateMove(2, 2));
        assertFalse("Unexpected candidate.", candidates.isCandidateMove(1, 2));
        assertFalse("Unexpected candidate.", candidates.isCandidateMove(2, 1));
        assertFalse("Unexpected candidate.", candidates.isCandidateMove(1, 1));
    }

    /** No candidates if the board is competely full. */
    @Test
    public void testCandidatesWhenNoMoves() throws Exception {

        GoMokuBoard board = new GoMokuBoard(2, 2);
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(1, 1), 0, new GamePiece(true)));
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(false)));
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(1, 2), 0, new GamePiece(true)));
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, new GamePiece(false)));

        candidates = board.getCandidateMoves();

        assertFalse("Unexpected candidate.", candidates.isCandidateMove(2, 2));
        assertFalse("Unexpected candidate.", candidates.isCandidateMove(1, 2));
        assertFalse("Unexpected candidate.", candidates.isCandidateMove(1, 1));
        assertFalse("Unexpected candidate.", candidates.isCandidateMove(2, 1));
    }

    /** The 8 spaces surrounding should be candidates. */
    @Test
    public void testCandidatesWhenOneCenterMove() throws Exception {

        GoMokuBoard board = new GoMokuBoard(10, 10);
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(3, 3), 0, new GamePiece(true)));

        candidates = board.getCandidateMoves();

        assertTrue("Missing candidate move.", candidates.isCandidateMove(4, 4));
        assertTrue("Missing candidate move.", candidates.isCandidateMove(2, 3));
        assertTrue("Missing candidate move.", candidates.isCandidateMove(3, 2));
        assertFalse("Unexpected candidate.", candidates.isCandidateMove(5, 6));
        assertFalse("Unexpected candidate.", candidates.isCandidateMove(4, 7));
    }

    /** This is a typical case. */
    @Test
    public void testCandidatesWhenTwoMovesPresent() throws Exception {

        GoMokuBoard board = new GoMokuBoard(10, 10);
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(3, 3), 0, new GamePiece(true)));
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(4, 3), 0, new GamePiece(false)));

        candidates = board.getCandidateMoves();

        assertTrue("Missing candidate move.", candidates.isCandidateMove(2, 2));
        assertTrue("Missing candidate move.", candidates.isCandidateMove(2, 3));
        assertTrue("Missing candidate move.", candidates.isCandidateMove(3, 2));
        assertTrue("Missing candidate move.", candidates.isCandidateMove(5, 3));
        assertTrue("Missing candidate move.", candidates.isCandidateMove(5, 4));
        assertFalse("Unexpected candidate.", candidates.isCandidateMove(6, 6));
        assertFalse("Unexpected candidate.", candidates.isCandidateMove(1, 7));
    }

}
