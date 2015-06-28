package com.barrybecker4.game.twoplayer.hex.pathsearch;

import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.common.search.SearchSpace;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.hex.HexBoard;
import com.barrybecker4.game.twoplayer.hex.HexBoardUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Barry Becker
 */
public class HexSearchSpace implements SearchSpace<HexState, HexTransition> {

    HexState initialState;
    boolean player1;

    /**
     *
     * @param board the board configuration to search for shortest path on.
     * @param player1 if true, then searching for player1 winning path
     */
    HexSearchSpace(HexBoard board, boolean player1) {
        this.initialState = new HexState(board,
                player1 ? new IntLocation(0, 1) : new IntLocation(1, 0)
        );
        this.player1 = player1;
    }

    @Override
    public HexState initialState() {
        return initialState;
    }

    @Override
    public boolean isGoal(HexState state) {
        Location loc = state.getLocation();
        return player1 ? loc.getRow() == state.getBoard().getNumRows() :
                loc.getCol() == state.getBoard().getNumCols();
    }

    /**
     * @return transitions all adjacent positions that are either friendly or empty
     *   friendly positions have a cost of 0, while empty ones have a cost of 1
     */
    @Override
    public List<HexTransition> legalTransitions(HexState state) {

        List<HexTransition> nbrs = new LinkedList<>();

        Location loc = state.getLocation();
        HexBoard board = state.getBoard();
        MoveList<TwoPlayerMove> moves = board.getMoveList();
        boolean player1 = !(moves.isEmpty() || !moves.getLastMove().isPlayer1());

        for (int dir = 0; dir < 6; dir++) {
            Location nbrLoc = HexBoardUtil.getNeighborLocation(loc, dir);
            if (isValid(nbrLoc)) {
               BoardPosition pos = board.getPosition(nbrLoc);
                if (pos == null) {
                    if ((!player1 && (nbrLoc.getCol() < 1 || nbrLoc.getCol() > board.getNumCols())) ||
                            player1 && (nbrLoc.getRow() < 1 || nbrLoc.getRow() > board.getNumRows())) {
                        nbrs.add(new HexTransition(nbrLoc, 0));
                    }
                }
                else if (pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() == player1) {
                    nbrs.add(new HexTransition(pos.getLocation(), 0));
                }
                else if (!pos.isOccupied())  {
                    nbrs.add(new HexTransition(pos.getLocation(), 1));
                }
            }
        }
        return nbrs;
    }

    /** @return true if the move is in bounds of the board */
    private boolean isValid(Location loc) {
        int row = loc.getRow();
        int col = loc.getCol();
        int max = initialState.getBoard().getNumRows();
        return row > 0 && row <= max && col > 0 && col <= max;
    }

    /** @return the board with the new move */
    @Override
    public HexState transition(HexState lastState, HexTransition transition) {
        return new HexState(lastState.getBoard(), transition.getLocation());
    }

    @Override
    public boolean alreadySeen(HexState state, Set<HexState> seen) {
        if (!seen.contains(state)) {
            seen.add(state);
            return false;
        }
        return true;
    }

    /** estimated distance to the goal state */
    @Override
    public int distanceFromGoal(HexState state) {
        Location lastLocation = state.getLocation();
        return player1 ? (initialState.getBoard().getNumRows() - lastLocation.getRow()) :
                (initialState.getBoard().getNumCols() - lastLocation.getCol());
    }

    @Override
    public int getCost(HexTransition transition) {
        return transition.getCost();
    }

    @Override
    public void refresh(HexState state, long numTries) {
         // intentionally do nothing
    }

    @Override
    public void finalRefresh(List<HexTransition> path, HexState state, long numTries, long elapsedMillis) {
         // intentionally do nothing
    }
}
