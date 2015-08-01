package com.barrybecker4.game.twoplayer.hex.pathsearch;

import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.common.search.SearchSpace;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.hex.HexBoard;
import com.barrybecker4.game.twoplayer.hex.HexBoardUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Barry Becker
 */
public class HexSearchSpace implements SearchSpace<HexState, HexTransition> {

    // the cost of adding one tile to a path.
    private static final int PATH_STEP_COST = 10;

    private HexState initialState;
    private boolean player1;
    private int maxPlus1;

    /**
     * @param board the board configuration to search for shortest path on.
     * @param player1 if true, then searching for player1 winning path
     */
    HexSearchSpace(HexBoard board, boolean player1) {
        Location start = player1 ? new IntLocation(0, 2) : new IntLocation(2, 0);
        this.initialState = new HexState(board, start);
        this.player1 = player1;
        this.maxPlus1 = board.getNumRows() + 1;
    }

    @Override
    public HexState initialState() {
        return initialState;
    }

    @Override
    public boolean isGoal(HexState state) {
        Location loc = state.getLocation();
        return player1 ? loc.getRow() == maxPlus1 : loc.getCol() == maxPlus1;
    }

    /**
     * @return transitions all adjacent positions that are either friendly or empty.
     *   Friendly positions have a cost of 0, while empty ones have a cost of 1
     *   A good optimization would be to avoid adding the place that we just came from.
     */
    @Override
    public List<HexTransition> legalTransitions(HexState state) {

        List<HexTransition> nbrs = new LinkedList<>();

        Location loc = state.getLocation();
        HexBoard board = state.getBoard();

        for (int dir = 0; dir < 6; dir++) {
            Location nbrLoc = HexBoardUtil.getNeighborLocation(loc, dir);
            if (isValid(nbrLoc, player1)) {
               BoardPosition pos = board.getPosition(nbrLoc);
                if (pos == null) {
                    if ((!player1 && (nbrLoc.getCol() < 1 || nbrLoc.getCol() > board.getNumCols())) ||
                            player1 && (nbrLoc.getRow() < 1 || nbrLoc.getRow() > board.getNumRows())) {
                        nbrs.add(new HexTransition(nbrLoc, 0));
                    }
                }
                else if (pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() == player1) {
                    nbrs.add(new HexTransition(nbrLoc, 0));
                }
                else if (!pos.isOccupied())  {
                    nbrs.add(new HexTransition(nbrLoc, PATH_STEP_COST));
                }
            }
        }
        // sorting the nbrs by path cost will speed up search
        Collections.sort(nbrs);
        return nbrs;
    }

    /**
     * @param loc the location to check
     * @param player1 true if player1
     * @return true if the move is in bounds of the board.
     * Player 1 cannot touch the left/right sides and Player 2 cannot touch the top/bottom sides.
     */
    private boolean isValid(Location loc, boolean player1) {
        int row = loc.getRow();
        int col = loc.getCol();
        int max = initialState.getBoard().getNumRows();
        return player1 ?
                (row >= 0 && row <= maxPlus1 && col > 0 && col <= max) :
                (row > 0 && row <= max && col >= 0 && col <= maxPlus1);
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

    /**
     * Estimated distance to the goal state
     * It is important that this distance underestimates the true distance, otherwise, we may get into a
     * situation where it will pick a more direct path that is not the shortest cost.
     */
    @Override
    public int distanceFromGoal(HexState state) {
        Location lastLocation = state.getLocation();
        return (PATH_STEP_COST -1 ) *
                (player1 ? (maxPlus1 - lastLocation.getRow()) : (maxPlus1 - lastLocation.getCol()));
    }

    /**
     * @param transition  transition to get cost of
     * @return either 0 or 1
     */
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
