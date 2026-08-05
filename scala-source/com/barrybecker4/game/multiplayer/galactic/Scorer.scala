// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.galactic

import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer

class Scorer:
  def score(player: GalacticPlayer): Double =
    val playerPlanets = Galaxy.getPlanets(player)
    Scorer.PLANET_WEIGHT * playerPlanets.size +
      Scorer.SHIP_WEIGHT * player.getTotalNumShips.toDouble

object Scorer:
  private val PLANET_WEIGHT = 1.0
  private val SHIP_WEIGHT = 0.000000001
