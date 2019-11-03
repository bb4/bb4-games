/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information;

import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye;
import com.barrybecker4.game.twoplayer.go.board.elements.group.GoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet;

import java.util.List;

/**
 * @author Barry Becker
 */
public class StubGoEye implements IGoEye {

    private GoBoardPositionSet members;
    boolean visited = false;

    public StubGoEye(List<GoBoardPosition> list) {

        this.members = new GoBoardPositionSet();
        this.members.addAll(list);
    }

    public StubGoEye(GoBoardPositionSet members) {
        this.members = members;
    }

    @Override
    public GoBoardPositionSet getMembers() {
        return members;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean isEnemy(GoBoardPosition pos) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOwnedByPlayer1() {
        return false;
    }

    @Override
    public void setVisited(boolean visited) {
        visited = false;
    }

    @Override
    public EyeInformation getInformation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EyeStatus getStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getEyeTypeName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getNumCornerPoints() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getNumEdgePoints() {
        throw new UnsupportedOperationException();
    }

    @Override
    public GoGroup getGroup() {
        return null;
    }

    @Override
    public boolean isUnconditionallyAlive() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUnconditionallyAlive(boolean unconditionallyAlive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public GoBoardPositionSet getLiberties(GoBoard board) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getNumLiberties(GoBoard board) {
        throw new UnsupportedOperationException();
    }
}
