// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.analysis;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.blockade.BlockadeTestCase;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeTstUtil;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.blockade.board.path.Path;
import com.barrybecker4.game.twoplayer.blockade.board.path.PathList;
import org.junit.Test;

import static com.barrybecker4.game.twoplayer.blockade.board.BlockadeTstUtil.createMove;
import static org.junit.Assert.assertEquals;

/**
 * @author Barry Becker
 */
public class ShortestPathFinderTest extends BlockadeTestCase {

    @Test
    public void testFindShortestPathsForSimple5x7() throws Exception {

        PathList expPaths = new PathList(new Path[] {
            new Path(new BlockadeMove[] {
                createMove(2,2,  4,2, 0, new GamePiece(false), null),
                createMove(4,2,  6,2, 0, null, null),
            }),
            new Path(new BlockadeMove[] {
                createMove(2,2,  4,2, 0, new GamePiece(false), null),
                createMove(4,2,  6,2, 0, null, null),
                createMove(6,2,  6,4, 0, new GamePiece(true), null),
            }),
        });

        verifyShortestPaths("board/analysis/initial5x7", new ByteLocation(2, 2), expPaths);
    }

    @Test
    public void testFindShortestPathsWhenWAllsPresentFor5x7() throws Exception {

        PathList expPaths = new PathList(new Path[] {
            new Path(new BlockadeMove[] {
                createMove(2,2,  4,2, 0, new GamePiece(false), null),
                createMove(4,2,  6,2, 0, null, null),
            }),
            new Path(new BlockadeMove[] {
                createMove(2,2,  4,2, 0, new GamePiece(false), null),
                createMove(4,2,  6,2, 0, null, null),
                createMove(6,2,  6,4, 0, new GamePiece(true), null),
            }),
        });

        verifyShortestPaths("board/analysis/initial5x7", new ByteLocation(2, 2), expPaths);
    }

    /**
     * @param filename name of the test file to restore
     * @param loc starting location of the shortest paths.
     * @param expPaths the expected shortest paths to opponent home bases for the specified player.
     */
    private void verifyShortestPaths(String filename, Location loc, PathList expPaths) throws Exception {

        restore(filename);

        BlockadeBoard board = (BlockadeBoard)controller_.getBoard();
        ShortestPathFinder pathFinder = new ShortestPathFinder(board);

        PathList paths = pathFinder.findShortestPaths(board.getPosition(loc));

        //assertEquals("Unexpected number of shortest paths", 4, paths.size());
        assertEquals("Unexpected paths. Got\n" + BlockadeTstUtil.getConstructorString(paths),
                expPaths, paths);
    }

}
