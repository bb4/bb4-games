/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search

import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy
import org.junit.jupiter.api.Assertions.{assertEquals, assertFalse, assertTrue}
import org.junit.jupiter.api.Test

/**
 * Unit tests for [[SearchWindow]], including the NegaMax/PVS child-window transform.
 */
class SearchWindowTest:

  @Test
  def testDefaultConstructorUsesInfinityBounds(): Unit =
    val w = new SearchWindow()
    assertEquals(SearchStrategy.INFINITY, w.alpha)
    assertEquals(-SearchStrategy.INFINITY, w.beta)

  @Test
  def testCopyIsIndependent(): Unit =
    val original = new SearchWindow(3, 9)
    val copied = original.copy()
    assertEquals(original, copied)
    copied.alpha = 1
    assertEquals(3, original.alpha)
    assertEquals(1, copied.alpha)

  @Test
  def testGetMidPoint(): Unit =
    assertEquals(5, new SearchWindow(0, 10).getMidPoint)
    assertEquals(0, new SearchWindow(-4, 4).getMidPoint)

  @Test
  def testEqualsAndHashCode(): Unit =
    val a = new SearchWindow(-10, 20)
    val b = new SearchWindow(-10, 20)
    val c = new SearchWindow(-10, 21)
    assertTrue(a.equals(b))
    assertEquals(a.hashCode(), b.hashCode())
    assertFalse(a.equals(c))

  /**
   * Classic NegaMax / PVS child window is (-beta, -alpha).
   * Negating without swapping yields an inverted (alpha > beta) window.
   */
  @Test
  def testNegateAndSwapReturnsNegatedSwappedBounds(): Unit =
    val window = new SearchWindow(alpha = -4, beta = 17)
    val child = window.negateAndSwap()
    assertEquals(-17, child.alpha, "child alpha should be -parent.beta")
    assertEquals(4, child.beta, "child beta should be -parent.alpha")

  @Test
  def testNegateAndSwapWithSymmetricWindow(): Unit =
    val window = new SearchWindow(-100, 100)
    val child = window.negateAndSwap()
    assertEquals(-100, child.alpha)
    assertEquals(100, child.beta)
