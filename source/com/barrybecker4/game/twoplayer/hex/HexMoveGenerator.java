/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.BestMoveFinder;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.gomoku.CandidateMoves;
import com.barrybecker4.game.twoplayer.gomoku.GoMokuBoard;
import com.barrybecker4.game.twoplayer.gomoku.GoMokuSearchable;
import com.barrybecker4.optimization.parameter.ParameterArray;

import static com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

/**
 * Responsible for determining a set of reasonable next moves.
 *
 * @author Barry Becker
 */
final class HexMoveGenerator {

    /**
     * Constructor.
     */
    public HexMoveGenerator() {
    }

    /**
     * @return all reasonably good next moves.
     */
    public final MoveList<TwoPlayerMove> generateMoves(HexSearchable searchable,
                                                       TwoPlayerMove lastMove, ParameterArray weights) {
        MoveList<TwoPlayerMove> moveList = new MoveList<>();

        HexBoard hb = searchable.getBoard();
        HexCandidateMoves candMoves = hb.getCandidateMoves();

        boolean player1 = (lastMove == null) || !lastMove.isPlayer1();

        int ncols = hb.getNumCols();
        int nrows = hb.getNumRows();

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
        return new MoveList<>();
    }
}