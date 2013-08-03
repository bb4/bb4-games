// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye;

import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeInformation;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeStatus;
import com.barrybecker4.game.twoplayer.go.board.elements.eye.EyeSerializer;
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet;


/**
 * @author Barry Becker
 */
class StubGoEye implements IGoEye {

    private boolean ownedByPlayer1;
    private EyeStatus status;
    private EyeInformation eyeInfo;
    private int numCornerPoints;
    private int numEdgePoints;
    private boolean isUncondAlive;
    private int numMembers;

    public StubGoEye(boolean ownedByPlayer1, EyeStatus status, EyeInformation eyeInfo,
                     int numCornerPoints, int numEdgePoints,
                     boolean isUncondAlive, int numMembers) {

        this.ownedByPlayer1 = ownedByPlayer1;
        this.status = status;
        this.eyeInfo = eyeInfo;
        this.numCornerPoints = numCornerPoints;
        this.numEdgePoints = numEdgePoints;
        this.isUncondAlive = isUncondAlive;
        this.numMembers = numMembers;
    }

    @Override
    public EyeStatus getStatus() {
        return status;
    }

    @Override
    public EyeInformation getInformation() {
        return eyeInfo;
    }

    @Override
    public String getEyeTypeName() {
        return eyeInfo.getTypeName();
    }

    @Override
    public int getNumCornerPoints() {
        return numCornerPoints;
    }

    @Override
    public int getNumEdgePoints() {
        return numEdgePoints;
    }

    @Override
    public IGoGroup getGroup() {
        return null;
    }

    @Override
    public GoBoardPositionSet getMembers() {
        return null;
    }

    @Override
    public boolean isEnemy(GoBoardPosition pos) {
        throw new IllegalStateException("not supported");
    }

    @Override
    public boolean isOwnedByPlayer1() {
        return ownedByPlayer1;
    }

    @Override
    public void setVisited(boolean visited) {}

    @Override
    public int size() {
        return numMembers;
    }

    @Override
    public GoBoardPositionSet getLiberties(GoBoard board) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getNumLiberties(GoBoard board) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUnconditionallyAlive() {
        return isUncondAlive;
    }

    @Override
    public void setUnconditionallyAlive(boolean unconditionallyAlive) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param eye eye to test for rough equality with.
     * @return true if all the fields match the specified eye
     */
    public boolean isMatch(IGoEye eye) {

        boolean infoEqual = getInformation().equals(eye.getInformation());
        boolean pointsEqual = getNumCornerPoints() == eye.getNumCornerPoints()
                        && getNumEdgePoints() == eye.getNumEdgePoints();
        boolean match =
                   isOwnedByPlayer1() == eye.isOwnedByPlayer1()
                && isUnconditionallyAlive() == eye.isUnconditionallyAlive()
                && getStatus() == eye.getStatus()
                && infoEqual
                && pointsEqual
                && size() == eye.size();
        return match;
    }

    public String toString() {
        return new EyeSerializer(this).serialize();
    }

}