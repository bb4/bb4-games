/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.transposition;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;


/**
 * Various uniqueness tests for hash keys.
 * @author Barry Becker
 */
public class HashGo9x9UniquenessTest extends HashGoBase {

    @Override
    public GoBoard createBoard() {
        return new GoBoard(9, 0);
    }

    public void testBoard9x9Uniqueness()  {
          GoMove[] moves1 = {
            new GoMove(new ByteLocation(9, 6), 0, black()),
            new GoMove(new ByteLocation(4, 3), 0, white()),
            new GoMove(new ByteLocation(7, 3), 0, black()),
            new GoMove(new ByteLocation(6, 6), 0, white()),
            new GoMove(new ByteLocation(7, 5), 0, black())
        };

        GoMove[] moves2 = {
            new GoMove(new ByteLocation(5, 1), 0, black()),
            new GoMove(new ByteLocation(3, 7), 0, white()),
            new GoMove(new ByteLocation(4, 3), 0, black()),
            new GoMove(new ByteLocation(6, 4), 0, white()),
            new GoMove(new ByteLocation(7, 3), 0, black()),
            new GoMove(new ByteLocation(7, 6), 0, white()),
            new GoMove(new ByteLocation(6, 3), 0, black())
        };

        verifyGoBoardsDistinct(moves1, moves2);
    }

    /** The order in which the moves are placed should not matter */
    public void testBoard9x9UniquenessReordered()  {
          GoMove[] moves1 = {
            new GoMove(new ByteLocation(7, 5), 0, black()),
            new GoMove(new ByteLocation(6, 6), 0, white()),
            new GoMove(new ByteLocation(9, 6), 0, black()),
            new GoMove(new ByteLocation(4, 3), 0, white()),
            new GoMove(new ByteLocation(7, 3), 0, black()),
        };

        GoMove[] moves2 = {
            new GoMove(new ByteLocation(7, 3), 0, black()),
            new GoMove(new ByteLocation(7, 6), 0, white()),
            new GoMove(new ByteLocation(4, 3), 0, black()),
            new GoMove(new ByteLocation(6, 4), 0, white()),
            new GoMove(new ByteLocation(5, 1), 0, black()),
            new GoMove(new ByteLocation(3, 7), 0, white()),
            new GoMove(new ByteLocation(6, 3), 0, black())
        };

        verifyGoBoardsDistinct(moves1, moves2);
    }

}
