// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move.wall;

import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.Direction;

/**
 * Find walls not blocking straight moves.
 *
 * @author Barry Becker
 */
class StraightWallChecker extends WallChecker {

    /**
     * Constructor
     */
    StraightWallChecker(BlockadeBoard board) {
        super(board);
    }

    /**
     * Add valid wall placements to the east.
     * Also verify not intersecting a horizontal wall.
     */
    BlockadeWallList checkWallsForEast(BlockadeBoardPosition eastPos, BlockadeBoardPosition pos) {
        BlockadeWallList wallsToCheck = new BlockadeWallList();
        if (eastPos!=null && !pos.isEastBlocked())  {

            BlockadeBoardPosition northPos = pos.getNeighbor(Direction.NORTH, board);
            BlockadeBoardPosition southPos = pos.getNeighbor(Direction.SOUTH, board);
            BlockadeBoardPosition northEastPos = pos.getNeighbor(Direction.NORTH_EAST, board);
            if (northPos != null && !northPos.isEastBlocked()
                 && !(northPos.isSouthBlocked() && northPos.getSouthWall() == northEastPos.getSouthWall())) {
                wallsToCheck.add( new BlockadeWall(pos, northPos) );
            }
            if (southPos != null && !southPos.isEastBlocked()
                && !(pos.isSouthBlocked() && pos.getSouthWall() == eastPos.getSouthWall())) {
                wallsToCheck.add( new BlockadeWall( pos, southPos) );
            }
        }
        return wallsToCheck;
    }

    /**
     * Add valid wall placements to the west.
     */
    BlockadeWallList checkWallsForWest(BlockadeBoardPosition westPos, BlockadeBoardPosition pos) {
        BlockadeWallList wallsToCheck = new BlockadeWallList();
        if (westPos!=null && !westPos.isEastBlocked())  {
            BlockadeBoardPosition northPos = pos.getNeighbor(Direction.NORTH, board);
            BlockadeBoardPosition southPos = pos.getNeighbor(Direction.SOUTH, board);
            BlockadeBoardPosition northWestPos = pos.getNeighbor(Direction.NORTH_WEST, board);
            if (northPos !=  null && !northWestPos.isEastBlocked()
                && !(northWestPos.isSouthBlocked() && northWestPos.getSouthWall() == northPos.getSouthWall())) {
                wallsToCheck.add( new BlockadeWall(westPos, northWestPos) );
            }
            BlockadeBoardPosition southWestPos = pos.getNeighbor(Direction.SOUTH_WEST, board);
            if (southPos != null && !southWestPos.isEastBlocked()
                && !(westPos.isSouthBlocked() && westPos.getSouthWall() == pos.getSouthWall())) {
                wallsToCheck.add( new BlockadeWall(westPos, southWestPos) );
            }
        }
        return wallsToCheck;
    }

    /**
     * Add valid wall placements to the north.
     */
    BlockadeWallList checkWallsForNorth(BlockadeBoardPosition northPos, BlockadeBoardPosition pos) {
        BlockadeWallList wallsToCheck = new BlockadeWallList();
        if (northPos!=null && !northPos.isSouthBlocked())  {
            BlockadeBoardPosition westPos = pos.getNeighbor(Direction.WEST, board);
            BlockadeBoardPosition northWestPos = pos.getNeighbor(Direction.NORTH_WEST, board);
            if (westPos != null && !northWestPos.isSouthBlocked()
                && !(westPos.isEastBlocked() && westPos.getEastWall() == northWestPos.getEastWall())) {
                wallsToCheck.add( new BlockadeWall( northPos, northWestPos) );
            }
            BlockadeBoardPosition northEastPos = pos.getNeighbor(Direction.NORTH_EAST, board);
            if (northEastPos != null && !northEastPos.isSouthBlocked()
                && !(pos.isEastBlocked() && pos.getEastWall() == northPos.getEastWall())) {
                wallsToCheck.add( new BlockadeWall(northPos, northEastPos) );
            }
        }
        return wallsToCheck;
    }

    /**
     * Add valid wall placements to the south.
     */
    BlockadeWallList checkWallsForSouth(BlockadeBoardPosition southPos, BlockadeBoardPosition pos) {
        BlockadeWallList wallsToCheck = new BlockadeWallList();
        if (southPos != null && !pos.isSouthBlocked()) {
            BlockadeBoardPosition westPos = pos.getNeighbor(Direction.WEST, board);
            BlockadeBoardPosition eastPos = pos.getNeighbor(Direction.EAST, board);
            if (eastPos != null && !eastPos.isSouthBlocked()
                && !(pos.isEastBlocked() && pos.getEastWall() == southPos.getEastWall())) {
                wallsToCheck.add( new BlockadeWall(pos, eastPos) );
            }
            BlockadeBoardPosition southWestPos = pos.getNeighbor(Direction.SOUTH_WEST, board);
            if (westPos != null && !westPos.isSouthBlocked()
                && !(westPos.isEastBlocked() && westPos.getEastWall() == southWestPos.getEastWall())) {
                wallsToCheck.add( new BlockadeWall(pos, westPos) );
            }
        }
        return wallsToCheck;
    }
}
