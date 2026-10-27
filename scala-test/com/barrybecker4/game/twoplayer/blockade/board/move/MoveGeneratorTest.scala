/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.move

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.blockade.BlockadeTestCase
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall
import org.junit.Assert.assertTrue
import org.junit.Test

/** Test methods on MoveGenerator @author Barry Becker */
class MoveGeneratorTest extends BlockadeTestCase:

  @Test
  def testGetWallsForMove(): Unit =
    restore("whitebox/moveList1")

  @Test
  def testGenerateMoves(): Unit =
    restore("whitebox/noMoves2")
    val board = controller_.getBoard.asInstanceOf[BlockadeBoard]
    val lastMove = controller_.getMoveList.getLastMove.asInstanceOf[BlockadeMove]
    val generator = new MoveGenerator(controller_.getComputerWeights().getDefaultWeights, board)
    val moves = generator.generateMoves(lastMove)
    val expectedNumMoves = 56
    assertTrue(
      "Expected there to be " + expectedNumMoves + " moves but got " + moves.size + " moves=" + moves,
      moves.size == expectedNumMoves
    )

  @Test
  def testGenerateMoves2(): Unit =
    restore("whitebox/noMoves2")
    val board = controller_.getBoard.asInstanceOf[BlockadeBoard]
    val piece1 = new GamePiece(true)
    val piece2 = new GamePiece(false)
    val wall1 = new BlockadeWall(board.getPosition(8, 10), board.getPosition(9, 10))
    val wall2 = new BlockadeWall(board.getPosition(12, 6), board.getPosition(12, 7))
    val move1 = BlockadeMove.createMove(new ByteLocation(8, 11), new ByteLocation(6, 11), 1, piece2, wall2)
    val move2 = BlockadeMove.createMove(new ByteLocation(12, 6), new ByteLocation(10, 6), 1, piece1, wall1)
    controller_.makeMove(move1)
    controller_.makeMove(move2)
    val lastMove = controller_.getMoveList.getLastMove.asInstanceOf[BlockadeMove]
    GameContext.log(2, "lastMove=" + lastMove)
    val generator = new MoveGenerator(controller_.getComputerWeights().getDefaultWeights, board)
    val moves = generator.generateMoves(lastMove)
    val expectedNumMoves = 60
    assertTrue(
      "Expected there to be " + expectedNumMoves + " moves but got " + moves.size + " moves=" + moves,
      moves.size == expectedNumMoves
    )
