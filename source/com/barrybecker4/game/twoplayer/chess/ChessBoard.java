/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess;

import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.CaptureList;
import com.barrybecker4.game.twoplayer.checkers.CheckersBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;

import java.util.List;


/**
 * Defines the structure of the Chess board and the pieces on it.
 * Chess is played on a CheckersBoard so we derive from that.
 * @see CheckersBoard
 *
 * @author Barry Becker
 */
public class ChessBoard extends TwoPlayerBoard<ChessMove> {

    public static final int SIZE = 8;

    /** arrangement of pieces on the back line. */
    private static final ChessPieceType[] PIECE_ARRANGEMENT = {
        ChessPieceType.ROOK, ChessPieceType.KNIGHT, ChessPieceType.BISHOP,
        ChessPieceType.QUEEN, ChessPieceType.KING,
        ChessPieceType.BISHOP, ChessPieceType.KNIGHT, ChessPieceType.ROOK
    };

    public ChessBoard() {
        setSize(SIZE, SIZE);
    }

    /** Copy constructor */
    public ChessBoard(ChessBoard b) {
        super(b);
    }

    @Override
    public ChessBoard copy() {
        return new ChessBoard(this);
    }

    /**
     * If a checkers game has more than this many moves, then we assume it is a draw.
     */
    @Override
    public int getMaxNumMoves() {
        return 4 * SIZE * SIZE;
    }

    /**
     * reset the board to its initial state.
     */
    @Override
    public void reset() {
        super.reset();
        fillRows();
    }

    /**
     * reset the board to its initial state.
     */
    protected void fillRows() {
        setupPlayerPieces(true); // player1
        setupPlayerPieces(false); // player2
    }

    /**
     * Lay down the initial pieces at the start of the game.
     * @param isPlayer1 true if black pieces to be laid down.
     */
    private void setupPlayerPieces(boolean isPlayer1) {
        int numRows = getNumRows();
        int pawnRow = isPlayer1 ? 2 : numRows - 1;
        int kingRow = isPlayer1 ? 1 : numRows;
        for ( int j = 1; j <= getNumCols(); j++ ) {
            setPosition(new BoardPosition( kingRow, j, new ChessPiece(isPlayer1, PIECE_ARRANGEMENT[j-1])));
            setPosition(new BoardPosition( pawnRow, j, new ChessPiece(isPlayer1, ChessPieceType.PAWN)));
        }
    }

    /**
     * determine if the specified opponent position is endangering your king.
     * @param pos the opponent position to misc
     * @return true if the king is in check as a result of the last move.
     */
    public boolean isKingCheckedByPosition(BoardPosition pos, ChessMove lastMove)
    {
        boolean checked = false;

        if (pos.isUnoccupied())
            return false;
        List<ChessMove> moves = ((ChessPiece)pos.getPiece()).findPossibleMoves(this, pos.getRow(), pos.getCol(), lastMove);

        // loop through the possible moves.
        // if any of them capture the king then the opponents king is in check.
        for (ChessMove move : moves) {

            CaptureList cl = move.captureList;
            if (null != cl && !cl.isEmpty()) {
                ChessPiece piece = (ChessPiece)cl.getFirst().getPiece();
                if (piece.is(ChessPieceType.KING)) {
                    checked = true;
                    break;
                }
            }
        }
        return checked;
    }


    /**
     * given a move specification, execute it on the board.
     * This places the players symbol at the position specified by move.
     * @param move to make
     */
    @Override
    protected boolean makeInternalMove( ChessMove move )
    {

        BoardPosition oldPos = getPosition(move.getFromRow(), move.getFromCol());
        BoardPosition newPos = getPosition(move.getToRow(), move.getToCol());

        // remove the captures before we place the moved piece since it may be underneath.
        removeCaptures( move.captureList );

        if (oldPos.getPiece() != null) {
            move.setFirstTimeMoved(((ChessPiece) oldPos.getPiece()).isFirstTimeMoved());
            newPos.setPiece(move.getPiece());

            // once its been moved its no longer the first time its been moved
            ((ChessPiece)newPos.getPiece()).setFirstTimeMoved(false);

            getPosition(move.getFromRow(), move.getFromCol()).clear();
        }
        return true;
    }

    /**
     * for chess, undoing a move means moving the piece back and
     * restoring any captures.
     * @param move to undo
     */
    @Override
    protected void undoInternalMove( ChessMove move )
    {

        BoardPosition start = getPosition(move.getFromRow(), move.getFromCol());
        start.setPiece(move.getPiece());

        getPosition(move.getToRow(), move.getToCol()).clear();
        // restore the firstTimeMoved status of the piece since we
        // may be moving it back to its original position.
        ((ChessPiece)start.getPiece()).setFirstTimeMoved(move.isFirstTimeMoved());

        // restore the captured pieces to the board
        restoreCaptures( move.captureList );
    }

     void removeCaptures( CaptureList captureList )
    {
        if ( captureList != null )
            captureList.removeFromBoard( this );
    }

    void restoreCaptures( CaptureList captureList )
    {
        if ( captureList != null )
            captureList.restoreOnBoard( this );
    }

    /**
     * @return true if the move puts the player who made the move into check (his king gets put in check).
     */
    public boolean causesSelfCheck(ChessMove m)
    {
        // need to check all opponent pieces to see if they can capture the piece after it has moved.
        assert (null != m);
        makeMove(m);

        int row, col;
        boolean checked = false;
        for ( row = 1; row <= getNumRows(); row++ ) {
            for ( col = 1; col <= getNumCols(); col++ ) {
                BoardPosition pos = getPosition( row, col );
                if ( pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() == !m.isPlayer1() ) {
                    checked = isKingCheckedByPosition(pos, m);
                }
                if (checked) {
                    undoMove();
                    return true;
                }
            }
        }

        undoMove();
        return false;
    }

    /**
     * Num different states. E.g. black queen.
     * This is used primarily for the Zobrist hash. You do not need to override if yo udo not use it.
     * 2 * 6 = 12.
     * @return number of different states this position can have.
     */
    @Override
    public int getNumPositionStates() {
        return 12;
    }

    /**
     * The index of the state for this position.
     * Perhaps this would be better abstract.
     * @return The index of the state for tihs position.
     */
    @Override
    public int getStateIndex(BoardPosition pos) {
        if (pos.isOccupied()) {
            ChessPiece p = (ChessPiece) pos.getPiece();
            return p.typeIndex();
        } else {
            return 0;
        }
    }
}
