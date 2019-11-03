/* Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.hex.pathsearch.ShortestPathSearch;

/**
 * Representation of a Hex Game Board
 *
 * @author Barry Becker
 */
public class HexBoard extends TwoPlayerBoard<TwoPlayerMove> {

    private static final int DEFAULT_SIZE = 11;


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

    public int getP1PathCost() {
        return getPathCost(true);
    }

    public int getP2PathCost() {
        return getPathCost(false);
    }

    public Location getCenter() {
        return new ByteLocation(getNumRows()/2, getNumCols()/2);
    }

    /**
     * Use A* to search for the lowest cost path for specified player
     * @param player1 if true then find lowest cost path for p1, else p2.
     * @return lowest cost path found for specified player
     */
    private int getPathCost(boolean player1) {
        ShortestPathSearch searcher = new ShortestPathSearch(this, player1);
        return searcher.getShortestPathCost();
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

}
