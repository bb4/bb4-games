/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess;

import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.optimization.parameter.ParameterArray;

import java.util.List;

/**
 *  The ChessChessBoardPosition describes the physical marker at a location on the board.
 *  A ChessPiece is either empty or contains one of the standard chess pieces.
 *  This class has in it the rules for how each chess move can move.
 *
 * @see ChessBoard
 * @author Barry Becker
 */
public class ChessPiece extends GamePiece {

    private ChessPieceType pieceType_;

    /** true until the piece has been moved the first time.  */
    private boolean firstTimeMoved_ = true;

    public ChessPiece( boolean player1, ChessPieceType type)  {
        super( player1, type.getSymbol());
        pieceType_ = type;
    }

    /** copy constructor */
    protected ChessPiece(ChessPiece piece) {
        super(piece);
        this.pieceType_ = piece.pieceType_;
        this.firstTimeMoved_ = piece.firstTimeMoved_;
    }

    /**
     *  create a deep copy of the position.
     */
    @Override
    public ChessPiece copy() {
        return new ChessPiece(this);
    }

    public boolean is(ChessPieceType type) {
        return pieceType_ == type;
    }

    public ChessPieceType getPieceType() {
        return pieceType_;
    }

    /**
     * @return  true if this is the first time that this piece has been moved in the game.
     */
    public boolean isFirstTimeMoved() {
        return firstTimeMoved_;
    }

    /**
     * @param firstTimeMoved whether or not this piece has been moved yet this game.
     */
    public void setFirstTimeMoved( boolean firstTimeMoved) {
        firstTimeMoved_ = firstTimeMoved;
    }

    /**
     * find all the possible moves that this piece can make.
     * If checkingChecks is true then moves that lead to a king capture are not allowed.
     *
     * @param board  the board we are examining
     * @param lastMove the most recently made move.
     * @return a list of legal moves for this piece to make
     */
    public List<ChessMove> findPossibleMoves(Board board, int row, int col, ChessMove lastMove) {
        return pieceType_.findPossibleMoves(board, row, col, lastMove, this);
    }

    public double getWeightedScore(int side, BoardPosition pos, ParameterArray weights, int advancement) {
        return pieceType_.getWeightedScore(side, pos, weights, advancement);
    }

    public int typeIndex() {
        return pieceType_.ordinal() + 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder( super.toString() );
        if (this.isFirstTimeMoved())
            sb.append(" notYetMoved ");
        return sb.toString();
    }
}



