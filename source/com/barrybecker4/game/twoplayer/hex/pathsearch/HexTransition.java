package com.barrybecker4.game.twoplayer.hex.pathsearch;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

/**
 * @author Barry Becker
 */
public class HexTransition {
    private int cost;
    private TwoPlayerMove move;

    HexTransition(TwoPlayerMove move, int cost) {
        this.move = move;
        this.cost = cost;
    }

    int getCost() {
        return cost;
    }

    TwoPlayerMove getMove() {
        return move;
    }

    Location getLocation() {
        return move.getToLocation();
    }
}
