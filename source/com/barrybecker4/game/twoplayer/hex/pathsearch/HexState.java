package com.barrybecker4.game.twoplayer.hex.pathsearch;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.hex.HexBoard;

/**
 * Hex state consists of an initial board position and a current location.
 * @author Barry Becker
 */
public class HexState {
    private HexBoard board;
    private Location location;

    HexState(HexBoard board, Location loc) {
        this.board = board;
        this.location = loc;
    }

    HexBoard getBoard() {
        return board;
    }

    Location getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HexState hexState = (HexState) o;

        if (board != null ? board != hexState.board : hexState.board != null) return false;
        return !(location != null ? !location.equals(hexState.location) : hexState.location != null);
    }

    @Override
    public int hashCode() {
        int result = board != null ? board.hashCode() : 0;
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }
}
