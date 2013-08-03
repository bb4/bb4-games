// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move.wall;

import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;

/**
 * Find walls not blocking diagonal move.
 *
 * @author Barry Becker
 */
class DiagonalWallChecker extends WallChecker {

    /**
     * Constructor
     */
    DiagonalWallChecker(BlockadeBoard board) {
        super(board);
    }

    /**
     * The 9 wall cases for a diagonal move
     */
    BlockadeWallList checkWalls(
            BlockadeBoardPosition topLeft,
            BlockadeBoardPosition topRight,
            BlockadeBoardPosition bottomLeft) {

        boolean leftWall = topLeft.isSouthBlocked();
        boolean rightWall = topRight.isSouthBlocked();
        boolean topWall = topLeft.isEastBlocked();
        boolean bottomWall = bottomLeft.isEastBlocked();
        BlockadeWallList wallsToCheck = new BlockadeWallList();

        // now check and add walls based on the nine possible cases
        if (!(leftWall || rightWall || topWall || bottomWall)) {
            wallsToCheck.add( new BlockadeWall(topLeft, topRight) );
            wallsToCheck.add( new BlockadeWall(topLeft, bottomLeft) );
        }
        else if (leftWall && bottomWall) {
            wallsToCheck = handleDirectionCase(topRight, 0, 1, wallsToCheck);
            wallsToCheck = handleDirectionCase(topLeft, -1, 0, wallsToCheck);
        }
        else if (topWall && rightWall) {
            wallsToCheck = handleDirectionCase(topLeft, 0, -1, wallsToCheck);
            wallsToCheck = handleDirectionCase(bottomLeft, 1, 0, wallsToCheck);
        }
        else if (topWall && leftWall) {
            wallsToCheck = handleDirectionCase(topRight, 0, 1, wallsToCheck);
            wallsToCheck = handleDirectionCase(bottomLeft, 1, 0, wallsToCheck);
        }
        else if (rightWall && bottomWall) {
            wallsToCheck = handleDirectionCase(topLeft, -1, 0, wallsToCheck);
            wallsToCheck = handleDirectionCase(topLeft, 0, -1, wallsToCheck);
        }
        else if (leftWall) {
            wallsToCheck.add( new BlockadeWall( topLeft, bottomLeft) );
            wallsToCheck = handleDirectionCase(topRight, 0, 1, wallsToCheck);
        }
        else if (rightWall) {
            wallsToCheck.add( new BlockadeWall( topLeft, bottomLeft) );
            wallsToCheck = handleDirectionCase(topLeft, 0, -1, wallsToCheck);
        }
        else if (topWall) {
            wallsToCheck.add( new BlockadeWall(topLeft, topRight) );
            wallsToCheck = handleDirectionCase(bottomLeft, 1, 0, wallsToCheck);
        }
        else { // bottomWall
            wallsToCheck.add( new BlockadeWall(topLeft, topRight) );
            wallsToCheck = handleDirectionCase(topLeft, -1, 0, wallsToCheck);
        }

        return wallsToCheck;
    }

    /**
     * This used to be 4 methods (one for each of right, left, top, and bottom).
     * @param rowOffset row offset from pos
     * @param colOffset col offset from pos
     * @param pos one of the 3 base positions
     * @return list of accumulated walls to check.
     */
    private BlockadeWallList handleDirectionCase(BlockadeBoardPosition pos, int rowOffset, int colOffset,
                                                          BlockadeWallList wallsToCheck) {
        BlockadeBoardPosition offsetPos =
                    board.getPosition(pos.getRow() + rowOffset, pos.getCol() + colOffset);
        boolean isVertical = (rowOffset != 0);

        if (offsetPos != null) {
            boolean dirOpen = isVertical? offsetPos.isEastOpen() : offsetPos.isSouthOpen();
            if (dirOpen) {
                wallsToCheck.add( new BlockadeWall(pos, offsetPos));
            }
        }
        return wallsToCheck;
    }
}
