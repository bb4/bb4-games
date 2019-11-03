/* Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.move;

import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.mancala.MancalaWeights;
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Evaluates the value a recent move.
 *
 * @author Barry Becker
*/
public class MoveEvaluator {

    /**
     * Statically evaluate the board position.
     * @return the lastMove's value modified by the value add of the new move.
     *  a large positive value means that the move is good from player1's viewpoint.
     */
    public int worth(MancalaBoard board, TwoPlayerMove lastMove, ParameterArray weights ) {

        int storageDiff = (board.getHomeBin(true).getNumStones() - board.getHomeBin(false).getNumStones());
        return (int) (weights.get(MancalaWeights.STORAGE_WEIGHT_INDEX).getValue() * storageDiff);
    }
}
