/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common;

import com.barrybecker4.game.common.board.Board;

/**
 *  This base class describes a change in state from one board
 *  position to the next in a game.
 *
 *  @see Board
 *  @author Barry Becker
 */
public class Move implements Comparable<Move> {

    /**
     * The value of this move from the point of view of player1.
     * The value is determined by static evaluation of the board.
     * We use an integer because 0 width search windows require
     * coarse values to work well.
     */
    private int value_;

    /**
     * protected Constructor.
     * use the factory method createMove instead.
     */
    protected Move() {}

    /** Copy constructor */
    public Move(Move move) {
        value_ = move.value_;
    }

    /** @return a deep copy */
    public Move copy() {
        return new Move(this);
    }

    /**
     *  we sort based on the statically evaluated board value
     *  because the inherited value is not known yet.
     *  @return  > 0 if move1 bigger, < 0 if smaller, = 0 if equal
     */
    @Override
    public int compareTo( Move move ) {
        if ( getValue() < move.getValue() )   {
            return -1;
        }
        else if ( getValue() > move.getValue() )  {
            return 1;
        }
        else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "The value of this move is " + value_;
    }

    public int getValue() {
        return value_;
    }

    public void setValue(int value) {
        this.value_ = value;
    }
}

