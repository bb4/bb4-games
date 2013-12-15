// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move.wall;

import com.barrybecker4.game.twoplayer.blockade.BlockadeTestCase;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Barry Becker
 */
public class DiagonalWallCheckerTest extends BlockadeTestCase {

    /** instance under test */
    DiagonalWallChecker checker;
    BlockadeBoard board;

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
    @Before
    public void setUp() throws Exception {
        super.setUp();

        board = new BlockadeBoard(7, 5);
        checker = new DiagonalWallChecker(board);

        topLeft = board.getPosition(2, 3);
        topRight = board.getPosition(2, 4);
        bottomLeft = board.getPosition(3, 3);

        topTopLeft = board.getPosition(1, 3);
        topLeftLeft = board.getPosition(2, 2);
        topRightRight = board.getPosition(2, 5);
        bottomBottomLeft = board.getPosition(4, 3);
    }

    @Test
    public void testCheckWhenNoWallsOnBoard() {

        BlockadeWallList expWalls = new BlockadeWallList();
        expWalls.add(new BlockadeWall(topLeft, topRight));
        expWalls.add(new BlockadeWall(topLeft, bottomLeft));

        verifyWalls(expWalls);
    }

    @Test
    public void  testCheckWhenLeftWallOnBoard() {

        board.addWall(new BlockadeWall(topLeftLeft, topLeft));

        BlockadeWallList expWalls = new BlockadeWallList();
        expWalls.add(new BlockadeWall(topLeft, bottomLeft));
        expWalls.add(new BlockadeWall(topRight, topRightRight));

        verifyWalls(expWalls);
    }

    @Test
    public void testCheckWhenRightWallOnBoard() {

        board.addWall(new BlockadeWall(topRight, topRightRight));

        BlockadeWallList expWalls = new BlockadeWallList();
        expWalls.add(new BlockadeWall(topLeft, bottomLeft));
        expWalls.add(new BlockadeWall(topLeftLeft, topRight));

        verifyWalls(expWalls);
    }

    @Test
    public void testCheckWhenTopWallOnBoard() {

        board.addWall(new BlockadeWall(topTopLeft, topLeft));

        BlockadeWallList expWalls = new BlockadeWallList();
        expWalls.add(new BlockadeWall(topLeft, topRight));
        expWalls.add(new BlockadeWall(bottomLeft, bottomBottomLeft));

        verifyWalls(expWalls);
    }

    @Test
    public void testCheckWhenBottomWallOnBoard() {

        board.addWall(new BlockadeWall(bottomLeft, bottomBottomLeft));

        BlockadeWallList expWalls = new BlockadeWallList();
        expWalls.add(new BlockadeWall(topLeft, topRight));
        expWalls.add(new BlockadeWall(topTopLeft, topLeft));

        verifyWalls(expWalls);
    }

    @Test
    public void testCheckWhenLeftAndBottomWalls() {

        board.addWall(new BlockadeWall(topLeftLeft, topLeft));
        board.addWall(new BlockadeWall(bottomLeft, bottomBottomLeft));

        BlockadeWallList expWalls = new BlockadeWallList();
        expWalls.add(new BlockadeWall(topRight, topRightRight));
        expWalls.add(new BlockadeWall(topTopLeft, topLeft));

        verifyWalls(expWalls);
    }

    @Test
    public void testCheckWhenLeftAndTopWalls() {

        board.addWall(new BlockadeWall(topLeftLeft, topLeft));
        board.addWall(new BlockadeWall(topTopLeft, topLeft));

        BlockadeWallList expWalls = new BlockadeWallList();
        expWalls.add(new BlockadeWall(topRight, topRightRight));
        expWalls.add(new BlockadeWall(bottomLeft, bottomBottomLeft));

        verifyWalls(expWalls);
    }

    @Test
    public void testCheckWhenRightAndTopWalls() {

        board.addWall(new BlockadeWall(topTopLeft, topLeft));
        board.addWall(new BlockadeWall(topRight, topRightRight));

        BlockadeWallList expWalls = new BlockadeWallList();
        expWalls.add(new BlockadeWall(topLeftLeft, topLeft));
        expWalls.add(new BlockadeWall(bottomLeft, bottomBottomLeft));

        verifyWalls(expWalls);
    }

    @Test
    public void testCheckWhenRightAndBottomWalls() {

        board.addWall(new BlockadeWall(topRight, topRightRight));
        board.addWall(new BlockadeWall(bottomLeft, bottomBottomLeft));

        BlockadeWallList expWalls = new BlockadeWallList();
        expWalls.add(new BlockadeWall(topTopLeft, topLeft));
        expWalls.add(new BlockadeWall(topLeftLeft, topLeft));

        verifyWalls(expWalls);
    }

    private void verifyWalls(BlockadeWallList expWalls) {
        BlockadeWallList walls = checker.checkWalls(topLeft, topRight, bottomLeft);
        assertEquals("Unexpected walls", expWalls, walls);
    }
}
