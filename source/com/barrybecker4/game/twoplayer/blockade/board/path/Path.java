/** Copyright by Barry G. Becker, 2007-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.path;

import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.blockade.board.move.MovePlacementValidator;
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A path that connects a pawn to an opponent home base.
 * Each element of the path is a BlockadeMove
 *
 * @author Barry Becker
 */
public class Path {

    /** the path elements that represent steps to the opponent home. */
    private List<BlockadeMove> moves;

    /**
     * Creates a new instance of Path
     */
    private Path() {
        moves = new LinkedList<BlockadeMove>();
    }

    public Path(DefaultMutableTreeNode node) {
        this();
        addPathElements(node);
    }

    public Path(BlockadeMove[] moves) {
        this();
        for (BlockadeMove m : moves) {
            add(m);
        }
    }

    void add(BlockadeMove move) {
        moves.add(move);
    }

    public Iterator<BlockadeMove> iterator() {
        return moves.iterator();
    }

    public BlockadeMove get(int index) {
        return moves.get(index);
    }

    /**
     * @param wall check this wall to see if it is blocking this path.
     * @return true if the specified wall is blocking the paths.
     */
    public boolean isBlockedByWall(BlockadeWall wall, BlockadeBoard board) {
       MovePlacementValidator validator = new MovePlacementValidator(board);
       for (BlockadeMove move: moves) {
            if (validator.isMoveBlockedByWall(move, wall)) {
                return true;
            }
       }
       return false;
    }

    /**
     * @return true if a wall is blocking this path.
     */
    public boolean isBlocked(BlockadeBoard board) {
       MovePlacementValidator validator = new MovePlacementValidator(board);
       for (BlockadeMove move: moves) {
            if (validator.isMoveBlocked(move)) {
                return true;
            }
       }
       return false;
    }

    void addPathElements(DefaultMutableTreeNode node) {
        Object[] ps = node.getUserObjectPath();
        if (ps.length > 1)  {
            // skip the first null move.
            for (int k = 1; k < ps.length; k++) {
                add((BlockadeMove)ps[k]);
            }
        }
    }

    /**
     * @return the magnitude of the path.
     */
    public int getLength() {
        return moves.size();
    }

    public int getPathLength() {

        int total = 0;
        for (BlockadeMove move : moves) {
            total += move.getToLocation().getDistanceFrom(move.getFromLocation());
        }
        return total;
    }

    /**
     *return true if the 2 paths are equal.
     */
    @Override
    public boolean equals(Object path) {
        Path comparisonPath = (Path) path;
        if (comparisonPath.getLength() != this.getLength())
            return false;
        int i = 0;
        for (BlockadeMove move : moves) {
            if (!move.equals(comparisonPath.get(i++)))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash =  this.getLength() * 100000;
        for (BlockadeMove move : moves) {
            hash += move.hashCode() / 20;
        }
        return hash;
    }

    /**
     * Serialize list path.
     */
    @Override
    public String toString() {
        if (moves.isEmpty()) return "Path has 0 magnitude";

        StringBuilder bldr = new StringBuilder(32);
        for (BlockadeMove move: moves) {
            bldr.append('[').append(move.toString()).append("],");
        }
        // remove trailing comma
        bldr.deleteCharAt(bldr.length() -1);
        bldr.append("\n");
        return bldr.toString();
    }

}
