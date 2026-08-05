/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.pattern

import org.junit.Assert.assertEquals
import org.junit.Test

/** @author Barry Becker */
class PatternsTest:

  private var patterns: Patterns = scala.compiletime.uninitialized

  /** Verify that we get -1 for patterns that are not in the set. */
  @Test
  def testGetWeightIndexForNonExistantSimplePatterns(): Unit =
    patterns = new SimplePatterns()

    // not a recognizable pattern. -1 means not found.
    verify(-1, "_")
    verify(-1, "X")
    verify(-1, "X__X")
    verify(-1, "__X")
    verify(-1, "X__")
    verify(-1, "X")

  /** Verify that we get -1 for patterns that are not in the set. */
  @Test
  def testGetWeightIndexForNonExistantEmbeddedPattern(): Unit =
    patterns = new SimplePatterns()

    val wtIndex = patterns.getWeightIndexForPattern("OX__O", 1, 3)
    assertEquals(-1, wtIndex)

  /** Verify that we get the correct index for patterns that are in the set. */
  @Test
  def testGetWeightIndexForSimplePatterns(): Unit =
    patterns = new SimplePatterns()

    verify(0, "_X")
    verify(0, "X_")
    verify(1, "_X_")
    verify(2, "XX")
    verify(2, "_XX_")

  /** Verify that we get the correct index for patterns that are in the set. */
  @Test
  def testGetWeightIndexForEmbeddedPattern(): Unit =
    patterns = new SimplePatterns()

    val wtIndex = patterns.getWeightIndexForPattern("OO_X_O", 2, 4)
    assertEquals(1, wtIndex)

  @Test
  def testGetWeightIndexForGoMokuPatternX_XX_XXX(): Unit =
    patterns = new GoMokuPatterns()
    val pattern = "X_XX_XXX"

    assertEquals(-1, patterns.getWeightIndexForPattern(pattern, 0, 1))
    assertEquals(0, patterns.getWeightIndexForPattern(pattern, 1, 3))
    assertEquals(0, patterns.getWeightIndexForPattern(pattern, 2, 4))
    assertEquals(2, patterns.getWeightIndexForPattern(pattern, 0, 3))
    assertEquals(4, patterns.getWeightIndexForPattern(pattern, 0, 4))
    assertEquals(4, patterns.getWeightIndexForPattern(pattern, 0, 5))
    assertEquals(6, patterns.getWeightIndexForPattern(pattern, 0, 6))
    assertEquals(6, patterns.getWeightIndexForPattern(pattern, 0, 7))

  @Test
  def testGetWeightIndexForGoMokuPatternXX_XX(): Unit =
    patterns = new GoMokuPatterns()
    val pattern = "XX_XX"

    assertEquals(-1, patterns.getWeightIndexForPattern(pattern, 0, 0))
    assertEquals(-1, patterns.getWeightIndexForPattern(pattern, 0, 1))
    assertEquals(0, patterns.getWeightIndexForPattern(pattern, 0, 2))
    assertEquals(2, patterns.getWeightIndexForPattern(pattern, 0, 3))
    assertEquals(6, patterns.getWeightIndexForPattern(pattern, 0, 4))
    assertEquals(6, patterns.getWeightIndexForPattern(pattern))

  private def verify(expIndex: Int, pattern: String): Unit =
    val wtIndex = patterns.getWeightIndexForPattern(pattern, 0, pattern.length - 1)
    assertEquals(expIndex, wtIndex)
