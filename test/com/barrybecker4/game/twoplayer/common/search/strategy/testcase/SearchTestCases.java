package com.barrybecker4.game.twoplayer.common.search.strategy.testcase;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;


/**
 * @author Barry Becker
 */
public class SearchTestCases extends ArrayList<SearchTestCase> {

    SearchTestCases(Node testCasesNode) {
       NodeList children = testCasesNode.getChildNodes();

        for (int i=0; i<children.getLength(); i++) {
            this.add(new SearchTestCase(children.item(i)));
        }
    }
}
