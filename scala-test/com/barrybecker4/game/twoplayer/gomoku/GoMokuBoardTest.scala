/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/** @author Barry Becker */
class GoMokuBoardTest:

  /** instance under test */
  private var board: GoMokuBoard = scala.compiletime.uninitialized

  @Test
  def testBoardSize(): Unit =
    board = new GoMokuBoard(10, 11)

    assertEquals(10, board.getNumRows, "Unexpected num rows. ")
    assertEquals(11, board.getNumCols, "Unexpected num columns. ")

  @Test
  def testGetMaxMoves(): Unit =
    board = new GoMokuBoard(8, 10)

    assertEquals(80, board.getMaxNumMoves, "Unexpected number of max moves. ")
