// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.analysis;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.Homes;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.blockade.board.path.Path;
import com.barrybecker4.game.twoplayer.blockade.board.path.PathList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Finds the shortest path from a given position to a home base.
 *
 * @author Barry Becker
 */
class ShortestPathFinder {

    private BlockadeBoard board;
    private PossibleMoveAnalyzer moveAnalyzer;

    /**
     * Constructor.
     */
    public ShortestPathFinder(BlockadeBoard board) {
        this.board = board;
        moveAnalyzer = new PossibleMoveAnalyzer(board);
    }

    /**
     * Find the shortest paths from the specified position to opponent homes.
     * We use DefaultMutableTreeNodes to represent nodes in the path.
     * If the number of paths returned by this method is less than NUM_HOMES,
     * then there has been an illegal wall placement, since according to the rules
     * of the game there must always be paths from all pieces to all opponent homes.
     * If a pawn has reached an opponent home then the path magnitude is 0 and that player won.
     *
     * @param position position to check shortest paths for.
     * @return the NUM_HOMES shortest paths from toPosition.
     */
    public PathList findShortestPaths(BlockadeBoardPosition position)  {

        boolean opponentIsPlayer1 = !position.getPiece().isOwnedByPlayer1();
        // set of home bases
        // use a LinkedHashMap so the iteration order is predictable.
        Set<MutableTreeNode> homeSet = new LinkedHashSet<>();
        // mark position visited so we don't circle back to it.
        position.setVisited(true);

        List<DefaultMutableTreeNode> queue = new LinkedList<>();
        // the user object at the root is null, because there is no move there.
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(null);
        // if we are sitting on a home, then need to add it to the homeBase set.
        if (position.isHomeBase(opponentIsPlayer1)) {
           homeSet.add(root);
        }
        else {
            queue.addAll(findPathChildren(position, root, opponentIsPlayer1));

            // do a breadth first search until you have spanned/visited all opponent homes.
            while (homeSet.size() < Homes.NUM_HOMES && !queue.isEmpty()) {
                // pop the next move from the head of the queue.
                DefaultMutableTreeNode node = queue.remove(0);
                BlockadeMove nodeMove = (BlockadeMove)node.getUserObject();
                BlockadeBoardPosition toPosition =
                        board.getPosition(nodeMove.getToRow(), nodeMove.getToCol());
                if (!toPosition.isVisited()) {
                    toPosition.setVisited(true);
                    MutableTreeNode parentNode = (MutableTreeNode)node.getParent();
                    node.setParent(null);
                    parentNode.insert(node, parentNode.getChildCount());
                    if (toPosition.isHomeBase(opponentIsPlayer1)) {
                        homeSet.add(node);
                    }
                    List<DefaultMutableTreeNode> children = findPathChildren(toPosition, node, opponentIsPlayer1);
                    queue.addAll(children);
                }
            }
        }
        // extract the paths by working backwards to the root from the homes.
        PathList paths = extractPaths(homeSet);

        unvisitAll();
        return paths;
    }

    /**
     * Find moves going to unvisited positions that can be reached without going through walls.
     * @param pos the place we are moving from.
     * @param parent the parent node for the child moves
     * @param oppPlayer1 the opposing player (opposite of pies at pos).
     * @return a list of TreeNodes containing all the moves that lead to unvisited positions.
     */
    private List<DefaultMutableTreeNode> findPathChildren(
            BlockadeBoardPosition pos, MutableTreeNode parent, boolean oppPlayer1) {
        List<BlockadeMove> moves = moveAnalyzer.getPossibleMoveList(pos, oppPlayer1);
        List<DefaultMutableTreeNode> children = new ArrayList<>();

        for (BlockadeMove move : moves) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(move);
            childNode.setParent(parent);
            children.add(childNode);
        }
        return children;
    }

    /**
     * return everything to an unvisited state.
     */
    private void unvisitAll() {
        for ( int i = 1; i <= board.getNumRows(); i++ ) {
            for ( int j = 1; j <= board.getNumCols(); j++ ) {
                board.getPosition(i, j).setVisited(false);
            }
        }
    }

    /**
     * There will be a path to each home base from each pawn.
     * Extract the paths by working backwards to the root from the homes.
     * @param homeSet set of home base positions.
     * @return extracted paths
     */
    private PathList extractPaths(Set<MutableTreeNode> homeSet) {
        PathList paths = new PathList();

        for (MutableTreeNode home : homeSet) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)home;
            Path path = new Path(node);
            // if the path is not > 0 then then pawn is on the homeBase and the game has been won.
            if (path.getLength() == 0) {
                GameContext.log(2, "found 0 magnitude path =" + path +" for home "  + node);
            }
            paths.add(path);
        }
        return paths;
    }
}
