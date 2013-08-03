/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.transposition;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;
import com.barrybecker4.game.twoplayer.tictactoe.TicTacToeBoard;
import junit.framework.TestCase;

/**
 * Verify expected hash key are generated based on board state.
 * @author Barry Becker
 */
public class ZobristHashTest extends TestCase {

    private static final HashKey CENTER_X_HASH = new HashKey(428667830982598836L);
    private static final HashKey CORNER_O_HASH = new HashKey(-6688467811848818630L);

    private ZobristHash hash;
    private TwoPlayerBoard board;

    @Override
    public void setUp() {
        board = new TicTacToeBoard();
        createZHash(board);
    }

    public void testEmptyBoardHash() {
        assertEquals("Unexpected hashkey for empty board", new HashKey(), hash.getKey());
    }

    public void testCenterXHash() {
        applyMoveToHash(2, 2, true);
        assertEquals("Unexpected hashkey for board with center X",
                CENTER_X_HASH, hash.getKey());
    }

    public void testCenterXBoard() {
        TwoPlayerMove m = TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(true));
        board.makeMove(m);

        hash = createZHash(board);
        assertEquals("Unexpected hashkey for board with center X",
                CENTER_X_HASH, hash.getKey());
    }

    public void testCornerOHash() {
        applyMoveToHash(1, 1, false);
        assertEquals("Unexpected hashkey for board with corner O",
                CORNER_O_HASH, hash.getKey());
    }

    public void testCornerOBoard() {
        TwoPlayerMove m = TwoPlayerMove.createMove(new ByteLocation(1, 1), 0, new GamePiece(false));
        board.makeMove(m);
        hash = createZHash(board);
        assertEquals("Unexpected hashkey for board with corner O",
                CORNER_O_HASH, hash.getKey());
    }

    public void testHashAfterUndo() {

        applyMoveToHash(2, 2, true);
        applyMoveToHash(2, 2, true);
        assertEquals("Unexpected hashkey for entry board after undo",
                new HashKey(), hash.getKey());
    }


    public void testHashAfterTwoMoves() {

        applyMoveToHash(2, 2, true);
        applyMoveToHash(1, 1, false);
        assertEquals("Unexpected hashkey for board after 2 moves",
                new HashKey(-6422371760107745138L), hash.getKey());
    }

    public void testHashAfterTwoMovesThenUndoFirst() {

        applyMoveToHash(2, 2, true);
        applyMoveToHash(1, 1, false);

        applyMoveToHash(2, 2, true);
        assertEquals("Unexpected hashkey for board after 2 moves then an undo",
                CORNER_O_HASH, hash.getKey());
    }

    public void testHashAfterTwoMovesThenUndoSecond() {

        applyMoveToHash(2, 2, true);
        applyMoveToHash(1, 1, false);

        applyMoveToHash(1, 1, false);
        assertEquals("Unexpected hashkey for board after 2 moves then an undo",
                CENTER_X_HASH, hash.getKey());
    }

    public void testHashAfterTwoMovesThenUndoMoveO() {

        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(1, 1), 0, new GamePiece(false)));
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(true)));

        board.undoMove();
        hash = createZHash(board);
        assertEquals("Unexpected hashkey for board after 2 moves then an undo move",
                CORNER_O_HASH, hash.getKey());
    }

    public void testHashAfterTwoMovesThenUndoMoveX() {

        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(true)));
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(1, 1), 0, new GamePiece(false)));

        board.undoMove();
        hash = createZHash(board);
        assertEquals("Unexpected hashkey for board after 2 moves then an undo move",
                CENTER_X_HASH, hash.getKey());
    }


    public void testHashAfterTwoMovesThenTwoUndos() {

        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(true)));
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(1, 1), 0, new GamePiece(false)));

        board.undoMove();
        board.undoMove();
        hash = createZHash(board);
        assertEquals("Unexpected hashkey for board after 2 moves then two undos",
                new HashKey(), hash.getKey());
    }

    public void testHashesForDifferentBoardsStatesUnequal() {
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(true)));
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(1, 1), 0, new GamePiece(false)));
        hash = createZHash(board);
        HashKey hash1 = hash.getKey();

        board = new TicTacToeBoard();
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(true)));
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(3, 1), 0, new GamePiece(false)));
        hash = createZHash(board);

        assertFalse("Hash keys for different moves unexpectedly equal. both=" + hash1,
                hash1.equals(hash.getKey()));
    }

    public void testHashesForDifferentBoardsStatesUnequalUsingUndo() {
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(true)));
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(1, 1), 0, new GamePiece(false)));
        hash = createZHash(board);
        HashKey hash1 = hash.getKey();

        board.undoMove();
        board.undoMove();
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(2, 2), 0, new GamePiece(true)));
        board.makeMove(TwoPlayerMove.createMove(new ByteLocation(3, 1), 0, new GamePiece(false)));
        hash = createZHash(board);

        assertFalse("Hash keys for different moves unexpectedly equal. both=" + hash1,
                hash1.equals(hash.getKey()));
    }

    public void testGoHash()  {
        GoBoard board = new GoBoard(5, 0);
        board.makeMove(new GoMove(new ByteLocation(3, 3), 0, new GoStone(true)));
        board.makeMove(new GoMove(new ByteLocation(1, 3), 0, new GoStone(false)));
        hash = createZHash(board);

        assertEquals("Unexpected hashkey for GoBoard",
             new HashKey(-5179128285022783026L), hash.getKey());
    }

    private void applyMoveToHash(int row, int col, boolean player1) {
        GamePiece p = new GamePiece(player1);
        Location loc = new ByteLocation(row, col);
        TwoPlayerMove m = TwoPlayerMove.createMove(loc, 0, p);
        int stateIndex = board.getStateIndex(new BoardPosition(row, col, p));
        hash.applyMove(loc, stateIndex);
    }

    private ZobristHash createZHash(TwoPlayerBoard board) {
        hash = new ZobristHash(board, 0, true);
        return hash;
    }
}
