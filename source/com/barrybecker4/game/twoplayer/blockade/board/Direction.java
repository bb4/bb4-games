/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Possible Blockade move directions.
 *
 * @author Barry Becker
 */
public enum Direction {

    /** these directional constants are determined by the hash = 10*(2+rowDif) + 2+colDif */
    NORTH_NORTH(2),        // -2 0
    NORTH_WEST (11),       // -1 -1
    NORTH(12),             // -1 0
    NORTH_EAST(13),         // -1 1
    WEST_WEST(20),          // 0 -2
    WEST(21),               // 0 -1
    EAST(23),               // 0 1
    EAST_EAST(24),          // 0 2
    SOUTH_WEST(31),         // 1 -1
    SOUTH (32),             // 1 0
    SOUTH_EAST(33),         // 1 1
    SOUTH_SOUTH(42);        // 2 0

    /** hashcode for the direction */
    private int hashKey_;

    private static Map<Integer, Direction> map_;

    /**
     * constructor for blockade direction
     *
     * @param hashKey string name of the eye type (eg "False Eye")
     */
    Direction(int hashKey) {
        hashKey_ = hashKey;
        initHash(hashKey_, this);
    }

    /**
     * Oddly, this line cannot appear directly in the constructor or you get an error like
     * illegal reference to static field in initializer.
     */
    private static void initHash(int hashKey, Direction d) {
        if (map_ == null) {
            map_ =  new HashMap<Integer, Direction>(12);
        }
        map_.put(hashKey, d);
    }

    /**
     * Get the direction given the row and column change.
     * @param rowDif row delta
     * @param colDif column delta
     * @return the direction enum value
     */
    public static Direction getDirection(int rowDif, int colDif) {
        // hash function
        int hashKey = 10 * (2 + rowDif) + 2 + colDif;
        return map_.get(hashKey);
    }

    /**
     * @return the row and column offset for this direction.
     * An inverse map from the hashKey to the rowDif and colDif
     */
    public Location getOffset() {
        int colDif = (hashKey_ % 10) - 2;
        int rowDif = (hashKey_ / 10) - 2;
        return new ByteLocation(rowDif, colDif);
    }

}

