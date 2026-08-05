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
class StraightHorizontalDifferencerTest extends ValueDifferencerTst:

  override protected def createDifferencer(board: GoMokuBoard, patterns: Patterns): ValueDifferencer =
    differencerFactory.createValueDifferencer(board, Direction.HORIZONTAL)

  @Test def testBlank(): Unit = verifyLine(4, 1, "______")
  @Test def testBottomLeftCorner(): Unit = verifyLine(10, 1, "______")
  @Test def testBottomRightCorner(): Unit = verifyLine(10, 8, "____O_")
  @Test def testTopLeftCorner(): Unit = verifyLine(1, 1, "___X__")
  @Test def testTopRightCorner(): Unit = verifyLine(1, 8, "_X____")
  @Test def testPlayer1(): Unit = verifyLine(1, 4, "___X____")
  @Test def testPlayer2(): Unit = verifyLine(2, 4, "___O____")
  @Test def testPlayer2Single(): Unit = verifyLine(5, 2, "_O_XXXX")
  @Test def testPlayer1Single(): Unit = verifyLine(9, 2, "_X_____")
  @Test def testPlayer1Surrounded(): Unit = verifyLine(3, 4, "___X_OO_")
  @Test def testPlayer2Surrounded(): Unit = verifyLine(6, 5, "__XXO___")
  @Test def testPlayer2Edge(): Unit = verifyLine(10, 7, "_____O_")
  @Test def testPlayer2AmongFriends(): Unit = verifyLine(8, 6, "__X_OOO_")
