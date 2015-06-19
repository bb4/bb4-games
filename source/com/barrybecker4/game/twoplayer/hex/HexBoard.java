/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex;

import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

/**
 * Representation of a Hex Game Board
 *
 * @author Barry Becker
 */
public class HexBoard extends TwoPlayerBoard<TwoPlayerMove> {

    private static final int DEFAULT_SIZE = 11;

    private UnionFindTemp union;

    /**
     * Constructor.
     * Tic tac toe is always 3x3
     */
    public HexBoard() {
        setSize( DEFAULT_SIZE, DEFAULT_SIZE);
    }

    @Override
    protected BoardPosition getPositionPrototype() {
        return new HexBoardPosition(1, 1, null);
    }

    /**
     *  Reset the board to its initial state.
     *  Initialize the hidden borders to have pieces of the appropriate type.
     *  This is how we will tell when there is a winner.
     *  Add these borders to UnionFind so we can tell when opposite sides are connected.
     */
    @Override
    public void reset() {
        super.reset();
        int rowMax = getNumRows() + 1;
        int colMax = getNumCols() + 1;
        int n = (rowMax + 1) * (colMax + 1);
        union = new UnionFindTemp(n);

        int lastRowStart = (colMax + 1) * (rowMax);
        for (int i = 1; i <= colMax; i++) {
            union.union(i, i-1);
            union.union(lastRowStart + i, lastRowStart + i - 1);
        }
        for (int j = 2; j < rowMax; j++) {
            int idx = j * (colMax + 1);
            int lastIdx = (j - 1) * (colMax + 1);
            union.union(idx, lastIdx);
            union.union(idx + colMax, lastIdx + colMax);
        }
    }

    public boolean isPlayer1Connected() {
        int positionInLastRow = (getNumRows() + 1) * (getNumCols() + 2) + 2;
        return union.connected(2, positionInLastRow);
    }

    public boolean isPlayer2Connected() {
        int positionInFirstCol = getNumCols() + 2;
        int positionInLastCol = (2 * getNumCols() - 1);
        return union.connected(positionInFirstCol, positionInLastCol);
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
        return 3;
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
