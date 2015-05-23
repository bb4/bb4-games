package com.barrybecker4.game.twoplayer.common.search.strategy.testcase;

import com.barrybecker4.common.xml.DomUtil;
import com.barrybecker4.game.common.GameWeights;
import com.barrybecker4.game.common.GameWeightsStub;
import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.game.twoplayer.common.search.SearchableStub;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.BruteSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.MonteCarloSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;
import com.barrybecker4.optimization.parameter.ParameterArray;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.InvocationTargetException;

import static com.barrybecker4.game.twoplayer.common.search.options.MonteCarloSearchOptions.MaximizationStyle;

/**
 * Corresponds to a test-case node in the case xml. It describes which search algorithm
 * with what
 * @author Barry Becker
 */
public class SearchTestCase {

    /** the tests assume all the moves are used. We might want to also vary this later */
    private static final BestMovesSearchOptions BEST_MOVE_OPTIONS = new BestMovesSearchOptions(100, 0, 20);
    private String className;
    private BruteSearchOptions bruteSearchOptions;
    private MonteCarloSearchOptions monteCarloSearchOptions;
    private SearchResult expectedResult;

    SearchTestCase(Node testCaseNode) {
        className = DomUtil.getAttribute(testCaseNode, "name");
        bruteSearchOptions = new BruteSearchOptions();
        monteCarloSearchOptions = new MonteCarloSearchOptions();

        NodeList children = testCaseNode.getChildNodes();

        for (int i=0; i<children.getLength(); i++) {
            processNode(children.item(i));
        }
    }

    /**
     * Create teh strategy using reflection and pass it the searchable.
     * @return the search strategy to test
     */
    public SearchStrategy<TwoPlayerMoveStub> createSearchStrategy()
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {

        SearchOptions<TwoPlayerMoveStub, TwoPlayerBoard<TwoPlayerMoveStub>> searchOptions =
                new SearchOptions<>(bruteSearchOptions, BEST_MOVE_OPTIONS, monteCarloSearchOptions);
        Searchable searchable = new SearchableStub(searchOptions);
        GameWeights weights = new GameWeightsStub();

        // create the strategy using reflection
        return (SearchStrategy<TwoPlayerMoveStub>) Class.forName(className)
                .getConstructor(Searchable.class, ParameterArray.class)
                .newInstance(searchable, weights.getDefaultWeights());
    }

    public SearchResult getExpectedResult() {
        return expectedResult;
    }

    private void processNode(Node child) {
        String name = child.getNodeName();
        switch (name) {
            case "brute-force-options":
                bruteSearchOptions.setLookAhead(Integer.parseInt(DomUtil.getAttribute(child, "look-ahead")));
                bruteSearchOptions.setAlphaBeta(Boolean.parseBoolean(DomUtil.getAttribute(child, "alpha-beta")));
                bruteSearchOptions.setQuiescence(Boolean.parseBoolean(DomUtil.getAttribute(child, "quiescence")));
                bruteSearchOptions.setMaxQuiescentDepth(Integer.parseInt(DomUtil.getAttribute(child, "max-quiescent-depth")));
                break;
            case "monte-carlo-options":
                monteCarloSearchOptions.setMaxSimulations(Integer.parseInt(DomUtil.getAttribute(child, "max-simulations")));
                monteCarloSearchOptions.setExploreExploitRatio(Float.parseFloat(DomUtil.getAttribute(child, "explore-exploit-ratio")));
                monteCarloSearchOptions.setRandomLookAhead(Integer.parseInt(DomUtil.getAttribute(child, "random-look-ahead")));
                monteCarloSearchOptions.setMaxStyle(MaximizationStyle.valueOf(DomUtil.getAttribute(child, "max-style")));
                break;
            case "expected-search-result":
                String moveId = DomUtil.getAttribute(child, "move-id");
                int inheritedValue = Integer.parseInt(DomUtil.getAttribute(child, "inherited-value"));
                int numConsideredMoves = Integer.parseInt(DomUtil.getAttribute(child, "num-considered-moves"));
                expectedResult = new SearchResult(moveId, inheritedValue, numConsideredMoves);
                break;
            default:
                throw new IllegalArgumentException("Invalid element : " + name);
        }
    }
}
