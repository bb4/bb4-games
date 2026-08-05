// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.analysis

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.twoplayer.blockade.BlockadeTestCase
import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeTstUtil}
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import org.junit.Assert.assertEquals
import org.junit.Test

import java.util.{Arrays, List as JList}

/** @author Barry Becker */
class PossibleMoveAnalyzerTest extends BlockadeTestCase:

  @Test
  def testFindPossibleMoves(): Unit =
    val expMoves: JList[BlockadeMove] = Arrays.asList(
      BlockadeTstUtil.createMove(3, 3, 5, 3, 0, null, null),
      BlockadeTstUtil.createMove(3, 3, 4, 4, 0, null, null),
      BlockadeTstUtil.createMove(3, 3, 4, 2, 0, null, null),
      BlockadeTstUtil.createMove(3, 3, 3, 1, 0, null, null),
      BlockadeTstUtil.createMove(3, 3, 3, 4, 0, null, null),
      BlockadeTstUtil.createMove(3, 3, 2, 3, 0, null, null),
      BlockadeTstUtil.createMove(3, 3, 1, 3, 0, null, null),
      BlockadeTstUtil.createMove(3, 3, 3, 2, 0, null, null),
      BlockadeTstUtil.createMove(3, 3, 2, 3, 0, null, null),
      BlockadeTstUtil.createMove(3, 3, 3, 5, 0, null, null)
    )
    verifyPossibleMoves("board/analysis/initial5x7", new ByteLocation(3, 3), expMoves)

  @Test
  def testFindPossibleMovesWhenWalls(): Unit =
    val expMoves: JList[BlockadeMove] = Arrays.asList(
      BlockadeTstUtil.createMove(3, 3, 4, 4, 0, null, null),
      BlockadeTstUtil.createMove(3, 3, 2, 4, 0, null, null),
      BlockadeTstUtil.createMove(3, 3, 1, 3, 0, null, null),
      BlockadeTstUtil.createMove(3, 3, 2, 2, 0, null, null),
      BlockadeTstUtil.createMove(3, 3, 3, 5, 0, null, null)
    )
    verifyPossibleMoves("board/analysis/midGame5x7", new ByteLocation(3, 3), expMoves)

  private def verifyPossibleMoves(filename: String, loc: Location, expMoves: JList[BlockadeMove]): Unit =
    restore(filename)
    val board = controller_.getBoard.asInstanceOf[BlockadeBoard]
    val moveAnalyzer = new PossibleMoveAnalyzer(board)
    val position = board.getPosition(loc)
    val moves = moveAnalyzer.getPossibleMoveList(position, true)
    assertEquals("Unexpected possible moves.", expMoves, moves)
