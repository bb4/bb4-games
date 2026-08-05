/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.player

import com.barrybecker4.game.multiplayer.galactic.{Galaxy, Order, Planet}

import java.awt.Color
import java.util.{ArrayList, List as JList}
import javax.swing.ImageIcon

class MethodicalRobotPlayer(name: String, homePlanet: Planet, color: Color, icon: ImageIcon)
  extends GalacticRobotPlayer(name, homePlanet, color):

  if icon != null then
    icon_ = icon
  else
    iconBaseName_ = "methodical_robot_icon"
    iconIndex_ = MethodicalRobotPlayer.iconIndexCounter_ % MethodicalRobotPlayer.NUM_ICONS
    MethodicalRobotPlayer.iconIndexCounter_ += 1

  override def makeOrders(galaxy: Galaxy, numYearsRemaining: Int): JList[Order] =
    val newOrders = new ArrayList[Order]()
    val ownedPlanets = Galaxy.getPlanets(this)
    val it = ownedPlanets.iterator()
    while it.hasNext do
      val origin = it.next()
      if origin.getNumShips > 200 then
        newOrders.addAll(getOrders(origin, 4, 50, numYearsRemaining))
      else if origin.getNumShips > 100 then
        newOrders.addAll(getOrders(origin, 3, 30, numYearsRemaining))
      else if origin.getNumShips > 50 then
        newOrders.addAll(getOrders(origin, 2, 20, numYearsRemaining))
    orders_.addAll(newOrders)
    orders_

object MethodicalRobotPlayer:
  private val NUM_ICONS = 7
  private var iconIndexCounter_ = 0
