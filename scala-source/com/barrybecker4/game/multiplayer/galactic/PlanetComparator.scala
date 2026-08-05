/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic

import java.util.Comparator

class PlanetComparator(comparisonPlanet_ : Planet) extends Comparator[Planet]:
  assert(comparisonPlanet_ != null, "you must specify a comparison planet.")

  override def compare(p1: Planet, p2: Planet): Int =
    val p1Dist = p1.getDistanceFrom(comparisonPlanet_)
    val p2Dist = p2.getDistanceFrom(comparisonPlanet_)
    if p1Dist < p2Dist then -1
    else if p1Dist > p2Dist then 1
    else 0
