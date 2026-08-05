/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.player

import java.io.Serializable

enum RobotType(private val name_ : String) extends java.lang.Enum[RobotType] with Serializable {
  case CRAZY_ROBOT extends RobotType("Crazy")
  case METHODICAL_ROBOT extends RobotType("Methodical")
  case TIMID_ROBOT extends RobotType("Timid")

  def getName: String = name_
}
