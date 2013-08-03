// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move;

import com.barrybecker4.game.twoplayer.blockade.BlockadeTestCase;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall;

import static com.barrybecker4.game.twoplayer.blockade.board.BlockadeTstUtil.createMove;

/**
 * @author Barry Becker
 */
public class MovePlacementValidatorTest extends BlockadeTestCase {

    /** instance under test */
    private MovePlacementValidator validator;

    private BlockadeBoard board;

    BlockadeBoardPosition topLeft;
    BlockadeBoardPosition topRight;
    BlockadeBoardPosition bottomLeft;

    BlockadeBoardPosition topLeftLeft;
    BlockadeBoardPosition topRightRight;
    BlockadeBoardPosition topTopLeft;
    BlockadeBoardPosition bottomBottomLeft;


    /**
     * Common initialization for all test cases.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        board = new BlockadeBoard(7, 6);
        validator = new MovePlacementValidator(board);

        topLeft = board.getPosition(3, 3);
        topRight = board.getPosition(3, 4);
        bottomLeft = board.getPosition(4, 3);

        topTopLeft = board.getPosition(2, 3);
        topLeftLeft = board.getPosition(3, 2);
        topRightRight = board.getPosition(3, 5);
        bottomBottomLeft = board.getPosition(5, 3);
    }

    public void testSouthEastMoveNotBlockedWhenNopWall() {
        verifyNotBlocked(createMove(3, 3, 4, 4));
    }

    public void testSouthEastMoveNotBlockedWhenBottomWall() {
        board.addWall(new BlockadeWall(bottomLeft, bottomBottomLeft));
        verifyNotBlocked(createMove(3, 3, 4, 4));
    }
    public void testSouthEastMoveNotBlockedWhenLeftWall() {
        board.addWall(new BlockadeWall(topLeftLeft, topLeft));
        verifyNotBlocked(createMove(3, 3, 4, 4));
    }
    public void testSouthEastMoveNotBlockedWhenRightWall() {
        board.addWall(new BlockadeWall(topRight, topRightRight));
        verifyNotBlocked(createMove(3, 3, 4, 4));
    }
    public void testSouthEastMoveNotBlockedWhenTopWall() {
        board.addWall(new BlockadeWall(topTopLeft, topLeft));
        verifyNotBlocked(createMove(3, 4, 4, 3));
    }
    public void testSouthEastMoveBlockedWhenVertWall() {
        board.addWall(new BlockadeWall(topLeft, bottomLeft));
        verifyBlocked(createMove(3, 3, 4, 4));
    }
    public void testSouthEastMoveBlockedWhenHorzWall() {
        board.addWall(new BlockadeWall(topLeft, topRight));
        verifyBlocked(createMove(3, 3, 4, 4));
    }


    public void testSouthWestMoveNotBlockedWhenBottomWall() {
        board.addWall(new BlockadeWall(bottomLeft, bottomBottomLeft));
        verifyNotBlocked(createMove(3, 4, 4, 3));
    }
    public void testSouthWestMoveNotBlockedWhenLeftWall() {
        board.addWall(new BlockadeWall(topLeftLeft, topLeft));
        verifyNotBlocked(createMove(3, 4, 4, 3));
    }
    public void testSouthWestMoveNotBlockedWhenRightWall() {
        board.addWall(new BlockadeWall(topRight, topRightRight));
        verifyNotBlocked(createMove(3, 4, 4, 3));
    }
    public void testSouthWestMoveNotBlockedWhenTopWall() {
        board.addWall(new BlockadeWall(topTopLeft, topLeft));
        verifyNotBlocked(createMove(3, 4, 4, 3));
    }
    public void testSouthWestMoveBlockedWhenVertWall() {
        board.addWall(new BlockadeWall(topLeft, bottomLeft));
        verifyBlocked(createMove(3, 4, 4, 3));
    }
    public void testSouthWestMoveBlockedWhenHorzWall() {
        board.addWall(new BlockadeWall(topLeft, topRight));
        verifyBlocked(createMove(3, 4, 4, 3));
    }



    public void testNorthWestMoveNotBlockedWhenBottomWall() {
        board.addWall(new BlockadeWall(bottomLeft, bottomBottomLeft));
        verifyNotBlocked(createMove(4, 4, 3, 3));
    }
    public void testNorthWestMoveNotBlockedWhenLeftWall() {
        board.addWall(new BlockadeWall(topLeftLeft, topLeft));
        verifyNotBlocked(createMove(4, 4, 3, 3));
    }
    public void testNorthWestMoveNotBlockedWhenRightWall() {
        board.addWall(new BlockadeWall(topRight, topRightRight));
        verifyNotBlocked(createMove(4, 4, 3, 3));
    }
    public void testNorthWestMoveNotBlockedWhenTopWall() {
        board.addWall(new BlockadeWall(topTopLeft, topLeft));
        verifyNotBlocked(createMove(4, 4, 3, 3));
    }
    public void testNorthWestMoveBlockedWhenVertWall() {
        board.addWall(new BlockadeWall(topLeft, bottomLeft));
        verifyBlocked(createMove(4, 4, 3, 3));
    }
    public void testNorthWestMoveBlockedWhenHorzWall() {
        board.addWall(new BlockadeWall(topLeft, topRight));
        verifyBlocked(createMove(4, 4, 3, 3));
    }


    public void testNorthEastMoveNotBlockedWhenBottomWall() {
        board.addWall(new BlockadeWall(bottomLeft, bottomBottomLeft));
        verifyNotBlocked(createMove(4, 3, 3, 4));
    }
    public void testNorthEastMoveNotBlockedWhenLeftWall() {
        board.addWall(new BlockadeWall(topLeftLeft, topLeft));
        verifyNotBlocked(createMove(4, 3, 3, 4));
    }
    public void testNorthEastMoveNotBlockedWhenRightWall() {
        board.addWall(new BlockadeWall(topRight, topRightRight));
        verifyNotBlocked(createMove(4, 3, 3, 4));
    }
    public void testNorthEastMoveNotBlockedWhenTopWall() {
        board.addWall(new BlockadeWall(topTopLeft, topLeft));
        verifyNotBlocked(createMove(4, 3, 3, 4));
    }
    public void testNorthEastMoveBlockedWhenVertWall() {
        board.addWall(new BlockadeWall(topLeft, bottomLeft));
        verifyBlocked(createMove(4, 3, 3, 4));
    }
    public void testNorthEastMoveBlockedWhenHorzWall() {
        board.addWall(new BlockadeWall(topLeft, topRight));
        verifyBlocked(createMove(4, 3, 3, 4));
    }


    private void verifyNotBlocked(BlockadeMove move) {
       assertFalse("Move " + move + " unexpectedly blocked", validator.isMoveBlocked(move));
    }

    private void verifyBlocked(BlockadeMove move) {
       assertTrue("Move " + move + " unexpectedly not blocked", validator.isMoveBlocked(move));
    }

}
