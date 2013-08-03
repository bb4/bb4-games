// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move.wall;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.blockade.board.path.Path;
import com.barrybecker4.game.twoplayer.blockade.board.path.PathList;
import com.barrybecker4.game.twoplayer.blockade.board.path.PlayerPathLengths;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;
import com.barrybecker4.optimization.parameter.ParameterArray;

import java.util.LinkedList;
import java.util.List;

/**
 * Generates candidate next moves for a game of Blockade given a current board state.
 *
 * @author Barry Becker
 */
public class WallPlacementFinder {

    private BlockadeBoard board;
    private PathList opponentPaths;
    private ParameterArray weights;
    private WallsForMoveFinder wallsFinder;

    /**
     * Constructor
     */
    public WallPlacementFinder(BlockadeBoard board, PathList opponentPaths, ParameterArray weights) {

        this.board = board;
        this.weights = weights;
        this.opponentPaths = opponentPaths;
        wallsFinder = new WallsForMoveFinder(board);
    }

    /**
     * Find all wall placement variations for move firstStep that make the opponent
     * shortest paths longer, while not adversely affecting our own shortest paths.
     * should optimize this.
     * Is it true that the set of walls we could add for any constant set
     * of opponent paths is always the same regardless of firstStep?
     * I think its only true as long as firstStep is not touching any of those opponent paths.
     * @param firstStep the move to find wall placements for.
     * @param paths our shortest paths.
     * @return all move variations on firstStep based on different wall placements.
     */
    public List<BlockadeMove> findWallPlacementsForMove(BlockadeMove firstStep, PathList paths) {
        List<BlockadeMove> moves = new LinkedList<BlockadeMove>();

        GameContext.log(2, firstStep + "\nopponent paths="+ opponentPaths + "\n [[");

        for (Path opponentPath: opponentPaths) {
            assert (opponentPath != null):
                "    Opponent path was null. There are "+ opponentPaths.size()+" oppenent paths.";

            findWallPlacementsGivenOpponentPath(firstStep, paths, opponentPath, moves);
        }
        GameContext.log(2, "]]");

        // if no move was added add the move with no wall placement
        if (moves.isEmpty()) {
            addMoveWithWallPlacement(firstStep, null, weights, moves);
        }

        return moves;
    }

    private void findWallPlacementsGivenOpponentPath(
            BlockadeMove firstStep, PathList paths, Path opponentPath, List<BlockadeMove> moves) {

        for (int j = 0; j < opponentPath.getLength(); j++) {
            addWallPlacementsForOpponentPathMove(firstStep, opponentPath.get(j), paths, moves);
        }
        if (moves.isEmpty()) {
            GameContext.log(2, "No opponent moves found for "+firstStep +" along opponentPath="+opponentPath);
        }
    }

    /**
     * Add all the possible legal and reasonable wall placements for this move
     * along the opponent path that do not interfere with our own paths.
     *
     * If there is no wall currently interfering with this wall placement,
     * and it does not impact a friendly path,
     * then consider this (and/or its twin) a candidate placement.
     * By twin I mean the other wall placement that also intersects this opponent path
     * (there are always N for walls of size N where N is the number of spaces spanned by a wall).
     */
    private void addWallPlacementsForOpponentPathMove(
            BlockadeMove firstStep, BlockadeMove move, PathList paths, List<BlockadeMove> moves) {

        BlockadeWallList walls = wallsFinder.getWallsForMove(move, paths);
        GameContext.log(2, "    num walls for move " + move + "  = " + walls.size());

        if (walls.isEmpty()) {
            GameContext.log(2, "    ***No walls for move " + move
                    +" that do not interfere with our path");
        }

        // typically 0-4 walls
        assert (walls.size() <=4) : "num walls = " + walls.size();
        for (BlockadeWall wall: walls) {
            addMoveWithWallPlacement(firstStep, wall, weights, moves);
        }
    }

    /**
     * Add a new move to movelist.
     * The move is based on our move and the specified wall (the wall may be null if none placed).
     */
    private void addMoveWithWallPlacement(BlockadeMove ourmove, BlockadeWall wall,
                                          ParameterArray weights, List<BlockadeMove> moves) {
        int value = 0;
        // @@ we should provide the value here since we have all the path info.
        // we do not want to compute the path info again by calling findPlayerPathLengths.
        // The value will change based on how much we shorten our paths while lengthening the opponents.
        BlockadeMove m =
               BlockadeMove.createMove(ourmove.getFromLocation(),
                                       ourmove.getToLocation(),
                                       value, ourmove.getPiece(), wall);
        // for the time being just call worth directly. Its less efficient, but simpler.
        board.makeMove(m);
        PlayerPathLengths pathLengths = board.findPlayerPathLengths();
        board.undoMove();

        if (pathLengths.isValid()) {
            m.setValue(pathLengths.determineWorth(SearchStrategy.WINNING_VALUE, weights));
            moves.add(m);
        }
        else {
            GameContext.log(2, "Did not add "+ m+ " because it was invalid.");
        }
    }
}
