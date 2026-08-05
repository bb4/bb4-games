/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search

/**
 * The different search strategies may have zero or more of the following attributes to
 * describe what options they support.
 * Extends java.lang.Enum for Java interop.
 *
 * @author Barry Becker
 */
enum SearchAttribute extends java.lang.Enum[SearchAttribute]:

  /** traditional strategies like minimax, negamax, and negascout use this. */
  case BRUTE_FORCE

  /** for strategies that use a transposition table to remember past results. */
  case MEMORY

  /** Narrow test window is used. Like aspiration, negascout, mtd strategies. */
  case ASPIRATION

  /** Stochastic in nature, like UCT. */
  case MONTE_CARLO
