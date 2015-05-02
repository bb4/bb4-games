/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.twoplayer.checkers.CheckersBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerSearchable;
import com.barrybecker4.optimization.parameter.ParameterArray;

import java.util.Iterator;
import java.util.List;

/**
 * For searching the Chess game tree.
 * TODO: extract a MoveGenerator
 *
 * @author Barry Becker
 */
public class ChessSearchable extends TwoPlayerSearchable<ChessMove, ChessBoard> {

    public ChessSearchable(ChessBoard board, PlayerList players) {
        super(board, players);
    }

    public ChessSearchable(ChessSearchable searchable) {
        super(searchable);
    }

    @Override
    public ChessSearchable copy() {
        return new ChessSearchable(this);
    }

    @Override
    public ChessBoard getBoard() {
        return board_;
    }
    /**
     *  The primary way of computing the score for Chess is to just add up the pieces
     *  Kings should count more heavily. How much more is determined by the weights.
     *  We also give a slight bonus for advancement of non-kings to incentivize them to
     *  become kings.
     *  note: lastMove is not used
     *  @return the value of the current board position
     *   a positive value means that player1 has the advantage.
     *   A big negative value means a good move for p2.
     */
    @Override
    public int worth(ChessMove lastMove, ParameterArray weights )  {
        int row, col;
        double score = 0;

        // evaluate the board after the move has been made
        for ( row = 1; row <= CheckersBoard.SIZE; row++ ) {
            for ( col = 1; col <= CheckersBoard.SIZE; col++ ) {
                BoardPosition pos = getBoard().getPosition( row, col );
                if ( pos.isOccupied() ) {
                    ChessPiece piece = (ChessPiece)pos.getPiece();
                    int side = piece.isOwnedByPlayer1() ? 1 : -1;
                    int advancement =
                            (piece.isOwnedByPlayer1() ? pos.getRow()-1 : (CheckersBoard.SIZE - pos.getRow()-1));
                    score += piece.getWeightedScore(side, pos, weights, advancement);
                }
            }
        }
        return (int)score;
    }

    /**
     *  generate all possible next moves.
     */
   @Override
   public MoveList<ChessMove> generateMoves(ChessMove lastMove, ParameterArray weights) {
       MoveList<ChessMove> moveList = new MoveList<>();
       int row, col;

       boolean player1 = (lastMove == null) || !(lastMove.isPlayer1());

       // scan through the board positions. For each each piece of the current player's,
       // add all the moves that it can make.
       for ( row = 1; row <= CheckersBoard.SIZE; row++ ) {
           for ( col = 1; col <= CheckersBoard.SIZE; col++ ) {
               BoardPosition pos = getBoard().getPosition(row, col);
               if ( pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() == player1 ) {
                   addMoves( pos, moveList, lastMove, weights);
               }
           }
       }

       // remove any moves that causes the king goes into jeopardy (ie check).
       removeSelfCheckingMoves(moveList);

       return bestMoveFinder_.getBestMoves(moveList);
   }

   /**
    * TODO
    * @return those moves that result in check or getting out of check.
    */
   @Override
   public MoveList<ChessMove> generateUrgentMoves(ChessMove lastMove, ParameterArray weights) {
       return new MoveList<>();
   }

    /**
     * Find all the moves a piece p can make and insert them into moveList.
     *
     * @param pos the piece to check.
     * @param moveList add the potential moves to this existing list.
     * @param weights to use.
     * @return the number of moves added.
     */
    int addMoves(BoardPosition pos, MoveList<ChessMove> moveList, ChessMove lastMove, ParameterArray weights) {
        List<ChessMove> moves =
                ((ChessPiece)pos.getPiece()).findPossibleMoves(getBoard(), pos.getRow(), pos.getCol(), lastMove);

        // score the moves in this list
        for (ChessMove move : moves) {
            // first apply the move
            getBoard().makeMove(move);
            move.setValue(worth(move, weights));
            getBoard().undoMove();
        }
        moveList.addAll( moves );

        return moveList.size();
    }

    /**
     * remove any moves that put the king in jeopardy.
     * @param moveList list of moves to examine and modify
     */
    public void removeSelfCheckingMoves(List moveList) {

        Iterator it = moveList.iterator();
        while (it.hasNext()) {
           ChessMove move = (ChessMove)it.next();
           if (getBoard().causesSelfCheck(move)) {
                GameContext.log(2, "don't allow " + move + " because it puts the king in check.");
                it.remove();
           }
        }
    }
}
