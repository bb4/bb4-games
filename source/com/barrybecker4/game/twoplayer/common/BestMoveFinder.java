/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common;

import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions;

import java.util.Collections;

/**
 * Find the best moves from a list of reasonable next moves using configured search options
 *
 * @author Barry Becker
 */
public class BestMoveFinder {

    private BestMovesSearchOptions searchOptions_;

    /**
     * Constructor.
     */
    public BestMoveFinder(BestMovesSearchOptions searchOptions) {
        searchOptions_ = searchOptions;
    }

    /**
     * Take the list of all possible next moves and return just the top bestPercentage of them
     * (or minBestMoves moves, whichever is greater).
     *
     * sort the list so the better moves appear first.
     * This is a terrific improvement when used in conjunction with alpha-beta pruning.
     *
     * @param player1 true if its player one's turn
     * @param moveList the list of all generated moves
     * @return the best moves in order of how good they are.
     */
    public MoveList getBestMoves(boolean player1, MoveList moveList) {

        Collections.sort( moveList );

        // reverse the order so the best move (using static board evaluation) is first
        if (player1) {
            Collections.reverse( moveList );
        }

        return determineBestMoves(moveList);
    }

    /**
     * Select just the best moves after sorting the reasonable next moves.
     * We could potentially eliminate the best move doing this, but we need to trade that off against search time.
     * A move which has a low score this time might actually lead to the best move later.
     *
     * @param moveList reasonable next moves.
     * @return  set of best moves from the original list
     */
    private MoveList determineBestMoves(MoveList moveList) {

        int minBest = searchOptions_.getMinBestMoves();
        int percentLessThanBestThresh = searchOptions_.getPercentLessThanBestThresh();
        MoveList bestMoveList;

        if (percentLessThanBestThresh > 0) {
            bestMoveList =
                determineMovesExceedingValueThresh(moveList, minBest, percentLessThanBestThresh);
        }
        else {
            int topPercent = searchOptions_.getPercentageBestMoves();
            bestMoveList =
                determineTopPercentMoves(moveList, minBest, topPercent);
        }
        return bestMoveList;
    }

    /**
     *
     * @return top moves
     */
    private MoveList determineMovesExceedingValueThresh(MoveList moveList, int minBest, int percentLessThanBestThresh) {

        int numMoves = moveList.size();
        MoveList bestMoveList = new MoveList();
        if (numMoves > 0) {
            Move currentMove = moveList.getFirstMove();
            double thresholdValue = currentMove.getValue() * (1.0  - (float)percentLessThanBestThresh/100.0);
            double sign = thresholdValue < 0 ? -1 :1;

            bestMoveList.add(currentMove);
            int ct = 1;

            while ((sign * currentMove.getValue() >= sign * thresholdValue || ct < minBest) && ct < numMoves) {
                currentMove = moveList.get(ct++);
                bestMoveList.add(currentMove);
            }
        }
        return bestMoveList;
    }

    /**
     *
     * @return top moves
     */
    private MoveList determineTopPercentMoves(MoveList moveList, int minBest, int topPercent) {
        int numMoves = moveList.size();
        MoveList bestMoveList = moveList;
        int requestedBest = (int) ((float) topPercent / 100.0 * numMoves + 0.5);
        int best = Math.max(minBest, requestedBest);
        if ( best < numMoves)  {
            bestMoveList = moveList.subList(0, best);
        }
        return bestMoveList;
    }
}