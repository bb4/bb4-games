/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.move;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

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
     * Constructor. This should not usually be called directly
     * use the factory method createMove instead.
     */
    public MancalaMove(Location origin, byte numStonesMoved, int val) {
        super(origin, val,  null);
        stonesMoved = numStonesMoved;
    }


    /**
     * copy constructor
     */
    private MancalaMove(MancalaMove move) {
        super(move);
        stonesMoved = move.stonesMoved;
    }

    /**
     * make a deep copy.
     */
    @Override
    public MancalaMove copy() {
        return new MancalaMove(this);
    }

      /**
     * @return the bin where the stones were drawn.
     */
    public Location getFromLocation() {
        //Note that the toLocation from the parent class is used internally.
        return this.toLocation_;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MancalaMove that = (MancalaMove) o;
        if (stonesMoved != that.stonesMoved) return false;
        return true;
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



