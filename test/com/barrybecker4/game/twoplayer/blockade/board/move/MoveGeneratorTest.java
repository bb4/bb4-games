/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.move;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.blockade.BlockadeTestCase;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall;

import java.util.List;

/**
 * Test methods on MoveGenerator
 *
 * @author Barry Becker
 */
public class MoveGeneratorTest extends BlockadeTestCase {

    public void  testGetWallsForMove() throws Exception {
        restore("whitebox/moveList1");

        // List<BlockadeWall> walls = generator.getWallsForMove(move, paths);
        // verify that the list of walls is what we expect.
        //GameContext.log(2, "Walls=" + walls);
    }

    public void testGenerateMoves() throws Exception {

        restore("whitebox/noMoves2");

        BlockadeBoard board = (BlockadeBoard)controller_.getBoard();
        BlockadeMove lastMove = (BlockadeMove) controller_.getMoveList().getLastMove();
        MoveGenerator generator = new MoveGenerator(controller_.getComputerWeights().getDefaultWeights(), board);

        List moves = generator.generateMoves(lastMove);
        int expectedNumMoves = 56;
        assertTrue("Expected there to be "+expectedNumMoves+" moves but got " +moves.size()
                +" moves="+ moves, moves.size() == expectedNumMoves);
    }

    public void testGenerateMoves2() throws Exception {

        restore("whitebox/noMoves2");
        BlockadeBoard board = (BlockadeBoard)controller_.getBoard();

        GamePiece piece1 = new GamePiece(true); // player 1
        GamePiece piece2 = new GamePiece(false);  // player 2
        BlockadeWall wall1 =
                new BlockadeWall(board.getPosition(8, 10), board.getPosition(9, 10));
        BlockadeWall wall2 =
                new BlockadeWall(board.getPosition(12, 6), board.getPosition(12, 7));

        BlockadeMove move1 = BlockadeMove.createMove(new ByteLocation(8, 11), new ByteLocation(6, 11),  1 /*0.1*/, piece2, wall2);
        BlockadeMove move2 = BlockadeMove.createMove(new ByteLocation(12,6), new ByteLocation(10, 6), 1 /*0.1*/, piece1, wall1);

        controller_.makeMove(move1);
        controller_.makeMove(move2);

        BlockadeMove lastMove = (BlockadeMove) controller_.getMoveList().getLastMove();
        GameContext.log(2, "lastMove="+lastMove);

        MoveGenerator generator = new MoveGenerator(controller_.getComputerWeights().getDefaultWeights(), board);
        List moves = generator.generateMoves(lastMove);

        int expectedNumMoves = 60;
        assertTrue("Expected there to be " + expectedNumMoves
                + " moves but got " +moves.size() +" moves="+ moves,
                moves.size() == expectedNumMoves);
    }

}
