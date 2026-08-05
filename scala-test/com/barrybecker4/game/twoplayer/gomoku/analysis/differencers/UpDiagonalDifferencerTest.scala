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
class UpDiagonalDifferencerTest extends ValueDifferencerTst:

  override protected def createDifferencer(board: GoMokuBoard, patterns: Patterns): ValueDifferencer =
    differencerFactory.createValueDifferencer(board, Direction.UP_DIAGONAL)

  @Test def testBlank(): Unit = verifyLine(3, 1, "___")
  @Test def testLeftSide(): Unit = verifyLine(4, 1, "___X")
  @Test def testBottomLeftCorner(): Unit = verifyLine(10, 1, "_XXOOX")
  @Test def testBottomRightCorner(): Unit = verifyLine(10, 8, "_")
  @Test def testTopLeftCorner(): Unit = verifyLine(1, 1, "_")
  @Test def testTopRightCorner(): Unit = verifyLine(1, 8, "XX_O__")
  @Test def testTopSide(): Unit = verifyLine(1, 4, "___X")
  @Test def testPlayer1(): Unit = verifyLine(5, 5, "___XX_O_")
  @Test def testPlayer2(): Unit = verifyLine(5, 2, "_O_X__")
  @Test def testPlayer2Single(): Unit = verifyLine(2, 4, "___O_")
  @Test def testPlayer1Single(): Unit = verifyLine(9, 2, "_XXOOX_")
  @Test def testPlayer1Surrounded(): Unit = verifyLine(5, 4, "__XX_O__")
  @Test def testPlayer2Surrounded(): Unit = verifyLine(7, 4, "_XXOOX__")
  @Test def testPlayer2Edge(): Unit = verifyLine(10, 7, "O_")
  @Test def testPlayer2AmongFriends(): Unit = verifyLine(8, 7, "__O_")
