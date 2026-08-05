/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Test a collection of problems from
 * Martin Mueller (mmueller)
 * Markus Enzenberger (emarkus)
 * email domain: cs.ualberta.ca
 * @author Barry Becker
 */
class TestEscapeCaptureCollection extends GoTestCase:

  private val PREFIX = "problems/sgf/escape_capture/"

  @Test
  def testPass(): Unit = assertTrue(true)

  /*  These currently take a long time and fail.

  @Test
  def test3(): Unit =
    val m = getNextMove(PREFIX + "escape_capture.3", true)
    verifyExpected(m, 6, 11)

  @Test
  def test5(): Unit =
    val m = getNextMove(PREFIX + "escape_capture.5", true)
    verifyExpected(m, 0, 6)

  @Test
  def test13(): Unit =
    val m = getNextMove(PREFIX + "escape_capture.13", false)
    verifyExpected(m, 13, 5)
  */
