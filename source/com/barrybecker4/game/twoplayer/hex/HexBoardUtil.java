// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.hex;

import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.common.geometry.Location;

import java.awt.geom.Point2D;
import java.util.List;


/**
 *  Used to find neighboring locations in hex space.
 *  Tiles are arranged like this:
 *    0,0   0,1   0,2
 *    1,0   1,1   1,2   1,3
 *    2,0   2,1   2,2
 *
 *   TODO: move this and HexUtil to common/geometry
 *
 *  @author Barry Becker
 */
public class HexBoardUtil {

    /**
     * Odd rows are shifted back one.
     * @param loc source location
     * @param direction side to navigate to to find the neighbor. 0 is to the right.
     * @return the indicated neighbor of the specified tile.
     */
    public static Location getNeighborLocation(Location loc, int direction) {

        int row = loc.getRow();
        int col = loc.getCol();

        int colOffset = (Math.abs(row) % 2 == 1) ? -1 : 0;
        Location nbrLoc = null;

        switch (direction) {
            case 0 : nbrLoc = new IntLocation(row, col + 1); break;
            case 1 : nbrLoc = new IntLocation(row - 1, col + colOffset + 1); break;
            case 2 : nbrLoc = new IntLocation(row - 1, col + colOffset); break;
            case 3 : nbrLoc = new IntLocation(row, col - 1); break;
            case 4 : nbrLoc = new IntLocation(row + 1, col + colOffset); break;
            case 5 : nbrLoc = new IntLocation(row + 1, col + colOffset + 1); break;
            default : assert false;
        }
        return nbrLoc;
    }

    public static boolean isNeighbor(Location location1, Location location2) {
        int yDiff = location2.getY() - location1.getY();
        int xDiff = location2.getX() - location1.getX();
        if (Math.abs(yDiff) <= 1.0) {
            if (yDiff  == -1 ) {
                return xDiff == 1.0 || xDiff == 0;
            } else if (yDiff == 0) {
                return Math.abs(xDiff) == 1.0;
            } else {  // yDiff == 1
                return xDiff == -1 || xDiff == 0;
            }
        }
        return false;
    }

    /**
     * Convert to cartesian space, then computer the distance.
     * @param loc1 first location
     * @param loc2 second location
     * @return distance between two hex locations.
     */
    public static double distanceBetween(Location loc1, Location loc2) {
        int row1 = loc1.getRow();
        int row2 = loc2.getRow();
        Point2D point1 = new Point2D.Double(loc1.getCol() + (row1 % 2 == 1 ? -0.5 : 0), row1);
        Point2D point2 = new Point2D.Double(loc2.getCol() + (row2 % 2 == 1 ? -0.5 : 0), row2);

        return point1.distance(point2);
    }

    /** hidden constructor */
    private HexBoardUtil() {}
}
