/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;

/**
 * Defines the structure of the checkers board and the pieces on it.
 *
 * @author Barry Becker
 */
public class CheckersBoard extends TwoPlayerBoard<CheckersMove> {

    public static final int SIZE = 8;
    private static final int TWO = 2;

    /**
     *  Constructor
     *  dimensions must be 8*8 for a checkers/chess board.
     */
    public CheckersBoard() {
        setSize(SIZE, SIZE);
    }

    /** Copy constructor */
    public CheckersBoard(CheckersBoard b) {
        super(b);
    }

    @Override
    public CheckersBoard copy() {
        return new CheckersBoard(this);
    }

    /**
     * reset the board to its initial state.
     */
    @Override
    public void reset() {
        super.reset();
        fillRows();
    }

    protected void fillRows() {
        int i;
        int pieceRows = SIZE > 7 ? 2 : 1;
        for ( i = 1; i <= (1 + pieceRows); i++ )  {
            fillRow( i, i % TWO, true );
        }

        for ( i = SIZE-pieceRows; i <= SIZE; i++ ) {
            fillRow( i, i % TWO, false );
        }
    }

    /**
     * fill a row with pieces during setup.
     */
    private void fillRow( int row, int odd, boolean player1 ) {

        for ( int j = 1; j <= SIZE/2; j++ )
            setPosition(new BoardPosition(row, (TWO * j - odd),
                                          new CheckersPiece(player1, CheckersPiece.REGULAR_PIECE)));
    }

    /**
     *  can't change the size of a checkers board.
     */
    @Override
    public void setSize( int numRows, int numCols )  {

        super.setSize(numRows, numCols);
        if ( numRows != SIZE || numCols != SIZE) {
            GameContext.log(0,  "Can't change the size of a checkers/chess board. It must be " + SIZE + "x" + SIZE );
        }
    }

    /**
     * If a checkers game has more than this many moves, then we assume it is a draw.
     */
    @Override
    public int getMaxNumMoves() {
        return 4 * SIZE * SIZE;
    }

    /**
     * given a move specification, execute it on the board.
     * This places the players symbol at the position specified by move.
     */
    @Override
    protected boolean makeInternalMove( CheckersMove move ) {
        getPosition(move.getToRow(), move.getToCol()).setPiece(move.getPiece());

        // we also need to remove the captures from the board
        move.removeCaptures( this );
        getPosition(move.getFromRow(), move.getFromCol()).clear();

        return true;
    }

    /**
     * for checkers, undoing a move means moving the piece back and restoring any captures.
     */
    @Override
    protected void undoInternalMove( CheckersMove move ) {
        BoardPosition startPos = getPosition(move.getFromRow(), move.getFromCol());

        startPos.setPiece(move.getPiece().copy());
        if ( move.kinged ) {
            // then it was just kinged and we need to undo it
            startPos.setPiece(new CheckersPiece(move.isPlayer1(), CheckersPiece.REGULAR_PIECE));
        }
        // restore the captured pieces to the board
        move.restoreCaptures(this);

        getPosition(move.getToRow(), move.getToCol()).clear();
    }

    /**
     * Num different states. E.g. regular piece or king or no pieces at the position.
     * This is used primarily for the Zobrist hash. You do not need to override if yo udo not use it.
     * @return number of different states this position can have.
     */
    @Override
    public int getNumPositionStates() {
        return  5;
    }

    /**
     * @return The index of the state for this position.
     */
    @Override
    public int getStateIndex(BoardPosition pos) {
        if (pos.isOccupied()) {
            CheckersPiece p = (CheckersPiece) pos.getPiece();
            return (p.isOwnedByPlayer1()? 1:2) + (p.isKing()? 0:2);
        } else {
            return 0;
        }
    }

}
