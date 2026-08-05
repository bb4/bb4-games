/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.player

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.{Player, PlayerAction}
import com.barrybecker4.game.multiplayer.common.MultiGameController
import com.barrybecker4.game.multiplayer.poker.model.PokerAction
import com.barrybecker4.game.multiplayer.poker.PokerController
import java.awt.Color

abstract class PokerRobotPlayer(name: String, money: Int, color: Color, private val robotType_ : RobotType) 
    extends PokerPlayer(name, money, color, false) {

  override def setAction(action: PlayerAction): Unit = {
    assert(action != null)
    this.action = action.asInstanceOf[PokerAction]
  }

  override def getAction(controller: MultiGameController[_, _]): PlayerAction = {
    val a = if (action == null) {
      createAction(controller.asInstanceOf[PokerController])
    } else {
      action
    }
    action = null
    GameContext.log(0, s"in PokerRobotPlayer.getAction a=$a")
    a
  }

  def createAction(pc: PokerController): PokerAction

  def getType: String = robotType_.getName

  def getRaise(pc: PokerController): Int

  protected def min(a: Int, b: Int, c: Int): Int = 
    Math.min(Math.min(a, b), c)

  protected def allOthersFolded(pc: PokerController): Boolean = {
    val it = pc.getPlayers.iterator()
    while (it.hasNext) {
      val player = it.next()
      val pp = player.asInstanceOf[PokerPlayer]
      if (!pp.hasFolded && (pp ne this)) {
        return false
      }
    }
    true
  }
}

object PokerRobotPlayer {
  private var seq_ = 0

  def getRandomRobotPlayer(name: String, money: Int, color: Color): PokerRobotPlayer = {
    val r = (RobotType.values.length * Math.random()).toInt
    getRobotPlayer(RobotType.values(r), name, money, color)
  }

  def getSequencedRobotPlayer(name: String, money: Int, color: Color): PokerRobotPlayer = {
    val r = seq_ % RobotType.values.length
    seq_ += 1
    getRobotPlayer(RobotType.values(r), name, money, color)
  }

  private def getRobotPlayer(`type`: RobotType, name: String, money: Int, color: Color): PokerRobotPlayer = {
    `type` match {
      case RobotType.CRAZY_ROBOT => CrazyRobotPlayer(name, money, color, RobotType.CRAZY_ROBOT)
      case RobotType.METHODICAL_ROBOT => MethodicalRobotPlayer(name, money, color, RobotType.METHODICAL_ROBOT)
      case RobotType.TIMID_ROBOT => TimidRobotPlayer(name, money, color, RobotType.TIMID_ROBOT)
    }
  }
}
