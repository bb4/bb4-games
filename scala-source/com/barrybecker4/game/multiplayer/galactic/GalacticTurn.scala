/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic

import com.barrybecker4.game.common.Move
import java.util.{ArrayList, List as JList}
import scala.compiletime.uninitialized

class GalacticTurn private () extends Move:
  private var battles_ : JList[BattleSimulation] = uninitialized

  def addSimulation(order: Order, destPlanet: Planet): Unit =
    addSimulation(new BattleSimulation(order, destPlanet))

  def addSimulation(battle: BattleSimulation): Unit =
    if battles_ == null then battles_ = new ArrayList[BattleSimulation]()
    battles_.add(battle)

  def getBattles: JList[BattleSimulation] = battles_

object GalacticTurn:
  def createMove(): GalacticTurn = new GalacticTurn()
