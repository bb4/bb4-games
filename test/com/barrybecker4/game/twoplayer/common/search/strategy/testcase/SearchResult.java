/** Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy.testcase;

import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;

import static com.barrybecker4.game.twoplayer.common.search.strategy.EvaluationPerspective.CURRENT_PLAYER;

/**
 * Meta information about a move made during search.
 *
 * @author Barry Becker
 */
public class SearchResult {

    /** this is the move id when there is no next move at all */
    public static final String NO_MOVE = "NO_MOVE";

    String moveId;
    int inheritedValue;
    long numMovesConsidered;

    /**
     * The result of a search.
     * @param moveId a unique identifier for this move. Usually it encodes the position in the game tree.
     * @param inheritedValue at the leaf, this is the result of board evaluation.
     *                       At interior nodes it is determined by the search algorithm.
     * @param numConsideredMoves the total number of moves considered to arrive at this result.
     */
    public SearchResult(String moveId, int inheritedValue, long numConsideredMoves) {
        this.moveId = moveId;
        this.inheritedValue = inheritedValue;
        this.numMovesConsidered = numConsideredMoves;
    }

    /**
     * This does the actual search for the found move given the strategy to use
     * @param initialMove the root move in the game tree
     * @param searchStrategy the search strategy to use to find the result move
     */
    public SearchResult(TwoPlayerMoveStub initialMove, SearchStrategy<TwoPlayerMoveStub> searchStrategy) {

        TwoPlayerMoveStub foundMove = searchStrategy.search(initialMove, null);

        int inheritedValue = 0;
        if (foundMove != null)  {
            inheritedValue = foundMove.getInheritedValue();

            if (searchStrategy.getEvaluationPerspective() == CURRENT_PLAYER) {
                inheritedValue = initialMove.isPlayer1() ? -inheritedValue : inheritedValue;
            }
            this.moveId = foundMove.getId();
        }
        else {
            this.moveId = NO_MOVE;
        }

        this.inheritedValue = inheritedValue;
        this.numMovesConsidered = searchStrategy.getNumMovesConsidered();
    }

    public String getMoveId() {
        return moveId;
    }

    public int getInheritedValue() {
        return inheritedValue;
    }

    public long getNumMovesConsidered() {
        return numMovesConsidered;
    }

    public String toString() {
        return "moveId:" + moveId +" inheritedValue:" + inheritedValue +" numMovesConsidered=" + numMovesConsidered;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchResult that = (SearchResult) o;

        if (inheritedValue != that.inheritedValue) return false;
        if (numMovesConsidered != that.numMovesConsidered) return false;
        if (moveId != null ? !moveId.equals(that.moveId) : that.moveId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = moveId != null ? moveId.hashCode() : 0;
        result = 31 * result + inheritedValue;
        result = 31 * result + (int)numMovesConsidered;
        return result;
    }
}
