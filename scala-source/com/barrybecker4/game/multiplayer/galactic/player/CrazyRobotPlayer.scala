/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.player

import com.barrybecker4.game.multiplayer.galactic.{Galaxy, Order, Planet}

import java.awt.Color
import java.util.{ArrayList, List as JList}
import javax.swing.ImageIcon

class CrazyRobotPlayer(name: String, homePlanet: Planet, color: Color, icon: ImageIcon)
  extends GalacticRobotPlayer(name, homePlanet, color):

  if icon != null then
    icon_ = icon
  else
    iconBaseName_ = "crazy_robot_icon"
    iconIndex_ = CrazyRobotPlayer.iconIndexCounter_ % CrazyRobotPlayer.NUM_ICONS
    CrazyRobotPlayer.iconIndexCounter_ += 1

  override def makeOrders(galaxy: Galaxy, numYearsRemaining: Int): JList[Order] =
    val newOrders = new ArrayList[Order]()
    val ownedPlanets = Galaxy.getPlanets(this)
    val it = ownedPlanets.iterator()
    while it.hasNext do
      val origin = it.next()
      if origin.getNumShips > 200 then
        newOrders.addAll(getOrders(origin, 6, 20, numYearsRemaining))
      else if origin.getNumShips > 100 then
        newOrders.addAll(getOrders(origin, 5, 10, numYearsRemaining))
      else if origin.getNumShips > 50 then
        newOrders.addAll(getOrders(origin, 3, 5, numYearsRemaining))
      else if origin.getNumShips > 20 then
        newOrders.addAll(getOrders(origin, 1, 3, numYearsRemaining))
    orders_.addAll(newOrders)
    orders_

object CrazyRobotPlayer:
  private val NUM_ICONS = 6
  private var iconIndexCounter_ = 0
