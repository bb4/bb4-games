// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;

/**
 *  Decides where the wall should be placed given the mouse position.
 *
 *  @author Barry Becker
 */
class WallPlacer {

    private BlockadeBoard board;

    /**
     * Constructor.
     */
    public WallPlacer(BlockadeBoard board) {
        this.board = board;
    }


    /**
     * If we are in wallPlacingMode, then we show the wall being dragged around.
     * When the player clicks the wall is irrevocably placed.
     * @return array of 2 board positions.
     */
    public BlockadeBoardPosition[] getCellLocations(int x, int y, Location loc, int cellSize) {

        int index = getWallIndexForPosition(x, y, loc, cellSize);

        BlockadeBoardPosition pos1 = null,  pos2 = null;

        switch (index) {
            case 0 :
                pos1 = board.getPosition(loc);
                pos2 = board.getPosition(loc.getRow()+1, loc.getCol());
                break;
            case 1 :
                assert (board.getPosition(loc) != null);
                assert (board.getPosition(loc.getRow()-1, loc.getCol())!=null);
                pos1 = board.getPosition(loc);
                pos2 = board.getPosition(loc.getRow()-1, loc.getCol());
                break;
            case 2 :
                pos1 = board.getPosition(loc.getRow()-1, loc.getCol());
                pos2 = board.getPosition(loc.getRow()-1, loc.getCol()+1);
                break;
            case 3 :
                pos1 = board.getPosition(loc.getRow()-1, loc.getCol());
                pos2 = board.getPosition(loc.getRow()-1, loc.getCol()-1);
                break;
            case 4 :
                pos1 = board.getPosition(loc.getRow(), loc.getCol()-1);
                pos2 = board.getPosition(loc.getRow()-1, loc.getCol()-1);
                break;
            case 5 :
                pos1 = board.getPosition(loc.getRow(), loc.getCol()-1);
                pos2 = board.getPosition(loc.getRow()+1, loc.getCol()-1);
                break;
            case 6 :
                pos1 = board.getPosition(loc);
                pos2 = board.getPosition(loc.getRow(), loc.getCol()-1);
                break;
            case 7 :
                pos1 = board.getPosition(loc);
                pos2 = board.getPosition(loc.getRow(), loc.getCol()+1);
                break;
            default : assert false:("bad index="+index);
        }

        return new BlockadeBoardPosition[] {pos1, pos2};
    }


    /**
     * returns an index corresponding to a triangular cell segment according to:
     * <pre>
     * \  3 | 2 /
     * 4 \  | /  1
     * -------------
     * 5 /  | \  0
     * /  6 | 7 \
     * </pre>
     * Perhaps Make this an enum with values NEE, NNE, NNW, NWW, SWW, etc
     * @param xp x position
     * @param yp y position
     * @return wall index corresponding to specified position.
     */
    private int getWallIndexForPosition(int xp, int yp, Location loc, int cellSize) {

        int numRows = board.getNumRows();
        int numCols = board.getNumCols();

        float x = (float)xp/cellSize - (xp / cellSize);
        float y = (float)yp/cellSize - (yp / cellSize);

        if (loc.getCol() >= numCols)  {
             x = Math.min(0.499f, x);
        } else if (loc.getCol() <= 1)  {
             x = Math.max(0.501f, x);
        }

        if (loc.getRow() >= numRows)  {
             y = Math.min(0.499f, y);
        } else if (loc.getRow() <=1 ) {
             y = Math.max(0.501f, y);
        }

        if (x <= 0.5f) {
            if (y <= 0.5f) {    // upper left
                return (y > x)? 4 : 3;
            }
            else {  // y > .5  // lower left
                return ((y - 0.5f) > (0.5f - x))?  6 : 5;
            }
        }
        else { // x>.5
            if (y <= 0.5f) {    // upper right
                return (y < (1.0f - x))? 2 : 1;
            }
            else {  // y > .5  // lower right
                return (y < x)? 0 : 7;
            }
        }
    }
}