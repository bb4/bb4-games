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
class DownDiagonalDifferencerTest extends ValueDifferencerTst:

  override protected def createDifferencer(board: GoMokuBoard, patterns: Patterns): ValueDifferencer =
    differencerFactory.createValueDifferencer(board, Direction.DOWN_DIAGONAL)

  @Test def testBlank(): Unit = verifyLine(2, 1, "___XO_")
  @Test def testLeftEdge(): Unit = verifyLine(4, 1, "_OXOO_")
  @Test def testBottomLeftCorner(): Unit = verifyLine(10, 1, "_")
  @Test def testBottomRightCorner(): Unit = verifyLine(10, 8, "_X_O__")
  @Test def testTopLeftCorner(): Unit = verifyLine(1, 1, "____X_")
  @Test def testTopRightCorner(): Unit = verifyLine(1, 8, "_")
  @Test def testPlayer1(): Unit = verifyLine(1, 4, "X_O__")
  @Test def testPlayer2(): Unit = verifyLine(2, 4, "_O__X_")
  @Test def testPlayer2Single(): Unit = verifyLine(5, 2, "_OXOO_O")
  @Test def testPlayer1Single(): Unit = verifyLine(9, 2, "_X_")
  @Test def testPlayer1Surrounded(): Unit = verifyLine(6, 3, "_OXOO_O")
  @Test def testPlayer2Surrounded(): Unit = verifyLine(7, 4, "_OXOO_O")
  @Test def testPlayer2Edge(): Unit = verifyLine(10, 7, "OXOO_O")
  @Test def testPlayer2AmongFriends(): Unit = verifyLine(8, 7, "__XO_O_")
