/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.pente;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * @author Barry Becker
 */
public class PenteBoardTest {

    /** instance under test */
    private PenteBoard board;

    @Test
    public void testBoardSize() throws Exception {

        board = new PenteBoard(10, 11);

        assertEquals("Unexpected num rows. ", 10, board.getNumRows());
        assertEquals("Unexpected num columns. ", 11, board.getNumCols());
    }

    @Test
    public void testGetMaxMoves() throws Exception {

        board = new PenteBoard(8, 10);

        assertEquals("Unexpected number of max moves. ", 80, board.getMaxNumMoves());
    }
}
