/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.Player
import java.io.Serializable
import java.util.ArrayList
import scala.jdk.CollectionConverters._

/**
 * A list of tables that players can sit down at in order to play a game in an online environment.
 */
class OnlineGameTableList extends ArrayList[OnlineGameTable] with Serializable:

  def changeName(oldName: String, newName: String): Unit =
    this.asScala.foreach(_.changeName(oldName, newName))

  /** Remove a player from the first table that contains them; drop the table if no humans remain. */
  def removePlayer(player: Player): Unit =
    removePlayerWhere(_.equals(player))

  /** Remove a player by name from the first table that contains them; drop the table if no humans remain. */
  def removePlayer(playerName: String): Unit =
    GameContext.log(0, s"OnlineGameTableList.removePlayer $playerName is leaving.")
    removePlayerWhere(_.getName.equals(playerName))

  private def removePlayerWhere(matches: Player => Boolean): Unit =
    val it = this.iterator()
    while it.hasNext do
      val table = it.next()
      val pit = table.getPlayers.iterator()
      var removed = false
      while pit.hasNext && !removed do
        val p = pit.next()
        if matches(p) then
          pit.remove()
          removed = true
      if removed then
        if table.hasNoHumanPlayers then it.remove()
        return

  def getTableReadyToPlay(playerName: String): Option[OnlineGameTable] =
    this.asScala.find(table => table.isReadyToPlay && table.hasPlayer(playerName))

  def join(tableName: String, newPlayer: Player): Unit =
    this.asScala.find(_.getName.equals(tableName)).foreach(_.addPlayer(newPlayer))

  override def toString: String = synchronized {
    val bldr = new StringBuilder("Tables:")
    this.asScala.foreach(bldr.append)
    bldr.toString()
  }
