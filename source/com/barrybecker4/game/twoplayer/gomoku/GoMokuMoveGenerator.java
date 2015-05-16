/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku;

import com.barrybecker4.common.geometry.Location;
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
final class GoMokuMoveGenerator {

    /**
     * Constructor.
     */
    public GoMokuMoveGenerator() {
    }

    /**
     * @return all reasonably good next moves.
     */
    public final MoveList<TwoPlayerMove> generateMoves(GoMokuSearchable searchable,
                                                       TwoPlayerMove lastMove, ParameterArray weights) {
        MoveList<TwoPlayerMove> moveList = new MoveList<>();

        GoMokuBoard pb = searchable.getBoard();
        CandidateMoves candMoves = pb.getCandidateMoves();

        boolean player1 = (lastMove == null) || !lastMove.isPlayer1();

        int ncols = pb.getNumCols();
        int nrows = pb.getNumRows();

        for (int i = 1; i <= nrows; i++ ) {
             for (int j = 1; j <= ncols; j++ ) {
                if ( candMoves.isCandidateMove( i, j )) {
                    int lastValue = lastMove == null ? 0 : lastMove.getValue();
                    TwoPlayerMove move = TwoPlayerMove.createMove( i, j, lastValue, new GamePiece(player1));

                    searchable.makeInternalMove( move );
                    move.setValue(searchable.worth(move, weights));
                    // now revert the board
                    searchable.undoInternalMove( move );
                    moveList.add( move );
                }
            }
        }
        BestMoveFinder<TwoPlayerMove> finder =
                new BestMoveFinder<>(searchable.getSearchOptions().getBestMovesSearchOptions());
        return finder.getBestMoves(moveList);
    }

    /**
     * @return a list of urgent moves (i.e positions that can result in a win for either player.
     */
    public MoveList<TwoPlayerMove> generateUrgentMoves(
            GoMokuSearchable searchable, TwoPlayerMove lastMove, ParameterArray weights) {
        // no urgent moves at start of game.
        if (lastMove == null)  {
            return new MoveList<>();
        }
        MoveList<TwoPlayerMove> allMoves = findMovesForBothPlayers(searchable, lastMove, weights);

        // now keep only those that result in a win or loss.
        MoveList<TwoPlayerMove> urgentMoves = new MoveList<>();
        boolean currentPlayer = !lastMove.isPlayer1();

        for (TwoPlayerMove move : allMoves) {
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
    private MoveList<TwoPlayerMove> findMovesForBothPlayers(
            GoMokuSearchable searchable, TwoPlayerMove lastMove, ParameterArray weights) {
        MoveList<TwoPlayerMove> allMoves = new MoveList<>();

        MoveList<TwoPlayerMove> moves = generateMoves(searchable, lastMove, weights);
        allMoves.addAll(moves);

        // unlike go, I don't know if we need this
        TwoPlayerMove oppLastMove = lastMove.copy();
        oppLastMove.setPlayer1(!lastMove.isPlayer1());
        MoveList<TwoPlayerMove> opponentMoves =
                generateMoves(searchable, oppLastMove, weights);
        for (TwoPlayerMove m : opponentMoves){
            allMoves.add(m);
        }

        return allMoves;
    }

    /**
     * This version of contains does not consider the player when determining containment.
     * @return true of the {@link MoveList} contains the specified move.
     */
    private boolean contains(TwoPlayerMove move, MoveList<TwoPlayerMove> moves) {
        for (TwoPlayerMove m : moves) {
            Location moveLocation = m.getToLocation();
            if (moveLocation.equals(move.getToLocation())) {
                return true;
            }
        }
        return false;
    }
}