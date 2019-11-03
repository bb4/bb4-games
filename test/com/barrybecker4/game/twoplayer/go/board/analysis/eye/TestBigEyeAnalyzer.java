/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye;

import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeInformation;
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.EyeType;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzer;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap;
import com.barrybecker4.game.twoplayer.go.board.elements.eye.GoEyeSet;
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;

/**
 * Test that we can get the correct type and status for all the different eyes that can arise.
 *
 * @author Barry Becker
 */
public class TestBigEyeAnalyzer extends GoTestCase {

    private static final String PATH_PREFIX = "board/analysis/eye/";


    public void testE111223() throws Exception {

        restore(PATH_PREFIX + "BigEye_E111223");

        GoBoard board = getBoard();
        checkEyeType(board, EyeType.E6.getInformation("E111223"), true, true, true);
    }

    public void testE111223b() throws Exception {

        restore(PATH_PREFIX + "BigEye_E111223b");

        GoBoard board = getBoard();
        checkEyeType(board, EyeType.E6.getInformation("E111223"), true, true, true);
    }

    /**
     * Check information for specified eye.
     */
    protected void checkEyeType(GoBoard board,
                              EyeInformation expectedInfo, boolean isBlack,
                              boolean isInCorner, boolean isOnEdge) {

        IGoGroup group = getBiggestGroup(isBlack);
        GroupAnalyzer groupAnalyzer = new GroupAnalyzer(group, new GroupAnalyzerMap());

        GoEyeSet eyes = groupAnalyzer.getEyes(board);

        assertEquals("The group\n" + group + "\n did not have one eye",
                1, eyes.size());
        IGoEye firstEye = groupAnalyzer.getEyes(board).iterator().next();

        BigEyeAnalyzer eyeAnalyzer = new BigEyeAnalyzer(firstEye);


        EyeInformation information = eyeAnalyzer.determineEyeInformation();

        String eyeColor = isBlack? "black" : "white";
        assertEquals("Unexpected information found for " + eyeColor + " eye.",
                expectedInfo, information);

        assertEquals("Corner status unexpected", isInCorner, information.isInCorner(firstEye));
        assertEquals("Edge status unexpected", isOnEdge, information.isOnEdge(firstEye));
    }
}
