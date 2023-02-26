/* Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;

/**
 *  The BoardPosition describes the physical marker at a location on the board.
 *  It may be empty if there is no piece there.
 * @see Board
 */
public class HexBoardPosition extends BoardPosition {

    /**
     * constructor
     * @param row - y position on the board.
     * @param col - x position on the board.
     * @param piece - the piece to put at this position (use null if there is none).
     */
    public HexBoardPosition(int row, int col, GamePiece piece)  {
        super(new ByteLocation(row, col), piece);
    }

    /**
     * constructor
     * @param loc position on the board
     * @param piece - the piece to put at this position (use null if there is none).
     */
    public HexBoardPosition(Location loc, GamePiece piece)  {
        super(loc, piece);
    }

    public HexBoardPosition(HexBoardPosition pos) {
        super(new ByteLocation(pos.getRow(), pos.getCol()), pos.getPiece());
    }

    /**
     * @return copy of this position.
     */
    public HexBoardPosition copy() {
        return new HexBoardPosition(this);
    }

    /**
     * Get the euclidean distance from another board position
     * @param position to get the distance from
     * @return distance from another position
     */
    public final double getDistanceFrom( HexBoardPosition position ) {
        return HexBoardUtil.distanceBetween(location, position.getLocation());
    }

    /**
     * @param position to check if neighboring
     * @return true if immediate neighbor (nobi neighbor)
     */
    public final boolean isNeighbor( HexBoardPosition position ) {
        return HexBoardUtil.isNeighbor(location, position.getLocation());
    }
}

