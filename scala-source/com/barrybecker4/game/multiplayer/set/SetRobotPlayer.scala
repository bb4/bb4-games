/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set

import java.awt.Color
import SetRobotPlayer.RobotType

class SetRobotPlayer(name: String, private val robotType_ : RobotType, color: Color) 
    extends SetPlayer(name, color, false) {

  def getRobotType: String = robotType_.name()

  private def getSpeed: Int = {
    robotType_ match {
      case RobotType.SLOW_ROBOT => 30000
      case RobotType.AVERAGE_ROBOT => 15000
      case RobotType.EXPERT_ROBOT => 5000
    }
  }
}

object SetRobotPlayer {
  enum RobotType extends java.lang.Enum[RobotType] {
    case SLOW_ROBOT, AVERAGE_ROBOT, EXPERT_ROBOT
  }

  private var seq_ = 0

  def getSequencedRobotPlayer(name: String, color: Color): SetRobotPlayer = {
    val r = seq_ % RobotType.values.length
    seq_ += 1
    SetRobotPlayer(name, RobotType.values(r), color)
  }
}
