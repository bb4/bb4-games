/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy.integration;

import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

/**
 * Meta information about a move made during search.
 *
 * @author Barry Becker
 */
public class MoveInfo {

    private static final long UNKNOWN = -1;

    private TwoPlayerMove move;
    private long numMovesConsidered;

    public MoveInfo(TwoPlayerMove move) {
        this.move = move;
        this.numMovesConsidered = UNKNOWN;
    }

    public MoveInfo(TwoPlayerMove move, long numMovesConsidered) {
        this.move = move;
        this.numMovesConsidered = numMovesConsidered;
    }

    public TwoPlayerMove getMove() {
        return move;
    }

    public boolean hasMovesConsidered() {
        return numMovesConsidered != UNKNOWN;
    }

    public long getNumMovesConsidered() {
        return numMovesConsidered;
    }
}
