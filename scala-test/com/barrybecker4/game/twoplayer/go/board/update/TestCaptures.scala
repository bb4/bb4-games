/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update

import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class TestCaptures extends GoTestCase:

  /** instance under test */
  private var captures: CaptureCounts = scala.compiletime.uninitialized

  @BeforeEach
  override def setUp(): Unit =
    captures = new CaptureCounts()
    super.setUp()

  @AfterEach
  override def tearDown(): Unit =
    super.tearDown()

  @Test
  def testNoCaptures(): Unit =
    assertEquals(0, captures.getNumCaptures(true), "Unexpected number of black captures")
    assertEquals(0, captures.getNumCaptures(false), "Unexpected number of white captures")

  @Test
  def testIncBlackCaptures(): Unit = verifyIncCaptures(true)

  @Test
  def testIncWhiteCaptures(): Unit = verifyIncCaptures(false)

  @Test
  def testDecrBlackCaptures(): Unit = verifyDecrCaptures(true)

  @Test
  def testDecrWhiteCaptures(): Unit = verifyDecrCaptures(false)

  def verifyIncCaptures(isPlayer1: Boolean): Unit =
    val move = new GoMoveStub(new GoStone(!isPlayer1))
    move.setNumCaptures(2)
    captures.updateCaptures(move, true)
    val color = if isPlayer1 then "balck" else "white"
    assertEquals(2, captures.getNumCaptures(isPlayer1), "Unexpected number of " + color + " captures")
    captures.updateCaptures(move, true)
    assertEquals(4, captures.getNumCaptures(isPlayer1), "Unexpected number of  " + color + " captures")

  def verifyDecrCaptures(isPlayer1: Boolean): Unit =
    // first add some captures so they can be decremented
    val move = new GoMoveStub(new GoStone(!isPlayer1))
    move.setNumCaptures(3)
    captures.updateCaptures(move, true)
    move.setNumCaptures(1)
    captures.updateCaptures(move, false)
    val color = if isPlayer1 then "balck" else "white"
    assertEquals(2, captures.getNumCaptures(isPlayer1), "Unexpected number of " + color + " captures")
    captures.updateCaptures(move, false)
    assertEquals(1, captures.getNumCaptures(isPlayer1), "Unexpected number of " + color + " captures")
