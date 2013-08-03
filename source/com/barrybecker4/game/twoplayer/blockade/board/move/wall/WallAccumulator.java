// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move.wall;

import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.Direction;
import com.barrybecker4.game.twoplayer.blockade.board.path.Path;
import com.barrybecker4.game.twoplayer.blockade.board.path.PathList;

/**
 * Find reasonable wall placements for a given pawn placement.
 *
 * @author Barry Becker
 */
class WallAccumulator {

    private BlockadeBoard board;
    private BlockadeWallList wallsList;

    /**
     * Constructor
     */
    WallAccumulator(BlockadeBoard board) {
        this.board = board;
        wallsList = new BlockadeWallList();
    }

    BlockadeWallList getAccumulatedList() {
        return wallsList;
    }

    /**
     * Add the walls that don't block your own paths, but do block the opponent shortest paths.
     * @param pos to start from.
     * @param paths our friendly paths (do not add wall if it intersects one of these paths).
     * @param direction to move one space (one of EAST, WEST, NORTH, SOUTH).
     * @return the accumulated list of walls.
     */
    BlockadeWallList checkAddWallsForDirection(BlockadeBoardPosition pos, PathList paths,
                                                         Direction direction) {
        BlockadeBoard b = board;
        BlockadeWallList wallsToCheck = new BlockadeWallList();
        BlockadeBoardPosition westPos = pos.getNeighbor(Direction.WEST, b);
        BlockadeBoardPosition eastPos = pos.getNeighbor(Direction.EAST, b);
        BlockadeBoardPosition northPos = pos.getNeighbor(Direction.NORTH, b);
        BlockadeBoardPosition southPos = pos.getNeighbor(Direction.SOUTH, b);
        StraightWallChecker straightChecker = new StraightWallChecker(b);
        DiagonalWallChecker diagonalChecker = new DiagonalWallChecker(b);

        switch (direction) {
            case EAST :
                wallsToCheck = straightChecker.checkWallsForEast(eastPos, pos);
                break;
            case WEST :
                wallsToCheck = straightChecker.checkWallsForWest(westPos, pos);
                break;
            case NORTH :
                wallsToCheck = straightChecker.checkWallsForNorth(northPos, pos);
                break;
            case SOUTH :
                wallsToCheck = straightChecker.checkWallsForSouth(southPos, pos);
                break;
            // There are 4 basic cases for all the diagonals.
            case NORTH_WEST :
                 BlockadeBoardPosition northWestPos = pos.getNeighbor(Direction.NORTH_WEST, b);
                 wallsToCheck = diagonalChecker.checkWalls(northWestPos, northPos, westPos);
                 break;
            case NORTH_EAST :
                 BlockadeBoardPosition northEastPos = pos.getNeighbor(Direction.NORTH_EAST, b);
                 wallsToCheck = diagonalChecker.checkWalls(northPos, northEastPos, pos);
                 break;
            case SOUTH_WEST :
                 BlockadeBoardPosition southWestPos = pos.getNeighbor(Direction.SOUTH_WEST, b);
                 wallsToCheck = diagonalChecker.checkWalls(westPos, pos, southWestPos);
                 break;
            case SOUTH_EAST :
                 wallsToCheck = diagonalChecker.checkWalls(pos, eastPos, southPos);
                 break;
        }

        assert validProposedWalls(wallsToCheck) :
            "Trying to place \n" + wallsToCheck + "\nwhere they already exist in direction " + direction + "\non board\n"+ board;


        return getBlockedWalls(wallsToCheck, paths);
    }

    /**
     * Verify that the walls to check do not overlap on any existing walls
     */
    boolean validProposedWalls(BlockadeWallList wallsToCheck) {
        for (BlockadeWall wall : wallsToCheck) {
            for (BlockadeBoardPosition pos : wall.getPositions()) {
                if (wall.isVertical()) {
                    if (pos.isEastBlocked()) return false;
                } else {
                    if (pos.isSouthBlocked()) return false;
                }
            }
        }
        return true;
    }


    /**
     * @param wallsToCheck list of walls
     * @param paths paths to check for walls that are blocking them.
     * @return wallsList list of walls that are blocking paths.
     */
    private BlockadeWallList getBlockedWalls(BlockadeWallList wallsToCheck, PathList paths)  {
        for (BlockadeWall wall : wallsToCheck) {
            if (wall != null && !arePathsBlockedByWall(paths, wall)) {
                wallsList.add(wall);
            }
        }

        return wallsList;
    }

    /**
     * @param paths are any of these paths blocked by the specified wall?
     * @param wall that we check to see if blocking any paths
     * @return true if the wall is blocking any of the paths.
     */
    private boolean arePathsBlockedByWall(PathList paths, BlockadeWall wall) {
        assert (wall != null);
        for (final Path path : paths) {
            if (path.isBlockedByWall(wall, board)) {
                return true;
            }
        }
        return false;
    }
}
