package com.barrybecker4.game.twoplayer.common.search.strategy.testcase;

import com.barrybecker4.common.xml.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

/**
 * Reads in the test case definition from a file, runs the search methods specified and verifies the expected results.
 * @author Barry Becker
 */
public class SearchTester {
    String name;
    SearchTestCases testCases;
    GameTree gameTree;

    public SearchTester(File file) {
        Document doc = DomUtil.parseXMLFile(file);

        Node root = doc.getDocumentElement();

        name =  DomUtil.getAttribute(root, "name");
        NodeList children = root.getChildNodes();

        testCases = new SearchTestCases(children.item(0));
        gameTree = new GameTree(children.item(1));

        System.out.println("TREE = ");
        gameTree.print();
    }
}
