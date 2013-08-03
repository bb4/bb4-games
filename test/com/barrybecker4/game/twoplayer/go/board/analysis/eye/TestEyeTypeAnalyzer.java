/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye;

import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeInformation;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeStatus;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeType;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzer;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap;
import com.barrybecker4.game.twoplayer.go.board.elements.eye.GoEyeSet;
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye;
import com.barrybecker4.game.twoplayer.go.board.elements.group.GoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet;
import junit.framework.Assert;

import java.util.Set;

/**
 * Test that we can get the correct type and status for all the different eyes that can arise.
 *
 * @author Barry Becker
 */
public abstract class TestEyeTypeAnalyzer extends GoTestCase {

    protected static final String PATH_PREFIX = "board/analysis/eye/information/";

    private enum GroupType {BIGGEST, SURROUNDED}

    protected GoBoard initializeBoard(String eyesProblemFile) throws Exception {
        return initializeBoard(eyesProblemFile, 2);
    }

    /**
     * @return Place where test SGF files are stored.
     */
    protected String getPathPrefix() {
        return PATH_PREFIX + getEyeType().toString() + "/";
    }

    protected abstract EyeType getEyeType();

    /**
     * @param eyesProblemFile saved sgf game file to load
     * @return the initialized board. Must have 2 groups.
     */
    protected GoBoard initializeBoard(
            String eyesProblemFile, int expectedNumGroups) throws Exception {
        System.out.println("finding eyes for " + eyesProblemFile + "...");
        restore(getPathPrefix() + eyesProblemFile);

        GoBoard board = getBoard();

        // consider the 2 biggest groups
        Set<IGoGroup> groups = board.getGroups();
        Assert.assertEquals("Unexpected number of groups.",
                expectedNumGroups, groups.size());

        return board;
    }

    protected void checkBlackEye(GoBoard board, EyeInformation expectedInfo, EyeStatus expectedStatus) {
         checkEyeInfo(board, expectedInfo, expectedStatus, true, false, false, GroupType.BIGGEST);
    }

    protected void checkWhiteEye(GoBoard board, EyeInformation expectedInfo, EyeStatus expectedStatus) {
         checkEyeInfo(board, expectedInfo, expectedStatus, false, false, false, GroupType.BIGGEST);
    }

    protected void checkEdgeBlackEye(GoBoard board, EyeInformation expectedInfo, EyeStatus expectedStatus) {
        checkEyeInfo(board, expectedInfo, expectedStatus, true, false, true, GroupType.BIGGEST);
    }

    protected void checkEdgeWhiteEye(GoBoard board, EyeInformation expectedInfo, EyeStatus expectedStatus) {
        checkEyeInfo(board, expectedInfo, expectedStatus, false, false, true, GroupType.BIGGEST);
    }

    protected void checkCornerBlackEye(GoBoard board, EyeInformation expectedInfo, EyeStatus expectedStatus) {
        checkEyeInfo(board, expectedInfo, expectedStatus, true, true, true, GroupType.BIGGEST);
    }

    protected void checkCornerWhiteEye(GoBoard board, EyeInformation expectedInfo, EyeStatus expectedStatus) {
        checkEyeInfo(board, expectedInfo, expectedStatus, false, true, true, GroupType.BIGGEST);
    }

    /** Check the eye of the surrounded group, not the biggest group as we usually do. */
    protected void checkBlackEyeSurrounded(GoBoard board, EyeInformation expectedInfo, EyeStatus expectedStatus) {
         checkEyeInfo(board, expectedInfo, expectedStatus, true, false, false, GroupType.SURROUNDED);
    }

    /** Check the eye of the surrounded group, not the biggest group as we usually do. */
    protected void checkWhiteEyeSurrounded(GoBoard board, EyeInformation expectedInfo, EyeStatus expectedStatus) {
         checkEyeInfo(board, expectedInfo, expectedStatus, false, false, false, GroupType.SURROUNDED);
    }

    /**
     * Check information and status for specified eye.
     */
    protected void checkEyeInfo(GoBoard board,
                              EyeInformation expectedInfo, EyeStatus expectedStatus, boolean isBlack,
                              boolean isInCorner, boolean isOnEdge, GroupType groupType) {

        IGoGroup group = getGroupToCheck(isBlack, groupType, board);
        GroupAnalyzer groupAnalyzer = new GroupAnalyzer(group, new GroupAnalyzerMap());

        GoEyeSet eyes = groupAnalyzer.getEyes(board);

        assertEquals("The group\n" + group + "\n did not have one eye.",
                1, eyes.size());
        IGoEye firstEye = groupAnalyzer.getEyes(board).iterator().next();

        EyeTypeAnalyzer eyeAnalyzer = new EyeTypeAnalyzer(firstEye, board, groupAnalyzer);
        EyeInformation information = eyeAnalyzer.determineEyeInformation();
        String eyeColor = isBlack? "black" : "white";
        assertEquals("Unexpected information found for " + eyeColor + " eye.",
                expectedInfo, information);
        EyeStatus status = information.determineStatus(firstEye, board);
        assertEquals("Unexpected status found for " + eyeColor + " eye in group=" + group,
                expectedStatus, status);

        assertEquals("Corner status unexpected.", isInCorner, information.isInCorner(firstEye));
        assertEquals("Edge status unexpected.", isOnEdge, information.isOnEdge(firstEye));
    }

    private IGoGroup getGroupToCheck(boolean isBlack, GroupType type, GoBoard board) {
        switch (type) {
            case BIGGEST : return getBiggestGroup(isBlack);
            case SURROUNDED : return getSurroundedGroup(isBlack, board);
        }
        return null;
    }

    /**
     * @param isBlack true if black
     * @return a large group of the specified side with 2 or fewer liberties.
     */
    protected IGoGroup getSurroundedGroup(boolean isBlack, GoBoard board) {

        Set<IGoGroup> groups = getBoard().getGroups();
        IGoGroup surroundedGroup = null;

        for (IGoGroup group : groups) {
            GoBoardPositionSet stones = ((GoGroup)group).getStones();
            if (stones.iterator().next().getPiece().isOwnedByPlayer1() == isBlack) {
                if (surroundedGroup == null ||
                    (group.getNumStones() > 5 && group.getLiberties(board).size() < 3)) {
                    surroundedGroup = group;
                }
            }
        }
        return surroundedGroup;
    }
}