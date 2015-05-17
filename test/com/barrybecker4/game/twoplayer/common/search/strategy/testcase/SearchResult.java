/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy.testcase;

/**
 * Meta information about a move made during search.
 *
 * @author Barry Becker
 */
public class SearchResult {

    String moveId;
    int inheritedValue;
    long numMovesConsidered;

    /**
     * The result of a search.
     */
    public SearchResult(String moveId, int inheritedValue, long numConsideredMoves) {
        this.moveId = moveId;
        this.inheritedValue = inheritedValue;
        this.numMovesConsidered = numConsideredMoves;
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
