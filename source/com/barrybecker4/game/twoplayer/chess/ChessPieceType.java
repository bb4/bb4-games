/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.CaptureList;
import com.barrybecker4.optimization.parameter.ParameterArray;

import java.util.LinkedList;
import java.util.List;


/**
 *  The ChessChessBoardPosition describes the physical marker at a location on the board.
 *  A ChessPiece is either empty or contains one of the standard chess pieces.
 *  This class has in it the rules for how each chess move can move.
 *
 *  The images and label could also be part of the enum.
 *
 * @author Barry Becker
 */
public enum ChessPieceType {
    PAWN('P') {
        @Override
        public List<ChessMove> findPossibleMoves(Board board, int row, int col, Move lastMove, ChessPiece piece) {
             List<ChessMove> moveList = new LinkedList<>();

            int direction = -1;
            if ( piece.isOwnedByPlayer1() )
                direction = 1;

            // if this is the first time moved, we need to consider a 2 space jump
            if (piece.isFirstTimeMoved())
                checkPawnForward(row, col, direction, 2, board, moveList, piece);
            // in general pawns move forward 1 space
            checkPawnForward(row, col, direction, 1, board, moveList, piece);

            // pawns capture by moving diagonally. Check both diagonals for enemy pieces we can capture.
            checkPawnDiagonal(row, col, direction, -1, board, moveList, piece);
            checkPawnDiagonal(row, col, direction,  1, board, moveList, piece);

            return moveList;
        }

        @Override
        public double getWeightedScore(int side, BoardPosition pos, ParameterArray weights, int advancement) {
            double score = side * advancement * weights.get(ChessWeights.PAWN_ADVANCEMENT_WEIGHT_INDEX).getValue();
            score += side * weights.get(ChessWeights.PAWN_WEIGHT_INDEX).getValue();
            return score;
        }

        /**
         * see if its legal to move the pawn forward numSteps. If so, add it to the moveList.
         * @return moveList list of legal moves discovered so far.
         */
        private List checkPawnForward(int row, int col, int direction, int numSteps, Board b,
                                      List<ChessMove> moveList, ChessPiece piece)
        {
            BoardPosition next =  b.getPosition( row + numSteps*direction, col );
            checkForNonCapture(next, row, col, moveList, piece);
            return moveList;
        }

         /**
          * see if its possible for the pawn to capture an opponent piece on the left or right diagonal.
          * If so, add it to the moveList.
          * @return moveList list of legal moves discovered so far.
          */
        private List<ChessMove> checkPawnDiagonal(int row, int col, int direction, int colInc, Board b,
                                                  List<ChessMove> moveList, ChessPiece piece)
        {
            BoardPosition diag =  b.getPosition( row + direction, col + colInc );
            return checkForCapture(diag, row, col, moveList, piece);
        }
    },
    ROOK('R') {
        @Override
        public List<ChessMove> findPossibleMoves(Board board, int row, int col, Move lastMove, ChessPiece piece) {
            List<ChessMove> moveList = new LinkedList<>();

            // consider horizontal and vertical directions.
            checkRunDirection(row, col, 1, 0,  board, moveList, piece);
            checkRunDirection(row, col, -1, 0, board, moveList, piece);
            checkRunDirection(row, col, 0, 1, board, moveList, piece);
            checkRunDirection(row, col, 0, -1, board, moveList, piece);

            return moveList;
        }

        @Override
        public double getWeightedScore(int side, BoardPosition pos, ParameterArray weights, int advancement) {
            return side * weights.get(ChessWeights.ROOK_WEIGHT_INDEX).getValue();
        }
    },
    KNIGHT('N') {
        /** These give all the positions for a knight's move.  check every pair of 2 in the sequence. */
        private final int[] knightMoveRow_ = {2,  2, -2, -2, 1, -1,  1, -1};
        private  final int[] knightMoveCol_ = {1, -1,  1, -1, 2,  2, -2, -2};

        @Override
        public List<ChessMove> findPossibleMoves(Board board, int row, int col, Move lastMove, ChessPiece piece) {
            return getEightDirectionalMoves(board, row, col, knightMoveRow_, knightMoveCol_, piece);
        }

        @Override
        public double getWeightedScore(int side, BoardPosition pos, ParameterArray weights, int advancement) {
            return side * weights.get(ChessWeights.KNIGHT_WEIGHT_INDEX).getValue();
        }
    },
    BISHOP('B') {
        @Override
        public List<ChessMove> findPossibleMoves(Board board, int row, int col, Move lastMove, ChessPiece piece) {
            List<ChessMove> moveList = new LinkedList<>();

            // consider the 4 diagonal directions.
            checkRunDirection(row, col, 1, 1,  board, moveList, piece);
            checkRunDirection(row, col, 1, -1, board, moveList, piece);
            checkRunDirection(row, col, -1, 1,  board, moveList, piece);
            checkRunDirection(row, col, -1, -1,  board, moveList, piece);

            return moveList;
        }

        @Override
        public double getWeightedScore(int side, BoardPosition pos, ParameterArray weights, int advancement) {
            return side * weights.get(ChessWeights.BISHOP_WEIGHT_INDEX).getValue();
        }
    },
    QUEEN('Q') {
        @Override
        public List<ChessMove> findPossibleMoves(Board board, int row, int col, Move lastMove, ChessPiece piece) {
            List<ChessMove> moveList = new LinkedList<>();

            // the set of queen moves equals rook type moves and bishop type moves.
            // all 8 directions are covered by this.
            List<ChessMove> rookMoveList = ROOK.findPossibleMoves(board, row, col, lastMove, piece);
            List<ChessMove> bishopMoveList = BISHOP.findPossibleMoves(board, row, col,  lastMove, piece);

            moveList.addAll(rookMoveList);
            moveList.addAll(bishopMoveList);

            return moveList;
        }

        @Override
        public double getWeightedScore(int side, BoardPosition pos, ParameterArray weights, int advancement) {
            return side * weights.get(ChessWeights.QUEEN_WEIGHT_INDEX).getValue();
        }
    },
    KING('K') {
        /** These give all the positions for a knight's move. check every pair of 2 in the sequence. */
        private final int[] kingMoveRow_ = {1, -1,  0,  0, -1,  1, -1,  1};
        private final int[] kingMoveCol_ = {0,  0,  1, -1, -1,  1,  1, -1};
        @Override
        public List<ChessMove> findPossibleMoves(Board board, int row, int col, Move lastMove, ChessPiece piece) {
            return getEightDirectionalMoves(board, row, col, kingMoveRow_, kingMoveCol_, piece);
        }

        @Override
        public double getWeightedScore(int side, BoardPosition pos, ParameterArray weights, int advancement) {
            return side * weights.get(ChessWeights.KING_WEIGHT_INDEX).getValue();
        }
    };


    /** Some character used to represent the chess piece */
    private char symbol_;


    ChessPieceType(char type)
    {
        symbol_ = type;
    }

    public char getSymbol() {
        return symbol_;
    }

    /**
     * find all the possible moves that this piece can make.
     * If checkingChecks is true then moves that lead to a king capture are not allowed.
     *
     * @param board  the board we are examining
     * @param lastMove the most recently made move.
     * @param piece chess piece that moved.
     * @return a list of legal moves for this piece to make
     */
    public abstract List<ChessMove> findPossibleMoves(Board board, int row, int col, Move lastMove, ChessPiece piece);


    /**
     * @param side 1 is p1 -1 if p2.
     * @param pos position of the piece
     * @param weights game weights
     * @param advancement  how far towards the opponents side of the board the piece is.
     * @return weighted score
     */
    public abstract double getWeightedScore(int side, BoardPosition pos, ParameterArray weights, int advancement);


    /**
     * find moves for kings or knights which have 8 possible moves.
     * @return  those moves which are valid out of the eight possible that are checked.
     */
    private static List<ChessMove> getEightDirectionalMoves(Board board, int row, int col,
                                                            int[] rowOffsets, int[] colOffsets, ChessPiece piece) {
        List<ChessMove> moveList = new LinkedList<>();

        for (int i=0; i<8; i++) {
            BoardPosition next =
               board.getPosition( row + rowOffsets[i], col + colOffsets[i] );
            checkForCapture(next, row, col, moveList, piece);
            checkForNonCapture(next, row, col, moveList, piece);
        }
        return moveList;
    }

    /**
     * Check all the moves in the direction specified by rowDir and colDir. Add all legal moves in that direction.
     * loop through all spaces between this piece and the next piece or the edge of the board.
     * if the next piece encountered in the specified direction is an opponent piece, then capture it.
     * @param moveList the accumulated possible moves
     * @return moveList
     */
    private static List<ChessMove> checkRunDirection(int curRow, int curCol, int rowDir, int colDir, Board board,
                                                       List<ChessMove> moveList, ChessPiece piece)  {
      int row = (curRow+rowDir);
      int col = (curCol+colDir);
      BoardPosition next = board.getPosition( row, col );

      while ((next != null) && next.isUnoccupied() )   {
          ChessMove m = ChessMove.createMove( new ByteLocation(curRow, curCol),
                                              new ByteLocation(row, col),
                                              null, 0,  piece );
          // no need to evaluate it since there were no captures
          moveList.add( m );
          row += rowDir;
          col += colDir;
          next = board.getPosition( row, col );
      }

      // if the first encountered piece is an enemy then add a move which captures it
      checkForCapture(next, curRow, curCol, moveList, piece);

      return moveList;
    }

    /**
     * @param next candidate next move
     * @param moveList current list of legal moves for this piece
     * @return all current legal moves plus the capture if there is one
     */
    private static List<ChessMove> checkForNonCapture(BoardPosition next, int row, int col, List<ChessMove> moveList,
                                                      ChessPiece piece) {
        if ( (next != null) &&  next.isUnoccupied()) {
            ChessMove m = ChessMove.createMove(new ByteLocation(row, col),
                                               new ByteLocation(next.getRow(), next.getCol()),
                                               null, 0, piece);
            moveList.add( m );
        }
        return moveList;
    }


    /**
     * @param next candidate next move
     * @param moveList current list of legal moves for this piece
     * @return all current legal moves plus the capture if there is one
     */
    private static List<ChessMove> checkForCapture(BoardPosition next, int row, int col, List<ChessMove> moveList,
                                                   ChessPiece piece) {
        if ( (next != null) &&  next.isOccupied() && (next.getPiece().isOwnedByPlayer1() != piece.isOwnedByPlayer1())) {
            // there can only be one capture in chess.
            CaptureList capture = new CaptureList();
            capture.add( next.copy() );
            ChessMove m = ChessMove.createMove(new ByteLocation(row, col),
                                               new ByteLocation(next.getRow(), next.getCol()),
                                               capture, 0, piece);
            moveList.add( m );
        }
        return moveList;
    }
}