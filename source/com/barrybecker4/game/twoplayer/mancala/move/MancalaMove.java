/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.move;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBin;

/**
 *  Describes a change in state from one board
 *  position to the next in a Mancala game.
 *
 *  @see BlockadeBoard
 *  @author Barry Becker
 */
public class MancalaMove extends TwoPlayerMove {

    /** the number of stones that were moved */
    private byte stonesMoved;

    /**
     * If the first move results in placing a stone in the players storage, then they are allowed to go again.
     * This can happen more than once in a row.
     */
    private MancalaMove followUpMove;

    /**
     * Since moves, have to be undone, if there were captures, then they need to be remembered.
     * On the final move, the opposite side is cleared of stones. That also needs to be remembered.
     * To do this, keep a map of location to number of stones captured
     */
    private Captures captures;

    /**
     * Constructor. This should not usually be called directly
     * use the factory method createMove instead.
     */
    public MancalaMove(boolean player1, Location origin, byte numStonesMoved, int val) {
        super(origin, val,  new GamePiece(player1));
        stonesMoved = numStonesMoved;
    }

    /**
     * copy constructor
     */
    private MancalaMove(MancalaMove move) {
        super(move);
        stonesMoved = move.stonesMoved;
    }

    public void setFollowUpMove(MancalaMove followUp) {
        followUpMove = followUp;
    }

    public MancalaMove getFollowUpMove() {
        return followUpMove;
    }

    /**
     * These get set when the move is made and restored when the move is undone.
     * @param captures stones captured in the process of making this move.
     */
    public void setCaptures(Captures captures) {
        this.captures = captures;
    }

    public Captures getCaptures() {
        return captures;
    }

    /**
     * make a deep copy.
     */
    @Override
    public MancalaMove copy() {
        return new MancalaMove(this);
    }

    /**
     * factory method for getting new moves. It uses recycled objects if possible.
     * @return the newly created move.
     */
    public static MancalaMove createMove(boolean isPlayer1, Location loc, int val, MancalaBin bin) {
        return new MancalaMove(isPlayer1, loc, bin.getNumStones(), val);
    }

    /**
     * @return the bin where the stones were drawn.
     */
    public Location getFromLocation() {
        //Note that the toLocation from the parent class is used internally.
        return this.toLocation_;
    }

    public int getNumStonesMoved() {
        return stonesMoved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MancalaMove that = (MancalaMove) o;
        return stonesMoved == that.stonesMoved;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) stonesMoved;
        return result;
    }


    @Override
    public String toString() {
        String s = super.toString();
        s += " stones move = " + stonesMoved;
        return s;
    }
}



