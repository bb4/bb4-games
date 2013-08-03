// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.analysis.group;

import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.GoBoardConfigurator;
import com.barrybecker4.game.twoplayer.go.board.analysis.WorthCalculator;
import com.barrybecker4.game.twoplayer.go.board.elements.group.GoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;
import com.barrybecker4.game.twoplayer.go.options.GoWeights;

/**
 * Check that we can identify the weakest neighboring group.
 * @author Barry Becker
 */
public class TestWeakestGroupFinder extends GoTestCase {

    /** where to go for the test files. */
    private static final String PREFIX = "board/analysis/group/weakest_group_finder_";

    private static final GoWeights WEIGHTS = new GoWeights();

    /** instance under test. */
    private WeakestGroupFinder finder;
    private GroupAnalyzerMap analyzerMap = new GroupAnalyzerMap();


    public void testFindGroupOnEmpytyBoard() {
        GoBoardConfigurator boardConfig = new GoBoardConfigurator(9);

        boardConfig.setPositions(new GoBoardPosition(2, 3, null, new GoStone(true)));
        GoBoard board = boardConfig.getBoard();
        finder = createFinder(board);

        GoBoardPositionSet groupStones = new GoBoardPositionSet();
        groupStones.add((GoBoardPosition)board.getPosition(2, 3));

        GoGroup group = finder.findWeakestGroup(groupStones);
        assertNull("There should not be a weakest group if the board is empty", group);
    }

    public void testFindGroupWithSingleStoneNeighbor() throws Exception {
        GoBoard board = initFinder("single_stone");

        GoBoardPositionSet groupStones = new GoBoardPositionSet();
        groupStones.add((GoBoardPosition)board.getPosition(4, 3));

        GoGroup group = finder.findWeakestGroup(groupStones);
        assertEquals("Unexpected number of stones found.", 1, group.getNumStones());
    }

    public void testFindWeakestGroupWhenTwoCompetingGroups() throws Exception {
        GoBoard board = initFinder("two_groups");

        GoBoardPositionSet groupStones = new GoBoardPositionSet();
        // the two stone black group.
        groupStones.add((GoBoardPosition)board.getPosition(5, 6));
        groupStones.add((GoBoardPosition)board.getPosition(6, 6));

        GoGroup group = finder.findWeakestGroup(groupStones);
        assertEquals("Unexpected number of stones found.", 3, group.getNumStones());
    }

    public void testFindWeakestGroupWhenTwoCompetingGroups2() throws Exception {
        GoBoard board = initFinder("two_groups2");

        GoBoardPositionSet groupStones = new GoBoardPositionSet();
        // the two stone black group.
        groupStones.add((GoBoardPosition)board.getPosition(5, 6));
        groupStones.add((GoBoardPosition)board.getPosition(6, 6));

        GoGroup group = finder.findWeakestGroup(groupStones);
        assertEquals("Unexpected number of stones found.", 3, group.getNumStones());
    }


    private GoBoard initFinder(String fileSuffix) throws Exception {
        restore(PREFIX  + fileSuffix);
        GoBoard board = getBoard();

        finder = createFinder(board);
        /** need something to update the scores for the group  */
        Move move = controller_.getLastMove();
        WorthCalculator calculator = new WorthCalculator(board, analyzerMap);
        calculator.worth(move, WEIGHTS.getDefaultWeights());
        System.out.println("AMAP="+ analyzerMap);

        return board;
    }

    private WeakestGroupFinder createFinder(GoBoard board) {
        finder = new WeakestGroupFinder(board, analyzerMap);
        return finder;
    }
}