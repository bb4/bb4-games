/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set

import com.barrybecker4.game.common.{GameContext, Move}
import com.barrybecker4.game.common.player.PlayerAction
import com.barrybecker4.game.multiplayer.common.{MultiGameController, MultiGamePlayer}
import java.awt.Color
import java.text.MessageFormat

class SetPlayer(name: String, color: Color, isHuman: Boolean) extends MultiGamePlayer(name, color, isHuman) {

  private var numSetsFound_ = 0

  override def getAction(controller: MultiGameController[?, ?]): PlayerAction = {
    assert(false, "getAction not implemented yet")
    null
  }

  override def setAction(action: PlayerAction): Unit = 
    assert(false, "setAction implemented yet")

  def getDefaultName(i: Int): String = {
    val args = Array[Object](Integer.toString(i))
    MessageFormat.format(GameContext.getLabel("SET_DEFAULT_NAME"), args*)
  }

  def getNumSetsFound: Int = numSetsFound_

  def setNumSetsFound(numSetsFound: Int): Unit = {
    this.numSetsFound_ = numSetsFound
    if (numSetsFound_ < 0) {
      numSetsFound_ = 0
    }
  }

  def incrementNumSetsFound(): Unit = numSetsFound_ += 1

  def decrementNumSetsFound(): Unit = {
    if (numSetsFound_ > 0) {
      numSetsFound_ -= 1
    }
  }

  override def equals(player: Any): Boolean = {
    val p = player.asInstanceOf[SetPlayer]
    p.getName == getName && (p.isHuman == isHuman) && p.getColor == getColor
  }

  override def hashCode(): Int = getName.hashCode

  override def additionalInfo(): String = s"Num Sets Found: $numSetsFound_"
}

object SetPlayer {
  def createSetPlayer(name: String, color: Color, isHuman: Boolean): SetPlayer = {
    if (isHuman)
      SetPlayer(name, color, true)
    else
      SetRobotPlayer.getSequencedRobotPlayer(name, color)
  }
}
