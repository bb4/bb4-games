// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.path;

import com.barrybecker4.game.twoplayer.blockade.board.Homes;

import java.util.LinkedList;

/**
 * A list of paths (from pawn to home base).
 * @author Barry Becker
 */
public class PathList extends LinkedList<Path> {

    public PathList() {}

    /** constructor */
    public PathList(Path[] paths) {
       for (Path path : paths) {
           add(path);
       }
    }

    @Override
    public boolean add(Path path) {
        return super.add(path);
    }

    /**
     * The pathList is valid if there are no paths, or there are fewer than NUM_HOMES, non-0 length paths.
     * and the player is on the opponent home base.
     * @return true if valid path list
     */
    public boolean isValid() {
        return (size() == 0 || (size() < Homes.NUM_HOMES && (get(0).getLength() > 0)));
        //return (size() != 0 && (size() == Homes.NUM_HOMES || (get(0).getLength() == 0)));
    }

    public int getTotalPathLength() {
        int total = 0;
        for (Path path : this) {
            total += path.getPathLength();
        }
        return total;
    }

}
