/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test

/**
 * Verify that all the methods in GoBoard work as expected
 * @author Barry Becker
 */
class TestGoBoard extends GoTestCase:

  private val PREFIX = "board/"

  /** verify that the right stones are captured by a given move. */
  @Test
  def testFindCaptures1(): Unit =
    verifyCaptures("findCaptures1", new ByteLocation(5, 6), 6)

  @Test
  def testFindCaptures2(): Unit =
    verifyCaptures("findCaptures2", new ByteLocation(6, 6), 9)

  @Test
  def testFindCaptures3(): Unit =
    verifyCaptures("findCaptures3", new ByteLocation(5, 4), 7)

  @Test
  def testFindCaptures4(): Unit =
    verifyCaptures("findCaptures4", new ByteLocation(4, 8), 16)

  @Test
  def testFindCaptures5(): Unit =
    verifyCaptures("findCaptures5", new ByteLocation(10, 2), 11)

  private def verifyCaptures(file: String, moveLocation: Location, expNnumCaptures: Int): Unit =
    restore(PREFIX + file)
    val move = new GoMove(moveLocation, 0, new GoStone(true))
    val board = getBoard
    val numWhiteStonesBefore = board.getNumStones(false)
    controller.makeMove(move)
    val numWhiteStonesAfter = board.getNumStones(false)
    val actualNumCaptures = move.getNumCaptures
    assertTrue(
      "move.captures=" + actualNumCaptures + " expected " + expNnumCaptures,
      actualNumCaptures == expNnumCaptures
    )
    val diffWhite = numWhiteStonesBefore - numWhiteStonesAfter
    assertTrue(
      "diff in num white stones (" + diffWhite + ") not = numcaptures (" + expNnumCaptures + ')',
      diffWhite == expNnumCaptures
    )
    controller.undoLastMove()
    // verify that all the captured stones get restored to the board
    val numWhiteStonesAfterUndo = board.getNumStones(false)
    assertTrue(
      "numWhiteStonesBefore=" + numWhiteStonesBefore +
        " not equal numWhiteStonesAfter=" + numWhiteStonesAfterUndo,
      numWhiteStonesBefore == numWhiteStonesAfterUndo
    )

  @Test
  def testCausedAtari1(): Unit =
    restore(PREFIX + "causedAtari1")
    val m = new GoMove(new ByteLocation(4, 4), 0, new GoStone(false))
    val numInAtari = m.numStonesAtaried(getBoard)
    assertTrue("numInAtri=" + numInAtari + " expected=" + 4, numInAtari == 4)

  @Test
  def testCausedAtari2(): Unit =
    restore(PREFIX + "causedAtari2")
    val m = new GoMove(new ByteLocation(2, 12), 0, new GoStone(true))
    controller.makeMove(m)
    val board = controller.getBoard.asInstanceOf[GoBoard]
    val numInAtari = m.numStonesAtaried(board)
    assertTrue("numInAtri=" + numInAtari + " expected=" + 12, numInAtari == 12)

  @Test
  def testNumLiberties1(): Unit =
    verifyGroupLiberties("causedAtari2", 2, 9, 14, 2, 10, 2)

  @Test
  def testNumLiberties2(): Unit =
    verifyGroupLiberties("numLiberties2", 1, 2, 15, 3, 6, 16)

  @Test
  def testCopy(): Unit =
    val board = new GoBoard(3, 0)
    val center: Location = new ByteLocation(2, 2)
    board.makeMove(new GoMove(center, 1, new GoStone(true)))
    val boardCopy = board.copy()
    assertEquals("Center positions not equal.", board.getPosition(center), boardCopy.getPosition(center))

  private def verifyGroupLiberties(
    file: String,
    bRow: Int, bCol: Int, expectedBlackLiberties: Int,
    wRow: Int, wCol: Int, expectedWhiteLiberties: Int
  ): Unit =
    restore(PREFIX + file)
    val board = getBoard
    var pos = board.getPosition(bRow, bCol).asInstanceOf[GoBoardPosition]
    var numGroupLiberties = pos.getGroup.getLiberties(board).size
    assertTrue(
      "numGroupLiberties=" + numGroupLiberties + " expected=" + expectedBlackLiberties,
      numGroupLiberties == expectedBlackLiberties
    )
    pos = board.getPosition(wRow, wCol).asInstanceOf[GoBoardPosition]
    numGroupLiberties = pos.getGroup.getLiberties(board).size
    assertTrue(
      "numGroupLiberties=" + numGroupLiberties + " expected=" + expectedWhiteLiberties,
      numGroupLiberties == expectedWhiteLiberties
    )
