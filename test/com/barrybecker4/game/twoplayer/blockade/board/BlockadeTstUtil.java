// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall;
import com.barrybecker4.game.twoplayer.blockade.board.path.Path;
import com.barrybecker4.game.twoplayer.blockade.board.path.PathList;

import java.util.Iterator;

/**
 * Static methods to help with Blockade testing.
 *
 * @author Barry Becker
 */
public class BlockadeTstUtil {


    public static BlockadeMove createMove(int r1, int c1, int r2, int c2, int val, GamePiece piece, BlockadeWall wall) {
        return new BlockadeMove(new ByteLocation(r1, c1), new ByteLocation(r2, c2), val, piece, wall);
    }

    public static BlockadeMove createMove(int r1, int c1, int r2, int c2) {
        return new BlockadeMove(new ByteLocation(r1, c1), new ByteLocation(r2, c2), 0, new GamePiece(true), null);
    }

    public static Path createPath(Location... locations) {
        BlockadeMove[] moves = new BlockadeMove[locations.length-1];
        Location lastMove = null;
        int ct = 0;
        for (Location loc : locations) {
             if (lastMove != null) {
                 moves[ct++] = new BlockadeMove(lastMove, loc, 0, null, null);
             }
            lastMove = loc;
        }
        return new Path(moves);
    }

    /**
     * @return a string, which if executed will create a path list identical to the instance passed in.
     */
    public static String getConstructorString(PathList paths) {

        StringBuilder bldr = new StringBuilder("new PathList(\n    new Path[] {\n");

        for (Path path : paths) {
            bldr.append("    ").append(getConstructorString(path)) ;
        }
        bldr.append("}),\n");
        return bldr.toString();
    }


    /**
     * @return a string, which if executed will create a path identical to the instance passed in.
     */
    public static String getConstructorString(Path path) {

        Iterator<BlockadeMove> iter = path.iterator();
        StringBuilder bldr = new StringBuilder("    new Path(new BlockadeMove[] {\n");


        while (iter.hasNext()) {
            BlockadeMove move = iter.next();
            bldr.append("            ").append(move.getConstructorString()).append("\n");
        }
        bldr.append("        }),\n");
        return bldr.toString();
    }


    private BlockadeTstUtil() {}
}
