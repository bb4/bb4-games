// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.hex;

import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.common.geometry.Location;

import java.awt.geom.Point2D;


/**
 *  Used to find neighboring locations in hex space.
 *  Tiles are arranged like this:
 *       1,2   1,3   1,4
 *    2,1   2,2   2,3   2,4
 *       3,1   3,2   3,3
 */
public class HexBoardUtil {

    /**
     * Odd rows are shifted back one.
     * @param loc source location
     * @param direction side to navigate to to find the neighbor. 0 is to the right, 1 is to the NE, etc.
     * @return the indicated neighbor of the specified tile.
     */
    public static Location getNeighborLocation(Location loc, int direction) {

        int row = loc.row();
        int col = loc.col();
        Location nbrLoc = null;

        switch (direction) {
            case 0 : nbrLoc = new IntLocation(row, col + 1); break;
            case 1 : nbrLoc = new IntLocation(row - 1, col + 1); break;
            case 2 : nbrLoc = new IntLocation(row - 1, col ); break;
            case 3 : nbrLoc = new IntLocation(row, col - 1); break;
            case 4 : nbrLoc = new IntLocation(row + 1, col - 1); break;
            case 5 : nbrLoc = new IntLocation(row + 1, col); break;
            default : assert false;
        }
        return nbrLoc;
    }

    static boolean isNeighbor(Location location1, Location location2) {
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
        int row1 = loc1.row();
        int row2 = loc2.row();
        Point2D point1 = new Point2D.Double(loc1.col() + (row1 * 0.5), row1);
        Point2D point2 = new Point2D.Double(loc2.col() + (row2 * 0.5), row2);
        return point1.distance(point2);
    }

    /** hidden constructor */
    private HexBoardUtil() {}
}
