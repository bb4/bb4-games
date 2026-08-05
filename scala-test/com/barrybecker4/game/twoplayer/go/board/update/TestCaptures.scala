/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update

import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import org.junit.Assert.assertEquals
import org.junit.{After, Before, Test}

/**
 * @author Barry Becker
 */
class TestCaptures extends GoTestCase:

  /** instance under test */
  private var captures: CaptureCounts = scala.compiletime.uninitialized

  @Before
  override def setUp(): Unit =
    captures = new CaptureCounts()
    super.setUp()

  @After
  override def tearDown(): Unit =
    super.tearDown()

  @Test
  def testNoCaptures(): Unit =
    assertEquals("Unexpected number of black captures", 0, captures.getNumCaptures(true))
    assertEquals("Unexpected number of white captures", 0, captures.getNumCaptures(false))

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
    assertEquals("Unexpected number of " + color + " captures", 2, captures.getNumCaptures(isPlayer1))
    captures.updateCaptures(move, true)
    assertEquals("Unexpected number of  " + color + " captures", 4, captures.getNumCaptures(isPlayer1))

  def verifyDecrCaptures(isPlayer1: Boolean): Unit =
    // first add some captures so they can be decremented
    val move = new GoMoveStub(new GoStone(!isPlayer1))
    move.setNumCaptures(3)
    captures.updateCaptures(move, true)
    move.setNumCaptures(1)
    captures.updateCaptures(move, false)
    val color = if isPlayer1 then "balck" else "white"
    assertEquals("Unexpected number of " + color + " captures", 2, captures.getNumCaptures(isPlayer1))
    captures.updateCaptures(move, false)
    assertEquals("Unexpected number of " + color + " captures", 1, captures.getNumCaptures(isPlayer1))
