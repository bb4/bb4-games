/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;


/**
 * Stub implementation of TwoPlayerMove to help test the search strategy classes without needing
 * a specific game implementation.
 *
 * @author Barry Becker
 */
public class TwoPlayerMoveStub extends TwoPlayerMove {

    public static final String ROOT_ID = "root";

    /** child moves */
    private MoveList<TwoPlayerMoveStub> children_;

    /** every move but the root of the tree has a parent_ */
    private TwoPlayerMoveStub parent_;

    private boolean causedUrgency;


    public TwoPlayerMoveStub(int val, boolean isPlayer1, Location toLocation, TwoPlayerMoveStub parent) {
        super(toLocation, val, new GamePiece(isPlayer1));
        this.parent_ = parent;
        if (this.parent_ != null)  {
            this.parent_.addChild(this);
        }
        this.children_ = new MoveList<>();
    }

    public TwoPlayerMoveStub getParent() {
        return parent_;
    }

    private void addChild(TwoPlayerMoveStub move) {
        this.children_.add(move);
    }

    public MoveList<TwoPlayerMoveStub> getChildren() {
        return children_;
    }

    public void setCausedUrgency(boolean value) {
        causedUrgency = value;
    }

    public boolean causedUrgency() {
        return causedUrgency;
    }

    /**
     * Unique move id that defines where in the game tree this move resides.
     * Something we can match against when testing.
     * Each character in the id is a digit that corresponds to traveling a path
     * from the nth child from root to this node.
     *
     * @return unique id for move in tree
     */
    public String getId() {
        if (parent_ == null) {
            return ROOT_ID;
        }

        TwoPlayerMoveStub current = this;
        TwoPlayerMoveStub parentMove = getParent();
        String id = "";
        while (parentMove != null) {
            int index = parentMove.getChildren().indexOf(current);
            assert (index >= 0) :
                getValue() + " not found among " + getParent().getChildren().size()
                        + " parent children of " + getParent();
            id = Integer.toString(index) + id;
            current = parentMove;
            parentMove = parentMove.getParent();
        }
        return id;
    }

    public void print() {
        print("", this);
    }

    private void print(String indent, TwoPlayerMoveStub subtreeRoot)  {

        MoveList<TwoPlayerMoveStub> childList = subtreeRoot.getChildren();
        System.out.println(indent + subtreeRoot);
        for (TwoPlayerMove move : childList) {
            print("  " + indent, (TwoPlayerMoveStub)move);
        }
    }

    public String toString() {
        return "id:" + getId() + " value:" + getValue()
                + " inh val:" + getInheritedValue() + (isUrgent() ? " urgent" : " ")
                + (causedUrgency() ? "jeopardy" : "");
    }
}
