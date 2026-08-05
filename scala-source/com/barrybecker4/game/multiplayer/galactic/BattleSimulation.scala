/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic

import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer

import java.util.{LinkedList, List as JList}
import scala.compiletime.uninitialized

class BattleSimulation(order_ : Order, destPlanet_ : Planet):
  private var hits_ : JList[Player] = uninitialized
  private var numShipsAfterAttack_ = 0
  private var ownerAfterAttack_ : GalacticPlayer = uninitialized

  createSimulation(order_, destPlanet_)

  def getOwnerAfterAttack: GalacticPlayer = ownerAfterAttack_
  def getNumShipsAfterAttack: Int = numShipsAfterAttack_
  def getHitSequence: JList[Player] = hits_
  def getPlanet: Planet = destPlanet_
  def getOrder: Order = order_

  private[galactic] def createSimulation(order: Order, destPlanet: Planet): Unit =
    hits_ = new LinkedList[Player]()
    if order.getOwner == destPlanet.getOwner then
      addReinforcements(order, destPlanet)
    else
      doBattleSequence(order, destPlanet)

  private def doBattleSequence(order: Order, destPlanet: Planet): Unit =
    var numAttackShips = order.getFleetSize
    var numDefendShips = destPlanet.getNumShips
    val attacker: Player = order.getOwner
    val defender: Player = destPlanet.getOwner

    while numAttackShips > 0 && numDefendShips > 0 do
      val ratio = (0.5 + numDefendShips.toDouble / (numAttackShips + numDefendShips).toDouble) / 2.0
      if Math.random() > ratio then
        numAttackShips -= 1
        hits_.add(attacker)
      else
        numDefendShips -= 1
        hits_.add(defender)

    if numAttackShips == 0 then
      updateForDefenderVictory(destPlanet, numDefendShips)
    else
      assert(numDefendShips == 0)
      updateForAttackerVictory(order, numAttackShips)

  private def updateForDefenderVictory(destPlanet: Planet, numDefendShips: Int): Unit =
    ownerAfterAttack_ = destPlanet.getOwner
    numShipsAfterAttack_ = numDefendShips

  private def updateForAttackerVictory(order: Order, numAttackShips: Int): Unit =
    ownerAfterAttack_ = order.getOwner
    numShipsAfterAttack_ = numAttackShips

  private def addReinforcements(order: Order, destPlanet: Planet): Unit =
    numShipsAfterAttack_ = order.getFleetSize + destPlanet.getNumShips
    ownerAfterAttack_ = destPlanet.getOwner
