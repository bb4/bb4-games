package com.barrybecker4.game.twoplayer.hex.pathsearch;

import com.barrybecker4.game.twoplayer.hex.HexBoard;
import com.barrybecker4.search.AStarSearch;
import com.barrybecker4.search.HeapPriorityQueue;
import scala.Option;
import scala.collection.Seq;


/**
 * @author Barry Becker
 */
public class ShortestPathSearch extends AStarSearch<HexState, HexTransition> {

    //private boolean p1;

    public ShortestPathSearch(HexBoard state, boolean player1) {
        // the second param should be just use default, but java does not support default params
        super(new HexSearchSpace(state, player1), new HeapPriorityQueue<>(256, null));
        //p1 = player1;
    }

    public int getShortestPathCost() {
        Option<Seq<HexTransition>> path = solve();

        int totalCost = 0;

        if (path.isEmpty()) {
            totalCost = 1000;
        }
        else {
            Seq<HexTransition> pathList = path.get();
            for (HexTransition transition : scala.collection.JavaConverters.seqAsJavaList(pathList)) {
                totalCost += transition.getCost();
            }
        }
        //System.out.println("shortest path for p1=" + p1 + " is " + path + "  COST = " + totalCost);

        return totalCost;
    }
}
