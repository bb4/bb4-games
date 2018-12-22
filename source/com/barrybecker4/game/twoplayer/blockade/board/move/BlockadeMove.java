/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.move;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.Direction;
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

import java.util.Iterator;

/**
 *  Describes a change in state from one board
 *  position to the next in a Blockade game.
 *
 *  @see BlockadeBoard
 *  @author Barry Becker
 */
public class BlockadeMove extends TwoPlayerMove {

    /** the position that the piece is moving from */
    private Location fromLocation;

    /** the wall placed as part of this move. Immutable. */
    private BlockadeWall wall;

    private final Direction direction;

    /**
     * At the very end of the game it is legal to capture an opponent piece that is still
     * sitting on the home base. This needs to be remembered, so it can be properly undone during search.
     */
    public GamePiece capturedOpponentPawn;

    /**
     * Constructor. This should not usually be called directly
     * use the factory method createMove instead.
     */
    public BlockadeMove(Location origin, Location destination,
                        int val, GamePiece piece, BlockadeWall w) {
        super( destination, val,  piece );
        fromLocation = origin;
        wall = w;
        direction = initDirection();
    }

    /**
     * Factory method for getting new moves.
     * @return new move
     */
    public static BlockadeMove createMove( Location origin, Location destination,
                                           int val, GamePiece piece, BlockadeWall w) {

        return new BlockadeMove( origin, destination, val,  piece, w);
    }

    /**
     * copy constructor
     */
    private BlockadeMove(BlockadeMove move) {
        super(move);
        this.fromLocation = move.fromLocation;
        this.wall = move.wall;
        direction = initDirection();
    }

    /**
     * make a deep copy.
     */
    @Override
    public BlockadeMove copy() {

        return new BlockadeMove(this);
    }

    private Direction initDirection() {
        int rowDif = toLocation.row() - fromLocation.row();
        int colDif = toLocation.col() - fromLocation.col();
        return Direction.getDirection(rowDif, colDif);
    }

    /**
     * @param mv  the move to compare to.
     * @return  true if values are equal.
     */
    @Override
    public boolean equals( Object mv ) {
         BlockadeMove comparisonMove = (BlockadeMove) mv;
         return (getFromLocation().equals(comparisonMove.getFromLocation())) &&
                    (getToLocation().equals(comparisonMove.getToLocation())) &&
                    ((wall == null && comparisonMove.getWall() == null)
                     || (wall != null && wall.equals(comparisonMove.getWall()))) &&
                    (isPlayer1() == comparisonMove.isPlayer1());
    }

    @Override
    public int hashCode() {
       return (100* fromLocation.row() + 99* fromLocation.col()
                  + 30 * getToLocation().row() + getToLocation().col() + wall.hashCode() + (isPlayer1() ? 54321 : 0));
    }

    public int getFromRow() {
        return fromLocation.row();
    }

    public int getFromCol() {
         return fromLocation.col();
    }

    public Location getFromLocation() {
        return fromLocation;
    }

    public BlockadeWall getWall() {
        return wall;
    }

    public void setWall(BlockadeWall wall) {
        this.wall = wall;
    }
    /**
     * @return one of the directional constants defined above (eg SOUTH_WEST)
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * @return a string, which if executed will create a move identical to this instance.
     */
    @Override
    public String getConstructorString() {

        String wallCreator ="null";
        if ( getWall() != null) {
            Iterator<BlockadeBoardPosition> it = getWall().getPositions().iterator();
            BlockadeBoardPosition p1 = it.next();
            BlockadeBoardPosition p2 = it.next();
            wallCreator = "new BlockadeWall(new BlockadeBoardPosition(" + p1.getRow()  +", "+ p1.getCol() +  "), "
                            + "new BlockadeBoardPosition(" + p2.getRow()  +", "+ p2.getCol() +"))";
        }
        String pieceCreator = "null";
        if (getPiece() != null) {
            pieceCreator = "new GamePiece(" + getPiece().isOwnedByPlayer1() + ")";
        }

        return "createMove("
                + getFromLocation().row() + "," + getFromLocation().col() +",  "
                +  getToLocation().row()  + "," + getToLocation().col()  + ", " + getValue() + ", "
                + pieceCreator +", " + wallCreator +
                "),";
    }

    @Override
    public String toString() {

        String s = super.toString();
        if (wall !=null) {
            s += " "+ wall.toString();
        }
        else {
            s += " (no wall placed)";
        }
        s += " (" + fromLocation + ")->(" + getToLocation() + ")";
        return s;
    }
}



