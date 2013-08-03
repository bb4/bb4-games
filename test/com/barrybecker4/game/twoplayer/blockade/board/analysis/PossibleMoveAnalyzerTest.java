// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.analysis;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.blockade.BlockadeTestCase;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;

import java.util.Arrays;
import java.util.List;

import static com.barrybecker4.game.twoplayer.blockade.board.BlockadeTstUtil.createMove;

/**
 * @author Barry Becker
 */
public class PossibleMoveAnalyzerTest extends BlockadeTestCase {

    public void testFindPossibleMoves() throws Exception {

        List<BlockadeMove> expMoves = Arrays.asList(
                createMove(3, 3,  5, 3, 0, null, null),
                createMove(3, 3,  4, 4, 0, null, null),
                createMove(3, 3,  4, 2, 0, null, null),
                createMove(3, 3,  3, 1, 0, null, null),
                createMove(3, 3,  3, 4, 0, null, null),
                createMove(3, 3,  2, 3, 0, null, null),
                createMove(3, 3,  1, 3, 0, null, null),
                createMove(3, 3,  3, 2, 0, null, null),
                createMove(3, 3,  2, 3, 0, null, null),
                createMove(3, 3,  3, 5, 0, null, null)
        );

        verifyPossibleMoves("board/analysis/initial5x7", new ByteLocation(3, 3), expMoves);
    }

    public void testFindPossibleMovesWhenWalls() throws Exception {

        List<BlockadeMove> expMoves = Arrays.asList(
                createMove(3, 3,  4, 4, 0, null, null),
                createMove(3, 3,  2, 4, 0, null, null),
                createMove(3, 3,  1, 3, 0, null, null),
                createMove(3, 3,  2, 2, 0, null, null),
                createMove(3, 3,  3, 5, 0, null, null)
        );

        verifyPossibleMoves("board/analysis/midGame5x7", new ByteLocation(3, 3), expMoves);
    }

    /**
     * @param filename name of the test file to restore
     * @param loc starting location of the shortest paths.
     * @param expMoves the expected possible moves.
     */
    private void verifyPossibleMoves(String filename, Location loc, List<BlockadeMove> expMoves) throws Exception {

        restore(filename);

        BlockadeBoard board = (BlockadeBoard)controller_.getBoard();
        PossibleMoveAnalyzer moveAnalyzer = new PossibleMoveAnalyzer(board);
        BlockadeBoardPosition position = board.getPosition(loc);
        boolean oppIsPlayer1 = true;

        List<BlockadeMove> moves = moveAnalyzer.getPossibleMoveList(position, oppIsPlayer1);

        assertEquals("Unexpected possible moves.", expMoves, moves);
    }

}
