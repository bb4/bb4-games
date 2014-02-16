package com.barrybecker4.game.twoplayer.mancala.board;


import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates a mapping between the boards grid and the mancala bin positions for purposes of navigation.
 *
 * The grid positions are something like
 * 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
 *      2,2  2,3  2,4  2,5  2,6  2,7
 *
 * Where 1,1 and 1,8 are player 1 and 2 homes respectively.
 * For purposes of navigation, the order is
 *  7    6    5     4    3    2    1   0
 *       8    9    10   11   12   13
 *
 * and from 13 back to 0
 *
 * @author Barry Becker
 */
public class BinNavigator {

    private int size;
    private Map<Location, Location> nextMap = new HashMap<>();

    /**
     * @param size the number of columns in the game board.
     *   This amounts to the number of player bins + 2. Traditionally this is 8.
     */
    public BinNavigator(int size) {
        this.size = size;
        for (int i = 0; i < size - 1; i++) {
            nextMap.put(new ByteLocation(1, size - i), new ByteLocation(1, size - i - 1));
        }
        nextMap.put(new ByteLocation(1, 1), new ByteLocation(2, 2));

        for (int i=2; i < size; i++) {
            nextMap.put(new ByteLocation(2, i), new ByteLocation(2, i + 1));
        }
        nextMap.put(new ByteLocation(2, size-1), new ByteLocation(1, size));
    }

    public Location getNextLocation(Location loc) {
        assert loc != null;
        Location next =  nextMap.get(loc);
        assert next != null : "could not find " + loc + " in " + nextMap;
        return next;
    }

    /**
     * @param location initial location
     * @param numHops number of hops. If negative, then hop backwards
     * @return the bin that is n hops counter clockwise from location
     */
    public Location getNthLocation(Location location, int numHops) {

        Location lastLoc = location;
        int hops = numHops > 0 ? numHops : 2 * size - 2 - numHops;
        while (hops > 0) {
            lastLoc = nextMap.get(lastLoc);
            hops--;
        }

        return lastLoc;
    }

    /**
     * @param loc the location to look opposite of
     * @return the location that is opposite the one specified
     */
    public Location getOppositeLocation(Location loc) {
        int col = loc.getCol();
        assert col > 1 && col < size : "Column out of range for  + " + loc ;
        return new ByteLocation((loc.getRow() == 1) ? 2:1, loc.getCol());
    }

    public Location getHomeLocation(boolean player1) {
        return player1 ? new ByteLocation(1, 1) : new ByteLocation(1, size);
    }
}
