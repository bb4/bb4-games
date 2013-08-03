// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move.wall;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.Homes;
import com.barrybecker4.game.twoplayer.blockade.board.analysis.BoardAnalyzer;
import com.barrybecker4.game.twoplayer.blockade.board.path.PathList;

import java.util.HashMap;
import java.util.Map;

/**
 * Checks for legal wall placements.
 *
 * @author Barry Becker
 */
public class WallPlacementValidator {

    private BlockadeBoard board;

    /**
     * Constructor.
     */
    public WallPlacementValidator(BlockadeBoard board) {
        this.board = board;
    }

    /*
     * It is illegal to place a wall at a position that overlaps
     * or intersects another wall, or if the wall prevents one of the pawns from reaching an
     * opponent home.
     * @param wall to place. has not been placed yet.
     * @param location where the wall is to be placed.
     * @return an error string if the wall is not a legal placement on the board.
     */
    public String checkLegalWallPlacement(BlockadeWall wall, Location location, BoardAnalyzer analyzer) {
        String sError = null;
        assert (wall != null);

        Map<BlockadeBoardPosition, BlockadeWall> oldWalls = getOldWalls(wall);

        if (hasWallOverlap(wall)) {
            sError = GameContext.getLabel("CANT_OVERLAP_WALLS");
        }

        BlockadeBoardPosition pos = board.getPosition(location);
        if (sError == null && hasWallIntersection(wall)) {
             sError = GameContext.getLabel("CANT_INTERSECT_WALLS");
        }
        else if (sError == null && pos == null) {
            sError = GameContext.getLabel("INVALID_WALL_PLACEMENT");
        }
        else if (sError == null && missingPath(analyzer)) {
            sError = GameContext.getLabel("MUST_HAVE_ONE_PATH");
        }

       // now restore the original walls
       for (BlockadeBoardPosition wallPos: wall.getPositions())  {
            BlockadeWall origWall = oldWalls.get(wallPos);
            if (wall.isVertical())
                wallPos.setEastWall(origWall);
            else
                wallPos.setSouthWall(origWall);
        }
        return sError;
    }


    private Map<BlockadeBoardPosition, BlockadeWall> getOldWalls(BlockadeWall wall) {

        Map<BlockadeBoardPosition, BlockadeWall> oldWalls = new HashMap<BlockadeBoardPosition, BlockadeWall>();
        boolean vertical = wall.isVertical();

        for (BlockadeBoardPosition pos: wall.getPositions())  {

            BlockadeWall origWall = vertical ? pos.getEastWall() : pos.getSouthWall() ;

            // save the old wall, and temporarily set the candidate wall
            oldWalls.put(pos, origWall);

        }
        return oldWalls;
    }

    private boolean hasWallOverlap(BlockadeWall wall) {
        // iterate over the 2 positions covered by the wall.
        boolean vertical = wall.isVertical();
        for (BlockadeBoardPosition pos: wall.getPositions())  {
            BlockadeWall origWall = vertical? pos.getEastWall() : pos.getSouthWall() ;

            if ((vertical && pos.isEastBlocked()) || (!vertical && pos.isSouthBlocked())) {
                return true;
            }

            if (vertical) {
                pos.setEastWall(wall);
            }
            else  {
                pos.setSouthWall(wall);
            }
        }
        return false;
    }

    /**
     * @return error message if the new wall intersects an old one.
     */
    private boolean hasWallIntersection(BlockadeWall wall) {
         boolean vertical = wall.isVertical();
         // you cannot intersect one wall with another
         BlockadeBoardPosition pos = wall.getFirstPosition();

         BlockadeBoardPosition secondPos =
                    (vertical? board.getPosition(pos.getRow(), pos.getCol()+1) :
                               board.getPosition(pos.getRow()+1, pos.getCol()));
        return (vertical && pos.isSouthBlocked() && secondPos.isSouthBlocked())
                || (!vertical && pos.isEastBlocked()) && secondPos.isEastBlocked();
    }

    /**
     * @return error message if no path exists from this position to an opponent home.
     */
    private boolean missingPath(BoardAnalyzer analyzer) {
        PathList paths1 = analyzer.findAllOpponentShortestPaths(true);
        PathList paths2 = analyzer.findAllOpponentShortestPaths(false);
        GameContext.log(2, "paths1.magnitude="+paths1.size()+" paths2.magnitude ="+paths2.size() );

        int expectedNumPaths = getExpectedNumPaths();
        return  (paths1.size() < expectedNumPaths || paths2.size() <expectedNumPaths );
    }

    private int getExpectedNumPaths() {
        return Homes.NUM_HOMES * Homes.NUM_HOMES;
    }
}
