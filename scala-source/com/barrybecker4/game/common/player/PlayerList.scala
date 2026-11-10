/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.player

import java.util
import java.util.ArrayList
import scala.jdk.CollectionConverters.*

/**
 * A list of players.
 */
class PlayerList extends util.ArrayList[Player] {

  def getFirstPlayer: Player = get(0)

  def getPlayer1: Player = get(0)

  def getPlayer2: Player = get(1)

  def getNumPlayers: Int = size()

  def reset(): Unit = {
    this.asScala.foreach(_.setWon(false))
  }

  def anyPlayerWon: Boolean = {
    this.asScala.exists(_.hasWon)
  }

  def getWinningPlayer: Player = {
    this.asScala.find(_.hasWon).orNull
  }

  def allPlayersHuman: Boolean = {
    this.asScala.forall(_.isHuman)
  }

  def allPlayersComputer: Boolean = {
    this.asScala.forall(!_.isHuman)
  }
}
