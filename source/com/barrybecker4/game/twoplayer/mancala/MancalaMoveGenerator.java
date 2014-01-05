/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.BestMoveFinder;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.optimization.parameter.ParameterArray;

import static com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

/**
 * Responsible for determining a set of reasonable next moves.
 *
 * @author Barry Becker
 */
final class MancalaMoveGenerator {

    /**
     * Constructor.
     */
    public MancalaMoveGenerator() {
    }

    /**
     * @return all reasonably good next moves.
     */
    public final MoveList generateMoves(MancalaSearchable searchable, TwoPlayerMove lastMove, ParameterArray weights) {
        MoveList moveList = new MoveList();

        MancalaBoard pb = searchable.getBoard();
        //CandidateMoves candMoves = pb.getCandidateMoves();

        boolean player1 = (lastMove == null) || !lastMove.isPlayer1();

        int ncols = pb.getNumCols();
        int nrows = pb.getNumRows();

        for (int i = 1; i <= nrows; i++ ) {
             for (int j = 1; j <= ncols; j++ ) {

                int lastValue = lastMove == null ? 0 : lastMove.getValue();
                TwoPlayerMove move = TwoPlayerMove.createMove( i, j, lastValue, new GamePiece(player1));

                searchable.makeInternalMove( move );
                move.setValue(searchable.worth(move, weights));
                // now revert the board
                searchable.undoInternalMove( move );
                moveList.add( move );
            }
        }
        BestMoveFinder finder = new BestMoveFinder(searchable.getSearchOptions().getBestMovesSearchOptions());
        return finder.getBestMoves(moveList);
    }

    /**
     * @return a list of urgent moves (i.e positions that can result in a win for either player.
     */
    public MoveList generateUrgentMoves(MancalaSearchable searchable, TwoPlayerMove lastMove, ParameterArray weights) {
        // no urgent moves at start of game.
        if (lastMove == null)  {
            return new MoveList();
        }
        MoveList allMoves = findMovesForBothPlayers(searchable, lastMove, weights);

        // now keep only those that result in a win or loss.
        MoveList urgentMoves = new MoveList();
        boolean currentPlayer = !lastMove.isPlayer1();

        for (Move m : allMoves) {
            TwoPlayerMove move = (TwoPlayerMove) m;
            // if its not a winning move or we already have it, then skip
            if ( Math.abs(move.getValue()) >= WINNING_VALUE  && !contains(move, urgentMoves)) {
                move.setUrgent(true);
                move.setPlayer1(currentPlayer);
                move.setPiece(new GamePiece(currentPlayer));
                urgentMoves.add(move);
            }
        }
        return urgentMoves;
    }

    /**
     * Consider both our moves and opponent moves.
     * @return Set of all next moves.
     */
    private MoveList findMovesForBothPlayers(
            MancalaSearchable searchable, TwoPlayerMove lastMove, ParameterArray weights) {
        MoveList allMoves = new MoveList();

        MoveList moves = generateMoves(searchable, lastMove, weights);
        allMoves.addAll(moves);

        // unlike go, I don't know if we need this
        TwoPlayerMove oppLastMove = lastMove.copy();
        oppLastMove.setPlayer1(!lastMove.isPlayer1());
        MoveList opponentMoves =
                generateMoves(searchable, oppLastMove, weights);
        for (Move m : opponentMoves){
            TwoPlayerMove move = (TwoPlayerMove) m;
            allMoves.add(move);
        }

        return allMoves;
    }

    /**
     * This version of contains does not consider the player when determining containment.
     * @return true of the {@link MoveList} contains the specified move.
     */
    private boolean contains(TwoPlayerMove move, MoveList moves) {
        for (Move m : moves) {
            Location moveLocation = ((TwoPlayerMove)m).getToLocation();
            if (moveLocation.equals(move.getToLocation())) {
                return true;
            }
        }
        return false;
    }
}