/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.common.util.UnionFind;

import java.util.LinkedList;
import java.util.List;

/**
 * Representation of a Hex Game Board
 *
 * @author Barry Becker
 */
public class HexBoard extends TwoPlayerBoard<TwoPlayerMove> {

    private static final int DEFAULT_SIZE = 11;

    private UnionFind union;

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
        union = new UnionFind(n);

        int lastRowStart = (colMax + 1) * (rowMax);
        // the top and bottom rows are connected sets.
        for (int i = 1; i <= colMax; i++) {
            union.union(i, i-1);
            union.union(lastRowStart + i, lastRowStart + i - 1);
        }
        // the left and right edges for connected sets.
        for (int j = 2; j < rowMax; j++) {
            int idx = j * (colMax + 1);
            int lastIdx = (j - 1) * (colMax + 1);
            union.union(idx, lastIdx);
            union.union(idx + colMax, lastIdx + colMax);
        }
    }

    /** check for top to bottom path for player 1 win*/
    public boolean isPlayer1Connected() {
        int positionInLastRow = (getNumRows() + 1) * (getNumCols() + 2) + 2;
        return union.connected(2, positionInLastRow);
    }

    /** check for left to right path for player 2 win */
    public boolean isPlayer2Connected() {
        int positionInFirstCol = getNumCols() + 2;
        int positionInLastCol = 2 * (getNumCols() + 2) - 1;
        return union.connected(positionInFirstCol, positionInLastCol);
    }

    @Override
    protected boolean makeInternalMove(TwoPlayerMove move) {
        boolean legal = super.makeInternalMove(move);
        if (legal) {
            // add connections to friendly neighbors
            List<BoardPosition> friendlyNbrs = getFriendlyNbrs(move);
            int idx1 = getPositionIdx(move.getToLocation());
            for (BoardPosition nbr : friendlyNbrs) {
                System.out.println("joining " + idx1 +" and " + getPositionIdx(nbr.getLocation()));
                union.union(idx1, getPositionIdx(nbr.getLocation()));
            }
        }
        return legal;
    }

    private int getPositionIdx(Location loc) {
        return loc.getCol() + (getNumCols() + 2) * loc.getRow();
    }

    private List<BoardPosition> getFriendlyNbrs(TwoPlayerMove move) {
        List<BoardPosition> nbrs = new LinkedList<>();
        Location loc = move.getToLocation();
        boolean player1 = move.isPlayer1();

        for (int dir = 0; dir < 6; dir++) {
            Location nbrLoc = HexBoardUtil.getNeighborLocation(loc, dir);
            BoardPosition pos = this.getPosition(nbrLoc);
            if (pos == null) {
                if (!player1 && (nbrLoc.getCol() < 1 || nbrLoc.getCol() > getNumCols())) {
                    HexBoardPosition edgePos = new HexBoardPosition(nbrLoc, new GamePiece(false));
                    nbrs.add(edgePos);
                }
                else if (player1 && (nbrLoc.getRow() < 1 || nbrLoc.getRow() > getNumRows())) {
                    HexBoardPosition edgePos = new HexBoardPosition(nbrLoc, new GamePiece(true));
                    nbrs.add(edgePos);
                }
            }
            else if (pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() == player1) {
                nbrs.add(pos);
            }
        }
        return nbrs;
    }

    @Override
    protected void undoInternalMove(TwoPlayerMove move) {
        getPosition(move.getToRow(), move.getToCol()).clear();
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
        return getNumCols() * getNumRows();
    }

    public HexCandidateMoves getCandidateMoves() {
        return new HexCandidateMoves(this);
    }

    public String getDebugInfo(BoardPosition pos) {
        int idx = getPositionIdx(pos.getLocation());
        return  " idx=" + idx  + " item=" + union.find(idx);
    }
}
