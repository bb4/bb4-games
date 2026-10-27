package com.barrybecker4.game.twoplayer.hex.pathsearch

import com.barrybecker4.common.geometry.Location

/**
 * @author Barry Becker
 */
class HexTransition(private val location: Location, private val cost: Int) 
    extends Comparable[HexTransition]:

  def getCost: Int = cost

  def getLocation: Location = location

  override def toString: String = s"[$location: $cost]"

  override def compareTo(that: HexTransition): Int =
    this.getCost - that.getCost
