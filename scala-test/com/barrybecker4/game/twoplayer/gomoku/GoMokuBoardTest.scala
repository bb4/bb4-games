/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku

import org.junit.Assert.assertEquals
import org.junit.Test

/** @author Barry Becker */
class GoMokuBoardTest:

  /** instance under test */
  private var board: GoMokuBoard = scala.compiletime.uninitialized

  @Test
  def testBoardSize(): Unit =
    board = new GoMokuBoard(10, 11)

    assertEquals("Unexpected num rows. ", 10, board.getNumRows)
    assertEquals("Unexpected num columns. ", 11, board.getNumCols)

  @Test
  def testGetMaxMoves(): Unit =
    board = new GoMokuBoard(8, 10)

    assertEquals("Unexpected number of max moves. ", 80, board.getMaxNumMoves)
