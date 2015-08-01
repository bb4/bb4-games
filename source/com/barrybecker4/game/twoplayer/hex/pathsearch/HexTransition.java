package com.barrybecker4.game.twoplayer.hex.pathsearch;

import com.barrybecker4.common.geometry.Location;

/**
 * @author Barry Becker
 */
public class HexTransition implements Comparable<HexTransition> {

    private int cost;
    private Location location;

    HexTransition(Location move, int cost) {
        this.location = move;
        this.cost = cost;
    }

    int getCost() {
        return cost;
    }

    Location getLocation() {
        return location;
    }

    public String toString() {
        return "[" + location + ": " + cost + "]";
    }

    @Override
    public int compareTo(HexTransition that) {
        return this.getCost() - that.getCost();
    }
}
