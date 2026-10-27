// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.pattern

import org.junit.Assert.assertEquals
import org.junit.Test

/** @author Barry Becker */
class PatternTableTest:

  private var patternTable: PatternTable = scala.compiletime.uninitialized

  @Test
  def testGetWeightIndexForUninitializedTable(): Unit =
    patternTable = new PatternTable()

    // not a recognizable pattern.
    val pattern = new StringBuilder("XX")
    val wtIndex = patternTable.getWeightIndexForPattern(pattern, 0, 0)
    assertEquals(-1, wtIndex)

  @Test
  def testGetWeightIndexForXXPattern(): Unit =
    patternTable = new PatternTable()
    patternTable.setPatternWeightInTable("XX", 0)

    assertEquals(0, patternTable.getWeightIndexForPattern("XX", 0, 1))

  @Test
  def testGetWeightIndexForXXXPattern(): Unit =
    patternTable = new PatternTable()
    patternTable.setPatternWeightInTable("XXX", 7)

    assertEquals(7, patternTable.getWeightIndexForPattern("XXX", 0, 2))
    assertEquals(-1, patternTable.getWeightIndexForPattern("XXX", 0, 1))

  /** Check that the reverse was also added. */
  @Test
  def testGetWeightIndexFor_XXPattern(): Unit =
    patternTable = new PatternTable()
    patternTable.setPatternWeightInTable("_XX", 3)

    assertEquals(3, patternTable.getWeightIndexForPattern("_XX", 0, 2))
    assertEquals(3, patternTable.getWeightIndexForPattern("XX_", 0, 2))
    assertEquals(-1, patternTable.getWeightIndexForPattern("X_X", 0, 2))

  /** Check that the reverse was also added. */
  @Test
  def testGetWeightIndexForOOX_XXPattern(): Unit =
    patternTable = new PatternTable()
    patternTable.setPatternWeightInTable("OOX_XX", 99)

    assertEquals(99, patternTable.getWeightIndexForPattern("OOX_XX", 0, 5))
    assertEquals(99, patternTable.getWeightIndexForPattern("XX_XOO", 0, 5))
    assertEquals(99, patternTable.getWeightIndexForPattern("XXX_XX", 0, 5))
    assertEquals(-1, patternTable.getWeightIndexForPattern("XOOO_X", 0, 5))
    assertEquals(-1, patternTable.getWeightIndexForPattern("XO_X", 0, 3))
