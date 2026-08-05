/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common

import com.barrybecker4.game.common.GameOptions

/**
 * @author Barry Becker
 */
class MultiGameOptions(
  private var maxNumPlayers: Int = MultiGameOptions.DEFAULT_PLAYER_LIMIT,
  private var numRobotPlayers: Int = MultiGameOptions.DEFAULT_NUM_ROBOT_PLAYERS
) extends GameOptions {

  def this() = this(MultiGameOptions.DEFAULT_PLAYER_LIMIT, MultiGameOptions.DEFAULT_NUM_ROBOT_PLAYERS)

  def getMinNumPlayers: Int = 2

  override def getMaxNumPlayers: Int = maxNumPlayers

  def setMaxNumPlayers(playerLimit: Int): Unit = {
    maxNumPlayers = playerLimit
  }

  def getNumRobotPlayers: Int = numRobotPlayers

  def setNumRobotPlayers(numRobotPlayers: Int): Unit = {
    this.numRobotPlayers = numRobotPlayers
  }

  override def testValidity(): String = {
    var msgs = ""
    if (getNumRobotPlayers > getMaxNumPlayers) {
      msgs += "The number of robot players cannot can exceed the total number of players."
    }
    if (msgs.length > 0) msgs else null
  }
}

object MultiGameOptions {
  private val DEFAULT_PLAYER_LIMIT: Int = 8
  private val DEFAULT_NUM_ROBOT_PLAYERS: Int = 1
}
