/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex;

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.gomoku.GoMokuBoard;
import com.barrybecker4.game.twoplayer.tictactoe.TicTacToeCandidateMoves;

/**
 * Representation of a Hex Game Board
 *
 * @author Barry Becker
 */
public class HexBoard extends TwoPlayerBoard<TwoPlayerMove> {

    /**
     * Constructor.
     * Tic tac toe is always 3x3
     */
    public HexBoard() {
        setSize( 11, 11 );
    }

    @Override
    protected void undoInternalMove(TwoPlayerMove move) {

    }

    @Override
    public HexBoard copy() {
        return new HexBoard(this);
    }

    @Override
    public int getNumPositionStates() {
        return 0;
    }

    private HexBoard(HexBoard pb) {
        super(pb);
    }

    @Override
    public int getMaxNumMoves() {
        return 9;
    }

    public HexCandidateMoves getCandidateMoves() {
        return new HexCandidateMoves(this);
    }

}
