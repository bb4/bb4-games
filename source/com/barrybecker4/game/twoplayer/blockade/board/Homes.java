// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;

/**
 * Contains a set of homes for each player
 *
 * @author Barry Becker
 */
public class Homes  {

    /** The number of home bases for each player.  Traditional rules call for 2. */
    public static final int NUM_HOMES = 2;

    /** The percentage away from the players closest edge to place the bases. */
    private static final float HOME_BASE_POSITION_PERCENT = 0.3f;

    /** Home base positions for both players. */
    private BoardPosition[] p1Homes_ = null;
    private BoardPosition[] p2Homes_ = null;

    /**
     * Constructor.
     */
    public Homes() {}

    /** copy constructor */
    Homes(Homes b) {
        p1Homes_ = b.p1Homes_.clone();
        p2Homes_ = b.p2Homes_.clone();
    }

    public BoardPosition[] getPlayerHomes(boolean player1) {
        return player1? p1Homes_ : p2Homes_;
    }

    /**
     * reset the board to its initial state.
     */
    public void init(int numRows, int numCols) {

        p1Homes_ = new BlockadeBoardPosition[NUM_HOMES];
        p2Homes_ = new BlockadeBoardPosition[NUM_HOMES];

        // determine the home base positions,
        // and place the players 2 pieces on their respective home bases initially.
        int homeRow1 = numRows - (int) (HOME_BASE_POSITION_PERCENT * numRows) + 1;
        int homeRow2 = (int) (HOME_BASE_POSITION_PERCENT * numRows);
        float increment = (float)numCols/(NUM_HOMES+1);
        int baseOffset = Math.round(increment);
        for (int i=0; i<NUM_HOMES; i++) {
            int c = baseOffset + Math.round(i*increment);

            p1Homes_[i] = new BlockadeBoardPosition(new ByteLocation(homeRow1, c), null, null, null, true, false);
            p1Homes_[i].setPiece(new GamePiece(true));
            p2Homes_[i] = new BlockadeBoardPosition(new ByteLocation(homeRow2, c), null, null, null, false, true);
            p2Homes_[i].setPiece(new GamePiece(false));
        }
    }
}
