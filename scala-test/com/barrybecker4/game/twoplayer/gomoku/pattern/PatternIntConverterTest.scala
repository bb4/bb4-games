// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.pattern

import org.junit.Assert.assertEquals
import org.junit.{Before, Test}

/** @author Barry Becker */
class PatternIntConverterTest:

  /** instance under test. */
  private var converter: PatternToIntConverter = scala.compiletime.uninitialized

  @Before
  def setUp(): Unit =
    converter = new PatternToIntConverter()

  /** converts to 1 in binary because we always have a leading 1 .*/
  @Test
  def testGetIntForEmptyPattern(): Unit =
    assertEquals(1, converter.convertPatternToInt(""))

  /** "_" converts to 10 in binary (i.e. 2 in decimal) */
  @Test
  def testGetIntForUnoccupied(): Unit =
    verify(2, Patterns.UNOCCUPIED.toString)

  /** Converts to 11 in binary */
  @Test
  def testGetIntForX(): Unit =
    verify(3, "X")

  @Test
  def testGetIntForO(): Unit =
    verify(3, "O")

  /** The character in the pattern does not matter. we just check that it is not the UNOCCUPIED char. */
  @Test
  def testGetIntForW(): Unit =
    verify(3, "W")

  /** Converts to 100 in binary */
  @Test
  def testGetIntFor__(): Unit =
    verify(4, "__")

  /** Converts to 101 in binary */
  @Test
  def testGetIntFor_X(): Unit =
    verify(5, "_X")

  /** Converts to 110 in binary */
  @Test
  def testGetIntForX_(): Unit =
    verify(6, "X_")

  /** Converts to 1000 in binary */
  @Test
  def testGetIntFor___(): Unit =
    verify(8, "___")

  /** Converts to 1100 in binary */
  @Test
  def testGetIntForX__(): Unit =
    verify(12, "X__")

  /** Converts to 1010 in binary */
  @Test
  def testGetIntFor_X_(): Unit =
    verify(10, "_X_")

  /** Converts to 1001 in binary */
  @Test
  def testGetIntFor__X(): Unit =
    verify(9, "__X")

  /** Converts to 1110 in binary */
  @Test
  def testGetIntForXX_(): Unit =
    verify(14, "XX_")

  /** Converts to 1011 in binary */
  @Test
  def testGetIntFor_XX(): Unit =
    verify(11, "_XX")

  /** Converts to 1111 in binary */
  @Test
  def testGetIntForXXX(): Unit =
    verify(15, "XXX")

  /** The character in the patter does not matter. we just check that it is not the UNOCCUPIED char. */
  @Test
  def testGetIntForNonXCharsInPatter(): Unit =
    verify(15, "Y X")

  /** Converts to 110101 in binary */
  @Test
  def testGetIntForX_X_X(): Unit =
    verify(53, "X_X_X")

  /** Converts to 110101 in binary */
  @Test
  def testGetIntForO_O_O(): Unit =
    verify(53, "O_O_O")

  /** Converts to 110101 in binary */
  @Test
  def testGettIntForO_X_O(): Unit =
    verify(53, "O_X_O")

  /** Converts to 110101 in binary given the range */
  @Test
  def testGetIntForOOX_X_XO(): Unit =
    val pattern = "OOX_X_XO"
    assertEquals("Unexpected integer for pattern.",
      53, converter.convertPatternToInt(pattern, 2, 6))

  private def verify(expectedInt: Int, pattern: String): Unit =
    assertEquals("Unexpected integer for pattern.",
      expectedInt, converter.convertPatternToInt(pattern))
