/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.board.GamePiece;

/**
 * This base class describes a change in state from one board position to the next in a game.
 * Perhaps save space by removing some of these members.
 * Consider splitting this into TwoPlayerMove (immutable part) and TwoPlayerMoveNode (game tree parts)
 * @author Barry Becker
 */
public class TwoPlayerMove extends Move {

    private static final String P1 = GameContext.getLabel("PLAYER1");
    private static final String P2 = GameContext.getLabel("PLAYER2");

    /** The location of the move. */
    protected Location toLocation;

    /**
     * The is the more accurate evaluated value from point of view of p1
     * It gets inherited from its descendants. It would be the real (perfect)
     * value of the position if the game tree is complete (which rarely happens in practice)
     */
    private int inheritedValue;

    /** true if player1 made the move. */
    private boolean player1;

    /** This is the piece to use on the board. Some games only have one kind of piece. */
    private GamePiece piece;

    /**
     * true if this move was generated during quiescent search.
     * Perhaps should not be in this class.
     */
    private boolean urgent;

    /**  If true then this move is a passing move. */
    protected boolean isPass = false;

    /** True if the player has resigned with this move. */
    protected boolean isResignation = false;

    /** If true then this move is in the path to selected move.  The game tree viewer may highlight it. */
    private boolean selected;

    /** This is a move that we anticipate will be made in the future. Will be rendered differently. */
    private boolean isFuture;

    /** Some comments about how the score was computed. Used for debugging. */
    private String scoreDescription = null;


    /**
     * Protected Constructor.
     * Use the factory method createMove instead.
     */
    protected TwoPlayerMove() {}

    /**
     * Create a move object representing a transition on the board.
     */
    protected TwoPlayerMove( Location destination, int val, GamePiece p) {
        toLocation = destination;

        setValue(val);
        inheritedValue = getValue();
        selected = false;
        piece = p;
        if (p != null) {
            player1 = p.isOwnedByPlayer1();
        }
        isPass = false;
    }

    /**
     * Copy constructor
     */
    protected TwoPlayerMove(TwoPlayerMove move) {

        this(move.getToLocation(), move.getValue(), (move.getPiece()!=null) ? move.getPiece().copy() : null);
        this.inheritedValue = move.inheritedValue;
        this.selected = move.selected;
        this.isPass = move.isPass;
        this.isFuture = move.isFuture;
        this.urgent = move.urgent;
        this.isResignation = move.isResignation;
        this.scoreDescription = move.scoreDescription;
        this.setPlayer1(move.isPlayer1());
    }

    /**
     * @return  a deep copy.
     */
    @Override
    public TwoPlayerMove copy() {
        return new TwoPlayerMove(this);
    }

    /**
     * factory method for getting new moves. It uses recycled objects if possible.
     * @return the newly created move.
     */
    public static TwoPlayerMove createMove( int destinationRow, int destinationCol,
                                            int val, GamePiece piece ) {
        return new TwoPlayerMove(new ByteLocation(destinationRow, destinationCol), val, piece);
    }

    /**
     * factory method for getting new moves. It uses recycled objects if possible.
     * @return the newly created move.
     */
    public static TwoPlayerMove createMove(Location destinationLocation,
                                           int val, GamePiece piece)  {
        return new TwoPlayerMove(destinationLocation, val, piece);
    }

    public final byte getToRow()  {
        return (byte) toLocation.row();
    }

    public final byte getToCol() {
        return (byte) toLocation.col();
    }

    public final Location getToLocation() {
        return toLocation;
    }

    /**
     * We sort based on the statically evaluated board value
     * because the inherited value is not known yet.
     * @return  &gt; 0 if move m is bigger, &lt; 0 if smaller, =0 if equal
     */
    @Override
    public int compareTo( Move m ) {

        int result = super.compareTo(m);
        if (result != 0)  {
            return result;
        }

        // break tie by row position
        TwoPlayerMove move = (TwoPlayerMove) m;
        if ( this.getToRow() < move.getToRow() )
            return -1;
        else if ( this.getToRow() > move.getToRow() )
            return 1;
        else {
            // if still tie, break using col position.
            if (this.getToCol() < move.getToCol() ) {
                return -1;
            } else if ( this.getToCol() > move.getToCol() )  {
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TwoPlayerMove)) return false;
        TwoPlayerMove that = (TwoPlayerMove) o;

        return player1 == that.player1 && toLocation != null && toLocation.equals(that.toLocation);
    }

    @Override
    public int hashCode() {
        int result = toLocation != null ? toLocation.hashCode() : 0;
        result = 31 * result + getValue();
        result = 31 * result + (player1 ? 1 : 0);
        return result;
    }

    /**
     * @return  true if the player (or computer) chose to pass this turn.
     */
    public final boolean isPassingMove() {
        return isPass;
    }

    public final boolean isResignationMove() {
        return isResignation;
    }

    public final boolean isPassOrResignation() {
        return isPass || isResignation;
    }

    public int getInheritedValue() {
        return inheritedValue;
    }

    public void setInheritedValue(int inheritedValue) {
        this.inheritedValue = inheritedValue;
    }

    public boolean isPlayer1() {
        return player1;
    }

    public void setPlayer1(boolean player1) {
        this.player1 = player1;
    }

    public GamePiece getPiece() {
        return piece;
    }

    public void setPiece(GamePiece piece) {
        this.piece = piece;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isFuture() {
        return isFuture;
    }

    public void setFuture(boolean future) {
        isFuture = future;
    }

    public String getScoreDescription() {
        return scoreDescription;
    }

    public void setScoreDescription(String desc) {
        scoreDescription = desc;
    }

    /**
     * @return a string, which if executed will create a move identical to this instance.
     */
    public String getConstructorString() {
        String pieceCreator = "null";
        if (getPiece() != null) {
            pieceCreator = getPiece().isOwnedByPlayer1()? "PLAYER1_PIECE" : "PLAYER2_PIECE";
        }
        return "TwoPlayerMove.createMove(new ByteLocation("
                + getToLocation().row()  + ", " + getToLocation().col()  + "), " + getValue() + ", "
                + pieceCreator + "),";
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append( player1 ? P1 : P2 );
        s.append(" val:").append(FormatUtil.formatNumber(getValue()));
        s.append(" inhrtd:").append(FormatUtil.formatNumber(inheritedValue));
        if (piece != null)  {
            s.append(" piece: ").append(piece.toString());
        }
        //s.append(" sel:"+selected);
        if (!(isPass || isResignation))  {
            s.append('(').append(toLocation.toString()).append(')');
        }
        if (urgent) {
            s.append(" urgent!");
        }
        if (isPass)  {
            s.append(" Passing move");
        }
        if (isResignation) {
            s.append(" Resignation move");
        }
        s.append(" ");
        return s.toString();
    }
}

