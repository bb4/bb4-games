/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.options

/**
 * How to select the best node after Monte Carlo search.
 * Extends java.lang.Enum for Java interop.
 */
enum MaximizationStyle extends java.lang.Enum[MaximizationStyle]:
  case WIN_RATE, NUM_VISITS
