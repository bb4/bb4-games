// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis.differencers

import com.barrybecker4.game.twoplayer.gomoku.GoMokuBoard
import com.barrybecker4.game.twoplayer.gomoku.analysis.Direction
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns
import org.junit.Test

/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
class StraightVerticalDifferencerTest extends ValueDifferencerTst:

  override protected def createDifferencer(board: GoMokuBoard, patterns: Patterns): ValueDifferencer =
    differencerFactory.createValueDifferencer(board, Direction.VERTICAL)

  @Test def testBlank(): Unit = verifyLine(4, 1, "_________")
  @Test def testBottomLeftCorner(): Unit = verifyLine(10, 1, "______")
  @Test def testBottomRightCorner(): Unit = verifyLine(10, 8, "______")
  @Test def testTopLeftCorner(): Unit = verifyLine(1, 1, "______")
  @Test def testTopRightCorner(): Unit = verifyLine(1, 8, "______")
  @Test def testPlayer1(): Unit = verifyLine(1, 4, "XOX_XX")
  @Test def testPlayer2(): Unit = verifyLine(2, 4, "XOX_XXO")
  @Test def testPlayer2Single(): Unit = verifyLine(5, 2, "____O___X_")
  @Test def testPlayer1Single(): Unit = verifyLine(9, 2, "_O___X_")
  @Test def testPlayer1Surrounded(): Unit = verifyLine(3, 4, "XOX_XXO_")
  @Test def testPlayer2Surrounded(): Unit = verifyLine(2, 4, "XOX_XXO")
  @Test def testPlayer2Edge(): Unit = verifyLine(10, 7, "X__O_O")
  @Test def testPlayer2AmongFriends(): Unit = verifyLine(8, 7, "O_X__O_O")
