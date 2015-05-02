/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.move;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.WallPlacementFinder;
import com.barrybecker4.game.twoplayer.blockade.board.path.Path;
import com.barrybecker4.game.twoplayer.blockade.board.path.PathList;
import com.barrybecker4.optimization.parameter.ParameterArray;

import java.util.LinkedList;
import java.util.List;

/**
 * Generates candidate next moves for a game of Blockade given a current board state.
 *
 * @author Barry Becker
 */
public class MoveGenerator {

    private BlockadeBoard board;
    private ParameterArray weights_;

    /** Constructor */
    public MoveGenerator(ParameterArray weights, BlockadeBoard board) {

        this.board = board;
        weights_ = weights;
    }

    /**
     * First find the opponent's shortest paths.
     * Then For each piece of the current player's NUM_HOME pieces, add a move that represents a step along
     * its shortest paths to the opponent homes and all reasonable wall placements.
     * To limit the number of wall placements we will restrict possibilities to those positions which
     * effect one of the *opponents* shortest paths.
     * @param lastMove last move that was made on the board
     * @return list of generated moves.
     */
    public MoveList<BlockadeMove> generateMoves(BlockadeMove lastMove) {

        MoveList<BlockadeMove> moveList = new MoveList<>();
        boolean player1 = (lastMove == null) || !lastMove.isPlayer1();

        // There is one path from every piece to every opponent home (i.e. n*NUM_HOMES)
        PathList opponentPaths = board.findAllOpponentShortestPaths(player1);

        List<BoardPosition> pawnLocations = new LinkedList<>();
        for ( int row = 1; row <= board.getNumRows(); row++ ) {
            for ( int col = 1; col <= board.getNumCols(); col++ ) {
                BoardPosition p = board.getPosition( row, col );
                if ( p.isOccupied() && p.getPiece().isOwnedByPlayer1() == player1 ) {
                    pawnLocations.add(p);
                    addMoves( p, moveList, opponentPaths, weights_ );
                }
            }
        }
        if (moveList.isEmpty())
            GameContext.log(1, "There aren't any moves to consider for lastMove=" + lastMove
                    + " Complete movelist =" + board.getMoveList() + " \nThe pieces are at:" + pawnLocations);

        return moveList;
    }

    /**
     * Find all the moves a piece can make from position p, and insert them into moveList.
     *
     * @param position the piece to check from its new location.
     * @param moveList add the potential moves to this existing list.
     * @param weights to use.
     * @return the number of moves added.
     */
    private int addMoves( BoardPosition position, MoveList<BlockadeMove> moveList,
                          PathList opponentPaths, ParameterArray weights) {
        int numMovesAdded = 0;

        // first find the NUM_HOMES shortest paths for p.
        PathList paths = board.findShortestPaths((BlockadeBoardPosition)position);

        WallPlacementFinder wallFinder = new WallPlacementFinder(board, opponentPaths, weights);

        // for each of these paths, add possible wall positions.
        // Take the first move from each shortest path and add the wall positions to it.
        for (Path path : paths) {
            if (path.getLength() > 0) {
                 numMovesAdded += addPotentialMoves(moveList, wallFinder, path);
            }
            else {
                GameContext.log(1, "one of the paths was empty : " + paths);
            }
        }

        return numMovesAdded;
    }

    /**
     * Add reasonable next moves.
     * @return the number of moves added.
     */
    private int addPotentialMoves(MoveList<BlockadeMove> moveList, WallPlacementFinder wallFinder, Path path) {

        BlockadeMove firstStep = path.get(0);
        // make the move
        board.makeMove(firstStep);

        // after making the first move, the shortest paths may have changed somewhat.
        // unfortunately, I think we need to recalculate them.
        BlockadeBoardPosition newPos =
                board.getPosition(firstStep.getToRow(), firstStep.getToCol());
        PathList ourPaths = board.findShortestPaths(newPos);

        List<BlockadeMove> wallMoves = wallFinder.findWallPlacementsForMove(firstStep, ourPaths);
        GameContext.log(2, "num wall placements for Move = " + wallMoves.size());
        board.undoMove();

        // iterate through the wallMoves and add only the ones that are not there already
        for (BlockadeMove wallMove : wallMoves) {
            if (!moveList.contains(wallMove)) {
                moveList.add(wallMove);
            }
        }
        return wallMoves.size();
    }
}
