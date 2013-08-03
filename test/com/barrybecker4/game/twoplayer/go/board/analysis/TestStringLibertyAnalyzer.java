// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.analysis;

import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.GoBoardConfigurator;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionList;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;
import com.barrybecker4.game.twoplayer.go.board.elements.string.GoString;

/**
 * Verify expected number of liberties for given string.
 * @author Barry Becker
 */
public class TestStringLibertyAnalyzer extends GoTestCase {

    private GoBoardConfigurator boardConfig;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        boardConfig = new GoBoardConfigurator(9, 0);
    }

    public void testSingleStoneLiberties() {

        GoBoardPositionList list = new GoBoardPositionList();
        list.add(createBlackStone(4, 4));

        verifyNumberOfLiberties(4, list);
    }

    public void testTwoStringLiberties() {

        GoBoardPositionList list = new GoBoardPositionList();
        list.add(createBlackStone(4, 4));
        list.add(createBlackStone(4, 5));

        verifyNumberOfLiberties(6, list);
    }

    /**
     *  XX
     *   X
     */
    public void testThreeStringLiberties() {

        GoBoardPositionList list = new GoBoardPositionList();
        list.add(createBlackStone(4, 4));
        list.add(createBlackStone(4, 5));
        list.add(createBlackStone(5, 5));

        verifyNumberOfLiberties(7, list);
    }

    /**
     *  OO
     *  XX
     *   XO
     */
    public void testThreeStringLibertiesWithOther() {

        GoBoardPositionList list = new GoBoardPositionList();
        list.add(createBlackStone(4, 4));
        list.add(createBlackStone(4, 5));
        list.add(createBlackStone(5, 5));

        GoBoardPositionList otherList = new GoBoardPositionList();
        otherList.add(createWhiteStone(3, 4));
        otherList.add(createWhiteStone(3, 5));
        otherList.add(createWhiteStone(5, 6));

        verifyNumberOfLibertiesWithOther(4, list, otherList);
    }

    /**
     *  XXX
     *  X X
     *  XXX
     */
    public void testOStringLiberties() {

        GoBoardPositionList list = new GoBoardPositionList();
        list.add(createBlackStone(4, 4));
        list.add(createBlackStone(4, 5));
        list.add(createBlackStone(4, 6));
        list.add(createBlackStone(5, 4));
        list.add(createBlackStone(5, 6));
        list.add(createBlackStone(6, 4));
        list.add(createBlackStone(6, 5));
        list.add(createBlackStone(6, 6));

        verifyNumberOfLiberties(13, list);
    }

    /**
     *  ___
     *  X |
     *  XX|
     */
    public void testInCornerStringLiberties() {

        GoBoardPositionList list = new GoBoardPositionList();
        list.add(createBlackStone(1, 8));
        list.add(createBlackStone(2, 8));
        list.add(createBlackStone(2, 9));

        verifyNumberOfLiberties(5, list);
    }

    private void verifyNumberOfLiberties(int expectedNumber, GoBoardPositionList list) {

        verifyNumberOfLibertiesWithOther(expectedNumber, list, null);
    }

    private void verifyNumberOfLibertiesWithOther(int expectedNumber,
                               GoBoardPositionList list, GoBoardPositionList otherList) {

        GoBoard board = boardConfig.getBoard();
        GoString string = new GoString(list, board);
        boardConfig.setPositions(list);
        if (otherList != null) {
            boardConfig.setPositions(otherList);
        }
        StringLibertyAnalyzer analyzer = new StringLibertyAnalyzer(board, string);
        assertEquals("Unexpected number of liberties",
                expectedNumber, analyzer.getLiberties().size());
    }

    /**
     * create a black stone to add to the string.
     */
    private GoBoardPosition createBlackStone(int row, int col) {
        return new GoBoardPosition(row, col, null, new GoStone(true, 0.5f));
    }

    /**
     * create a white stone to for other spots on the board.
     */
    private GoBoardPosition createWhiteStone(int row, int col) {
        return new GoBoardPosition(row, col, null, new GoStone(false, 0.5f));
    }
}
