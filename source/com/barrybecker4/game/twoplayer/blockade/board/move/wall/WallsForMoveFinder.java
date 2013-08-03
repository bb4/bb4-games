// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move.wall;

import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.Direction;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.blockade.board.path.PathList;

/**
 * Find reasonable wall placements for a given pawn placement.
 *
 * @author Barry Becker
 */
class WallsForMoveFinder {

    private BlockadeBoard board_;

    /**
     * Constructor
     */
    WallsForMoveFinder(BlockadeBoard board) {
        board_ = board;
    }

    /**
     * Find all the possible and legal wall placements for this given step along the opponent path
     * that do not adversely affecting our own shortest paths.
     * Public so it can be tested.
     * @param move move along an opponent path.
     * @param friendlyPaths our friendly paths.
     * @return the walls for a specific move along an opponent path.
     */
    BlockadeWallList getWallsForMove(BlockadeMove move, PathList friendlyPaths) {

        WallAccumulator accumulator = new WallAccumulator(board_);

        // 12 cases
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        BlockadeBoard b = board_;
        BlockadeBoardPosition origPos = board_.getPosition(fromRow, fromCol);
        BlockadeBoardPosition westPos = origPos.getNeighbor(Direction.WEST, b);
        BlockadeBoardPosition eastPos = origPos.getNeighbor(Direction.EAST, b);
        BlockadeBoardPosition northPos = origPos.getNeighbor(Direction.NORTH, b);
        BlockadeBoardPosition southPos = origPos.getNeighbor(Direction.SOUTH, b);
        switch (move.getDirection()) {
            case EAST_EAST :
                accumulator.checkAddWallsForDirection(eastPos, friendlyPaths, Direction.EAST);
            case EAST :
                accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.EAST);
                break;
            case WEST_WEST :
                accumulator.checkAddWallsForDirection(westPos, friendlyPaths, Direction.WEST);
            case WEST :
                accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.WEST);
                break;
            case SOUTH_SOUTH :
                accumulator.checkAddWallsForDirection(southPos, friendlyPaths, Direction.SOUTH);
            case SOUTH :
                accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.SOUTH);
                break;
            case NORTH_NORTH :
                accumulator.checkAddWallsForDirection(northPos, friendlyPaths, Direction.NORTH);
            case NORTH :
                accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.NORTH);
                break;
            case NORTH_WEST :
                accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.NORTH_WEST);
                break;
            case NORTH_EAST :
                accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.NORTH_EAST);
                break;
            case SOUTH_WEST :
                accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.SOUTH_WEST);
                break;
            case SOUTH_EAST:
                accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.SOUTH_EAST);
                break;
            default : assert false:("Invalid direction "+move.getDirection());
        }

        return accumulator.getAccumulatedList();
    }
}
