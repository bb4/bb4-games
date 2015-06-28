package com.barrybecker4.game.twoplayer.hex.pathsearch;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.common.search.SearchSpace;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.hex.HexBoard;
import com.barrybecker4.game.twoplayer.hex.HexBoardPosition;
import com.barrybecker4.game.twoplayer.hex.HexBoardUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Barry Becker
 */
public class HexSearchSpace implements SearchSpace<HexBoard, HexTransition> {

    HexBoard initialBoard;
    boolean player1;

    /**
     *
     * @param initialBoard searchable containing the initial initialBoard state.
     * @param player1 if true, then searching for player1 winning path
     */
    HexSearchSpace(HexBoard initialBoard, boolean player1) {
        this.initialBoard = initialBoard;
        this.player1 = player1;
    }

    @Override
    public HexBoard initialState() {
        return initialBoard;
    }

    @Override
    public boolean isGoal(HexBoard board) {
        return player1 ? board.isPlayer1Connected() : board.isPlayer2Connected();
    }

    /**
     * @return transitions all adjacent positions that are either friendly or empty
     *   friendly positions have a cost of 0, while empty ones have a cost of 1
     */
    @Override
    public List<HexTransition> legalTransitions(HexBoard board) {

        List<HexTransition> nbrs = new LinkedList<>();
        /*
        TwoPlayerMove lastMove = board.getMoveList().getLastMove();
        Location loc = lastMove.getToLocation();
        boolean player1 = lastMove.isPlayer1();

        for (int dir = 0; dir < 6; dir++) {
            Location nbrLoc = HexBoardUtil.getNeighborLocation(loc, dir);
            BoardPosition pos = board.getPosition(nbrLoc);
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
        }   */
        return null;
    }

    /** @return the board with the new move */
    @Override
    public HexBoard transition(HexBoard board, HexTransition transition) {
        // no need to do anything if new move to location already has that player there.
        if (board.getPosition(transition.getLocation()).isOccupied()) {
            return board;
        }
        else {
            HexBoard boardCopy = board.copy();
            boardCopy.makeMove(transition.getMove());
            return boardCopy;
        }
    }

    @Override
    public boolean alreadySeen(HexBoard board, Set<HexBoard> seen) {
        if (!seen.contains(board)) {
            seen.add(board);
            return false;
        }
        return true;
    }

    /** estimated distance to the goal state */
    @Override
    public int distanceFromGoal(HexBoard state) {
        Location lastLocation = state.getMoveList().getLastMove().getToLocation();
        return player1 ? (initialBoard.getNumRows() - lastLocation.getRow()) :
                (initialBoard.getNumCols() - lastLocation.getCol());
    }

    @Override
    public int getCost(HexTransition transition) {
        return transition.getCost();
    }

    @Override
    public void refresh(HexBoard state, long numTries) {
         // intentionally do nothing
    }

    @Override
    public void finalRefresh(List<HexTransition> path, HexBoard state, long numTries, long elapsedMillis) {
         // intentionally do nothing
    }
}
