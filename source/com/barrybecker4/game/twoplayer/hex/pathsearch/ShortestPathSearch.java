package com.barrybecker4.game.twoplayer.hex.pathsearch;

import com.barrybecker4.common.search.AStarSearch;
import com.barrybecker4.game.twoplayer.hex.HexBoard;

import java.util.List;

/**
 * @author Barry Becker
 */
public class ShortestPathSearch extends AStarSearch<HexState, HexTransition> {

    public ShortestPathSearch(HexBoard state, boolean player1) {
        super(new HexSearchSpace(state, player1));
    }

    public int getShortestPathCost() {
        List<HexTransition> path = solve();

        int totalCost = 0;

        if (path == null) {
            totalCost = 1000;
        }
        else {
            for (HexTransition transition : path) {
                totalCost += transition.getCost();
            }
        }
        //System.out.println("shortest path =" + path + " COST = " + totalCost);

        return totalCost;
    }
}