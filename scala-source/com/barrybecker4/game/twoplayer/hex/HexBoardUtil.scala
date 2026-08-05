package com.barrybecker4.game.twoplayer.hex

import com.barrybecker4.common.geometry.{IntLocation, Location}

import java.awt.geom.Point2D

/**
 *  Used to find neighboring locations in hex space.
 *  Tiles are arranged like this:
 *       1,2   1,3   1,4
 *    2,1   2,2   2,3   2,4
 *       3,1   3,2   3,3
 */
object HexBoardUtil:

  /**
   * Odd rows are shifted back one.
   * @param loc source location
   * @param direction side to navigate to to find the neighbor. 0 is to the right, 1 is to the NE, etc.
   * @return the indicated neighbor of the specified tile.
   */
  def getNeighborLocation(loc: Location, direction: Int): Location =
    val row = loc.row
    val col = loc.col
    
    direction match
      case 0 => IntLocation(row, col + 1)
      case 1 => IntLocation(row - 1, col + 1)
      case 2 => IntLocation(row - 1, col)
      case 3 => IntLocation(row, col - 1)
      case 4 => IntLocation(row + 1, col - 1)
      case 5 => IntLocation(row + 1, col)
      case _ => throw new AssertionError()

  def isNeighbor(location1: Location, location2: Location): Boolean =
    val yDiff = location2.getY - location1.getY
    val xDiff = location2.getX - location1.getX
    if Math.abs(yDiff) <= 1.0 then
      if yDiff == -1 then
        xDiff == 1.0 || xDiff == 0
      else if yDiff == 0 then
        Math.abs(xDiff) == 1.0
      else
        xDiff == -1 || xDiff == 0
    else
      false

  /**
   * Convert to cartesian space, then computer the distance.
   * @param loc1 first location
   * @param loc2 second location
   * @return distance between two hex locations.
   */
  def distanceBetween(loc1: Location, loc2: Location): Double =
    val row1 = loc1.row
    val row2 = loc2.row
    val point1 = Point2D.Double(loc1.col + (row1 * 0.5), row1)
    val point2 = Point2D.Double(loc2.col + (row2 * 0.5), row2)
    point1.distance(point2)
