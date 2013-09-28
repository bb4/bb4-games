/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.pente;

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
    public void testCandidatesWhenNoMoves() throws Exception {

        PenteBoard board = new PenteBoard(10, 10);

        candidates = board.getCandidateMoves();

        assertTrue("Missing candidate move.", candidates.isCandidateMove(6, 6));
        assertFalse("Unexpected candidate.", candidates.isCandidateMove(5, 6));
        assertFalse("Unexpected candidate.", candidates.isCandidateMove(4, 7));
    }

    /** The 8 spaces surrounding should be candidates. */
    @Test
    public void testCandidatesWhenOneCenterMove() throws Exception {

        PenteBoard board = new PenteBoard(10, 10);
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(3, 3), 0, new GamePiece(true)));

        candidates = board.getCandidateMoves();

        assertTrue("Missing candidate move.", candidates.isCandidateMove(4, 4));
        assertTrue("Missing candidate move.", candidates.isCandidateMove(2, 3));
        assertTrue("Missing candidate move.", candidates.isCandidateMove(3, 2));
        assertFalse("Unexpected candidate.", candidates.isCandidateMove(5, 6));
        assertFalse("Unexpected candidate.", candidates.isCandidateMove(4, 7));
    }

}
