package com.barrybecker4.game.twoplayer.common.search.strategy.testcase;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.common.xml.DomUtil;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Barry Becker
 */
public class GameTree {

    private static final int FAKE_BOARD_SIZE = 19;
    int moveCount = 0;

    TwoPlayerMoveStub root;

    GameTree(Node rootNode) {
        root = createTreeFromNode(rootNode, null);
    }

    /**
     * Create the tree rooted at node recursively.
     * @param node current root
     * @param parent parent of the node
     * @return the new move node (with children if any)
     */
    private TwoPlayerMoveStub createTreeFromNode(Node node, TwoPlayerMoveStub parent) {
        boolean isPlayer1 = parent == null || !parent.isPlayer1();
        TwoPlayerMoveStub current = createMoveFromNode(node, isPlayer1, parent);
        NodeList children = node.getChildNodes();
        for (int i=0; i<children.getLength(); i++) {
             createTreeFromNode(children.item(i), current);
        }
        return current;
    }

    /**
     * @return  the root move in the game tree.
     */
    public TwoPlayerMoveStub getInitialMove() {
       return root;
    }

    /**
     * Print the tree in depth first search for debugging purposes
     */
    public void print() {
        print(root, "");
    }

    private void print(TwoPlayerMoveStub moveNode, String indent) {
        System.out.println(indent + "Move: " + moveNode);
        for (TwoPlayerMoveStub move : moveNode.getChildren()) {
             print(move, indent + "  ");
        }
    }

    private TwoPlayerMoveStub createMoveFromNode(Node node, boolean isPlayer1, TwoPlayerMoveStub parent) {

        int val = Integer.parseInt(DomUtil.getAttribute(node, "value"));
        return new TwoPlayerMoveStub(val, isPlayer1, createToLocation(), parent);
    }

    /**
     * The location is not really used, just give it something unique so the hash works.
     * @return new to location
     */
    private Location createToLocation() {
        moveCount++;
        return new ByteLocation(moveCount / FAKE_BOARD_SIZE, moveCount % FAKE_BOARD_SIZE);
    }
}
