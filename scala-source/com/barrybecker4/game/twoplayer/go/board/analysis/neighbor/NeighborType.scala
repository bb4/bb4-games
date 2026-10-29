/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.board.analysis.neighbor

/**
 * Enum for the different possible Neighbor types.
 * These constants represent the types of possible neighbors that can be searched for.
 * Extends java.lang.Enum for Java interop.
 */
enum NeighborType extends java.lang.Enum[NeighborType]:

  /** Has a stone in the space */
  case OCCUPIED

  /** No stone at the nbr position. */
  case UNOCCUPIED

  /** nbr stone same color as current stone. */
  case FRIEND

  /** nbr stone enemy of current stone. */
  case ENEMY

  /** Enemy or unoccupied. */
  case NOT_FRIEND

  /** any kind of nbr. */
  case ANY
