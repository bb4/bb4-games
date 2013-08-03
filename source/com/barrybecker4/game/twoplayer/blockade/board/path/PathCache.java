// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.path;

import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;


/**
 * Maintain a list of best/shortest paths from a specific position.
 * Perhaps the paths could be cached based on the pawn and wall positions.
 *
 * @author Barry Becker
 */
public class PathCache {

    /** Cache the most recent shortest paths to opponent homes so we do not have to keep recomputing them. */
    private PathList cachedPaths = null;

    /**
     * Update the cached list of shortest paths if necessary
     */
    public synchronized void update(BlockadeBoardPosition pos, BlockadeBoard board) {

        PathList paths = board.findShortestPaths(pos);
        cachedPaths = paths;

        /**
         * I don't think path caching will work because a cached path may be lengthened during search
         * when walls are placed, but then never shortened again after walls are removed.
         *
        if (isPathCacheBroken(board)) {
            //PathList paths = board.findShortestPaths(pos);
            System.out.println("Updating broken path cache to \n" + paths + "\n on board="+ board);
            cachedPaths = paths;
        }
        else {
            System.out.println("cacheHit pl=" + paths.getTotalPathLength() +" cpl=" + cachedPaths.getTotalPathLength());
            assert (paths.getTotalPathLength() == cachedPaths.getTotalPathLength())
                : (paths  + "\n was not equal to \n" + cachedPaths + "\n on board=" + board);
        } */
    }

    public synchronized PathList getShortestPaths() {
        return cachedPaths;
    }

    /**
     * The cache is broken if any recently placed wall blocks one of our cached paths.
     */
    private synchronized boolean isPathCacheBroken(BlockadeBoard board) {

        // if nothing cached, we need to create the cache.
        if (cachedPaths == null) {
            return true;
        }
        // broken if any of the paths to opponent home is blocked by a recent wall.
        for (Path path : cachedPaths) {
            if (path.isBlocked(board)) {
                return true;
            }
        }
        return false;
    }
}



