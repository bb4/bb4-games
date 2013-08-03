/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.CaptureList;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

/**
 *  Describes a change in state from one board
 *  position to the next in a Chess game.
 *
 *  @see ChessBoard
 *  @author Barry Becker
 */
public class ChessMove extends TwoPlayerMove
{
    /** the position that the piece is moving from */
    private Location fromLocation_;

    /**
     * this is null (if no captures) or 1 if there was a capture.
     * in chess there can never be more than one piece captured by a single move.
     */
    public CaptureList captureList = null;

    /** True if the first time this piece has moved. */
    private boolean firstTimeMoved_ = true;


    /**
     *  Constructor. This should never be called directly
     *  use the factory method createMove instead.
     */
    private ChessMove( Location origin, Location destination,
                       CaptureList captures, int val, GamePiece piece ) {

        super( destination, val, piece );
        fromLocation_ = origin;
        captureList = captures;
        firstTimeMoved_ = true;
    }

    /**
     * Copy constructor.
     */
    protected ChessMove(ChessMove move) {
        super(move);
        fromLocation_ = move.fromLocation_;
        if (move.captureList != null)
            captureList = move.captureList.copy();
        firstTimeMoved_ = move.firstTimeMoved_;
    }


    /**
     * make a deep copy of this move.
     */
    @Override
    public ChessMove copy() {
        return new ChessMove(this);
    }

    /**
     * factory method for getting new moves.
     * I used to use recycled objects, but it did not seem to improve performance so I dropped it.
     * @return new chess move
     */
    public static ChessMove createMove( Location origin, Location destination,
                                        CaptureList captures, int val, GamePiece piece ) {

        return new ChessMove(origin, destination, captures, val, piece );
    }

    public int getFromRow() {
        return fromLocation_.getRow();
    }

    public int getFromCol() {
        return fromLocation_.getCol();
    }


    public boolean isFirstTimeMoved() {
        return firstTimeMoved_;
    }

    public void setFirstTimeMoved( boolean firstTimeMoved) {
        firstTimeMoved_ = firstTimeMoved;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (this.isFirstTimeMoved())
          sb.append(" firstTimeMoved ");

        if ( captureList != null ) {
            sb.append( captureList.toString() );
        }
        sb.append(" (").append(fromLocation_).append(")->(").append(toLocation_).append(')');
        return sb.toString();
    }
}



