/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy.example;

import com.barrybecker4.game.common.GameWeights;
import com.barrybecker4.game.common.GameWeightsStub;
import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.game.twoplayer.common.search.SearchableStub;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.barrybecker4.game.twoplayer.common.search.examples.GameTreeExample;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.strategy.MemorySearchStrategy;
import com.barrybecker4.game.twoplayer.common.search.strategy.testcase.SearchResult;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;
import com.barrybecker4.game.twoplayer.common.search.transposition.TranspositionTable;
import com.barrybecker4.optimization.parameter.ParameterArray;
import junit.framework.TestCase;
import static com.barrybecker4.game.twoplayer.common.search.strategy.EvaluationPerspective.CURRENT_PLAYER;

/**
 * Test minimax strategy independent of any particular game implementation.
 *
 * @author Barry Becker
 */
public abstract class AbstractSearchStrategyTst extends TestCase {

    protected SearchOptions searchOptions;


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        searchOptions = createSearchOptions();
    }

    protected SearchStrategy<TwoPlayerMoveStub> createSearchStrategy() {

        Searchable searchable = new SearchableStub(searchOptions);
        GameWeights weights = new GameWeightsStub();
        return createSearchStrategy(searchable, weights.getDefaultWeights());
    }

    /** @return the Search strategy to test. */
    protected abstract SearchStrategy<TwoPlayerMoveStub> createSearchStrategy(
            Searchable searchable, ParameterArray weights);

    /**
     * @return default search options for all games
     */
    public SearchOptions createSearchOptions() {
        SearchOptions opts = new SearchOptions();
        opts.getBestMovesSearchOptions().setPercentageBestMoves(100);
        opts.getBestMovesSearchOptions().setPercentLessThanBestThresh(0);
        return opts;
    }

    /**
     * Verify move that was found using search strategy under test.
     * @param example  game tree to use
     * @param expectedSearchResult collection of info regarding the search result.
     */
    protected void verifyResult(GameTreeExample example, SearchResult expectedSearchResult) {

        example.print();
        SearchStrategy<TwoPlayerMoveStub> searchStrategy = createSearchStrategy();
        TwoPlayerMoveStub foundMove =
                searchStrategy.search(example.getInitialMove(), null);

        String prefix = searchStrategy.getClass().getName();

        int inheritedValue = determineInheritedValue(foundMove.getInheritedValue(), searchStrategy, example);

        SearchResult actualResult =
                new SearchResult(foundMove.getId(), inheritedValue, searchStrategy.getNumMovesConsidered());

        if (searchStrategy instanceof MemorySearchStrategy) {
            TranspositionTable table = ((MemorySearchStrategy) searchStrategy ).getTranspositionTable();
            System.out.println("table=" + table);
        }
        assertEquals(prefix + " Unexpected search result", expectedSearchResult, actualResult);
    }

    /**
     * This does not need to be as complicated as I once thought.
     * @return  adjusted inherited value
     */
    protected int determineInheritedValue(int value, SearchStrategy searchStrategy, GameTreeExample example) {
        if (searchStrategy.getEvaluationPerspective() == CURRENT_PLAYER) {
            return example.getInitialMove().isPlayer1() ? -value : value;
        }
        return value;
    }
}