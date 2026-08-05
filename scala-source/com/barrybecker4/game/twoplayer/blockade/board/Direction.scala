/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.blockade.board

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import java.util.{HashMap, Map as JMap}

/**
 * Possible Blockade move directions.
 * Extends java.lang.Enum for Java interop.
 *
 * @author Barry Becker
 */
enum Direction(private val hashKey: Int) extends java.lang.Enum[Direction]:

  /** these directional constants are determined by the hash = 10*(2+rowDif) + 2+colDif */
  case NORTH_NORTH extends Direction(2)   // -2 0
  case NORTH_WEST extends Direction(11)   // -1 -1
  case NORTH extends Direction(12)        // -1 0
  case NORTH_EAST extends Direction(13)   // -1 1
  case WEST_WEST extends Direction(20)    // 0 -2
  case WEST extends Direction(21)         // 0 -1
  case EAST extends Direction(23)         // 0 1
  case EAST_EAST extends Direction(24)    // 0 2
  case SOUTH_WEST extends Direction(31)   // 1 -1
  case SOUTH extends Direction(32)        // 1 0
  case SOUTH_EAST extends Direction(33)   // 1 1
  case SOUTH_SOUTH extends Direction(42)  // 2 0

  /** @return the row and column offset for this direction. */
  def getOffset: Location =
    val colDif = (hashKey % 10) - 2
    val rowDif = (hashKey / 10) - 2
    new ByteLocation(rowDif, colDif)

object Direction:
  private val map: JMap[Integer, Direction] =
    val m = new HashMap[Integer, Direction](12)
    Direction.values.foreach(d => m.put(Integer.valueOf(d.hashKey), d))
    m

  /**
   * Get the direction given the row and column change.
   * @param rowDif row delta
   * @param colDif column delta
   * @return the direction enum value
   */
  def getDirection(rowDif: Int, colDif: Int): Direction =
    val hashKey = 10 * (2 + rowDif) + 2 + colDif
    map.get(hashKey)
