package com.barrybecker4.game.twoplayer.hex.pathsearch;

import com.barrybecker4.common.search.AStarSearch;
import com.barrybecker4.game.twoplayer.hex.HexBoard;

import java.util.List;

/**
 * @author Barry Becker
 */
public class ShortestPathSearch extends AStarSearch<HexBoard, HexTransition> {

    public ShortestPathSearch(HexBoard board, boolean player1) {
        super(new HexSearchSpace(board, player1));
    }

    public int getShortestPathCost() {
        List<HexTransition> path = solve();

        int totalCost = 0;
        for (HexTransition move : path) {
            totalCost += move.getCost();
        }

        return totalCost;
    }
}
