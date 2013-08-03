// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move;

import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.Direction;
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall;

/**
 * Checks to see if moves are blocked by walls.
 *
 * @author Barry Becker
 */
public class MovePlacementValidator {

    private BlockadeBoard board;

    /**
     * Constructor.
     */
    public MovePlacementValidator(BlockadeBoard board) {
        this.board = board;
    }

    /**
     * Check to see if a given wall blocks the move.
     * Assume the move is valid (eg does not move off the board or anything like that).
     * Do we need to place the wall and then remove it at the end?
     * @param move the move to verify not blocked by a wall.
     * @param wall to see if blocking our move. Assume that this wall does not interfere with other walls
     *             as that would be invalid.
     * @return  true if the wall blocks this move.
     */
    public boolean isMoveBlockedByWall(BlockadeMove move, BlockadeWall wall) {

        board.addWall(wall);

        boolean blocked = isMoveBlocked(move);
        board.removeWall(wall);

        return blocked;
    }

    /**
     * @param move  the move to check if blocked
     * @return true if any wall blocks this move
     */
    public boolean isMoveBlocked(BlockadeMove move) {

        boolean blocked = false;

        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        BlockadeBoardPosition start = board.getPosition(fromRow, fromCol);
        BlockadeBoardPosition west = start.getNeighbor(Direction.WEST, board);
        BlockadeBoardPosition north = start.getNeighbor(Direction.NORTH, board);
        BlockadeBoardPosition south, east;

        switch (move.getDirection()) {
            case NORTH_NORTH :
                BlockadeBoardPosition northNorth = start.getNeighbor(Direction.NORTH_NORTH, board);
                if (northNorth.isSouthBlocked()) blocked = true;
            case NORTH :
                if (north.isSouthBlocked()) blocked = true;
                break;
            case WEST_WEST :
                BlockadeBoardPosition westWest = start.getNeighbor(Direction.WEST_WEST, board);
                if (westWest.isEastBlocked()) blocked = true;
            case WEST :
                if (west.isEastBlocked()) blocked = true;
                break;
            case EAST_EAST :
                east = start.getNeighbor(Direction.EAST, board);
                if (east.isEastBlocked()) blocked = true;
            case EAST :
                if (start.isEastBlocked()) blocked = true;
                break;
            case SOUTH_SOUTH :
                south = start.getNeighbor(Direction.SOUTH, board);
                if (south.isSouthBlocked()) blocked = true;
            case SOUTH :
                if (start.isSouthBlocked()) blocked = true;
                break;
            case NORTH_WEST :
                BlockadeBoardPosition northWest = start.getNeighbor(Direction.NORTH_WEST, board);
                if (!((west.isEastOpen() && northWest.isSouthOpen()) ||
                     (north.isSouthOpen() && northWest.isEastOpen()) ) )  {
                    blocked = true;
                }
                break;
            case NORTH_EAST :
                BlockadeBoardPosition northEast = start.getNeighbor(Direction.NORTH_EAST, board);
                if (!((start.isEastOpen() && northEast.isSouthOpen()) ||
                     (north.isSouthOpen() && north.isEastOpen()) ) )  {
                    blocked = true;
                }
                break;
            case SOUTH_WEST :
                BlockadeBoardPosition southWest = start.getNeighbor(Direction.SOUTH_WEST, board);
                if (!((west.isEastOpen() && west.isSouthOpen()) ||
                     (start.isSouthOpen() && southWest.isEastOpen()) ) )  {
                    blocked = true;
                }
                break;
            case SOUTH_EAST :
                south = start.getNeighbor(Direction.SOUTH, board);
                east = start.getNeighbor(Direction.EAST, board);
                if (!((start.isEastOpen() && east.isSouthOpen()) ||
                     (start.isSouthOpen() && south.isEastOpen()) ) )  {
                    blocked = true;
                }
                break;
        }
        return blocked;
    }
}
