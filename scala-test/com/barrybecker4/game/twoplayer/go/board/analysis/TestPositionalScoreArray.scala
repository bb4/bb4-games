// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.analysis

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Test that we create the expected positional array.
 * @author Barry Becker
 */
class TestPositionalScoreArray:

  /** instance under test. */
  private var scoreArray: PositionalScoreArray = scala.compiletime.uninitialized

  @Test
  def testPositionalArrayOfSize5(): Unit =
    scoreArray = PositionalScoreArray.getArray(5)
    val expectedValues: Array[Array[Float]] = Array(
      Array(-3.75f, -2.5f, -2.5f, -2.5f, -3.75f),
      Array(-2.5f, 0.0f, 0.0f, 0.0f, -2.5f),
      Array(-2.5f, 0.0f, 1.5f, 0.0f, -2.5f),
      Array(-2.5f, 0.0f, 0.0f, 0.0f, -2.5f),
      Array(-3.75f, -2.5f, -2.5f, -2.5f, -3.75f)
    )
    checkEqual(expectedValues, 5)

  @Test
  def testPositionalArrayOfSize9(): Unit =
    scoreArray = PositionalScoreArray.getArray(9)
    val expectedValues: Array[Array[Float]] = Array(
      Array(-3.75f, -2.5f, -2.5f, -2.5f, -2.5f, -2.5f, -2.5f, -2.5f, -3.75f),
      Array(-2.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -2.5f),
      Array(-2.5f, 0.0f, 1.5f, 1.0f, 1.0f, 1.0f, 1.5f, 0.0f, -2.5f),
      Array(-2.5f, 0.0f, 1.0f, 1.3499999f, 0.9f, 1.3499999f, 1.0f, 0.0f, -2.5f),
      Array(-2.5f, 0.0f, 1.0f, 0.9f, 0.0f, 0.9f, 1.0f, 0.0f, -2.5f),
      Array(-2.5f, 0.0f, 1.0f, 1.3499999f, 0.9f, 1.3499999f, 1.0f, 0.0f, -2.5f),
      Array(-2.5f, 0.0f, 1.5f, 1.0f, 1.0f, 1.0f, 1.5f, 0.0f, -2.5f),
      Array(-2.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -2.5f),
      Array(-3.75f, -2.5f, -2.5f, -2.5f, -2.5f, -2.5f, -2.5f, -2.5f, -3.75f)
    )
    checkEqual(expectedValues, 9)

  private def checkEqual(expectedValues: Array[Array[Float]], size: Int): Unit =
    var equal = true
    for i <- 1 to size do
      for j <- 1 to size do
        if expectedValues(i - 1)(j - 1) != scoreArray.getValue(i, j) then
          equal = false
    assertTrue("The array was not what we expected. Got:" + scoreArray, equal)
