/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.transposition;

import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;
import junit.framework.TestCase;

/**
 * Various uniqueness tests for hash keys.
 * @author Barry Becker
 */
public abstract class HashGoBase extends TestCase {

    protected ZobristHash hash;
    protected GoBoard board;


    @Override
    public void setUp() {
        board = createBoard();
        hash = createZobristHash();
    }

    protected abstract GoBoard createBoard();

    /**
     * In actual play I found two boards that generated the same hash key. Verify that this does not happen again.
     */
    public void verifyGoBoardSame(GoMove[] moves1, GoMove[] moves2) {

        compareGoBoards(moves1, moves2, true);
    }

    /**
     * In actual play I found two boards that generated the same hash key. Verify that this does not happen again.
     */
    public void verifyGoBoardsDistinct(GoMove[] moves1, GoMove[] moves2) {

        compareGoBoards(moves1, moves2, false);
    }

    /**
     * In actual play I found two boards that generated the same hash key. Verify that this does not happen again.
     * @param assertEqual if true, then verify equal, else verify not equal.
     */
    public void compareGoBoards(GoMove[] moves1, GoMove[] moves2, boolean assertEqual) {

        applyMoves(moves1);
        HashKey key1 = hash.getKey().copy();

        // apply them again to reset
        applyMoves(moves1);

        board.reset();

        applyMoves(moves2);
        HashKey key2 = hash.getKey();

        if (assertEqual) {
            assertEquals("Keys not equal", key1,  key2);
        } else {
            assertTrue("Keys unexpectedly equal. \nkey1="+key1+"\nkey2="+key2,
                    !key1.equals(key2));
        }
    }


    protected GoStone black() {
        return new GoStone(true);
    }

    protected GoStone white() {
        return new GoStone(false);
    }

    protected void applyMoves(GoMove[] moves) {
        for (GoMove move : moves) {
            board.makeMove(move);
            applyMoveToHash(board.getPosition(move.getToLocation()));
            if (move.getNumCaptures() > 0) {
                for (BoardPosition capture : move.getCaptures()) {
                    GamePiece oldPiece = capture.getPiece();
                    capture.setPiece(move.isPlayer1() ? white() : black());
                    applyMoveToHash(capture);
                    capture.setPiece(oldPiece);
                }
            }
        }
        System.out.println();
    }

    private void applyMoveToHash(BoardPosition position) {
        int stateIndex = board.getStateIndex(position);
        System.out.print("stateIndex=" + stateIndex + " for " + position + "   ");
        hash.applyMove(position.getLocation(), stateIndex);
        System.out.println(" hash after apply="+ hash.getKey().getKey());
    }

    private ZobristHash createZobristHash() {
        return new ZobristHash(board, 0, false);
    }
}
