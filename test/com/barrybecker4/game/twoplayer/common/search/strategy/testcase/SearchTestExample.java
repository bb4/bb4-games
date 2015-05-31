package com.barrybecker4.game.twoplayer.common.search.strategy.testcase;

import com.barrybecker4.common.xml.DomUtil;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

/**
 * Reads in the test case definition from the xml case file and
 * runs the search methods specified and verifies the expected results.
 * @author Barry Becker
 */
public class SearchTestExample {
    String name;
    SearchTestCases testCases;
    GameTree gameTree;

    public SearchTestExample(File file) {
        Document doc = DomUtil.parseXMLFile(file);

        assert doc != null : "Check contents of " + file.getAbsolutePath();
        Node root = doc.getDocumentElement();

        name = DomUtil.getAttribute(root, "name");
        NodeList children = root.getChildNodes();

        testCases = new SearchTestCases(children.item(0));
        gameTree = new GameTree(children.item(1));

        //System.out.println("TREE = ");
        //gameTree.print();
    }

    public String getName() {
        return name;
    }

    public SearchTestCases getTestCases() {
        return testCases;
    }

    public TwoPlayerMoveStub getTree(boolean rootPlayer1) {

        return gameTree.getCloneWithRootPlayer1(rootPlayer1);
    }

}
