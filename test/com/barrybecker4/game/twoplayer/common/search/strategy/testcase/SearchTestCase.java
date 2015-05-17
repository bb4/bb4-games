package com.barrybecker4.game.twoplayer.common.search.strategy.testcase;

import com.barrybecker4.common.xml.DomUtil;
import com.barrybecker4.game.twoplayer.common.search.options.BruteSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.MonteCarloSearchOptions;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import static com.barrybecker4.game.twoplayer.common.search.options.MonteCarloSearchOptions.MaximizationStyle;

/**
 * @author Barry Becker
 */
public class SearchTestCase {

    String className;
    BruteSearchOptions bruteSearchOptions;
    MonteCarloSearchOptions monteCarloSearchOptions;
    SearchResult expectedResult;

    SearchTestCase(Node testCaseNode) {
        className = DomUtil.getAttribute(testCaseNode, "name");
        bruteSearchOptions = new BruteSearchOptions();
        monteCarloSearchOptions = new MonteCarloSearchOptions();

        NodeList children = testCaseNode.getChildNodes();

        for (int i=0; i<children.getLength(); i++) {
            processNode(children.item(i));
        }
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
