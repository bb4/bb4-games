/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.player

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.multiplayer.galactic.{Galaxy, Order, Planet, PlanetComparator}

import java.awt.Color
import java.util.{ArrayList, Collections, List as JList}
import javax.swing.ImageIcon

abstract class GalacticRobotPlayer(name: String, homePlanet: Planet, color: Color)
  extends GalacticPlayer(name, homePlanet, color, false):

  def makeOrders(galaxy: Galaxy, numYearsRemaining: Int): JList[Order]

  protected def getOrders(
      origin: Planet, numAttacks: Int, numShipsToLeaveBehind: Int, numYearsRemaining: Int
  ): JList[Order] =
    val orders = new ArrayList[Order]()
    val numShipsToSend = origin.getNumShips - numShipsToLeaveBehind
    val planets = Galaxy.getPlanets()
    Collections.sort(planets, new PlanetComparator(origin))

    val closestEnemies = new ArrayList[Planet]()
    val it = planets.iterator()
    var ct = 0
    while it.hasNext && ct < numAttacks do
      val p = it.next()
      if p.getOwner != origin.getOwner then
        closestEnemies.add(p)
        ct += 1

    val attackFleetSize = numShipsToSend / numAttacks
    val eit = closestEnemies.iterator()
    while eit.hasNext do
      val target = eit.next()
      val order = new Order(origin, target, attackFleetSize)
      if order.getTimeNeeded < numYearsRemaining then
        origin.deductShips(attackFleetSize)
        orders.add(order)

    GameContext.log(1, s"num orders=${orders.size}")
    orders

object GalacticRobotPlayer:
  private enum RobotType:
    case CRAZY_ROBOT, METHODICAL_ROBOT

  private var seq_ = 0

  def getRandomRobotPlayer(name: String, homePlanet: Planet, color: Color): GalacticRobotPlayer =
    val r = (RobotType.values.length * Math.random()).toInt
    getRobotPlayer(RobotType.fromOrdinal(r), name, homePlanet, color, null)

  def getSequencedRobotPlayer(name: String, homePlanet: Planet, color: Color): GalacticRobotPlayer =
    val r = seq_ % RobotType.values.length
    seq_ += 1
    getRobotPlayer(RobotType.fromOrdinal(r), name, homePlanet, color, null)

  def getSequencedRobotPlayer(
      name: String, homePlanet: Planet, color: Color, icon: ImageIcon
  ): GalacticRobotPlayer =
    val r = seq_ % RobotType.values.length
    seq_ += 1
    getRobotPlayer(RobotType.fromOrdinal(r), name, homePlanet, color, icon)

  private def getRobotPlayer(
      tpe: RobotType, name: String, homePlanet: Planet, color: Color, icon: ImageIcon
  ): GalacticRobotPlayer =
    tpe match
      case RobotType.CRAZY_ROBOT => new CrazyRobotPlayer(name, homePlanet, color, icon)
      case RobotType.METHODICAL_ROBOT => new MethodicalRobotPlayer(name, homePlanet, color, icon)
