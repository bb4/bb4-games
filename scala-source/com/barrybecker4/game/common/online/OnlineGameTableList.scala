/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.Player
import java.io.Serializable
import java.util.ArrayList
import scala.jdk.CollectionConverters._

/**
 * A list of tables that players can sit down at in order to play a game in an online environment.
 *
 * @author Barry Becker
 */
class OnlineGameTableList extends ArrayList[OnlineGameTable] with Serializable {

  def changeName(oldName: String, newName: String): Unit = {
    this.asScala.foreach(_.changeName(oldName, newName))
  }

  def removePlayer(player: Player): Unit = {
    val it = this.iterator()
    while (it.hasNext) {
      val table = it.next()
      table.getPlayers.remove(player)
      if (table.hasNoHumanPlayers) {
        it.remove()
        return
      }
    }
  }

  def removePlayer(playerName: String): Unit = {
    val it = this.iterator()
    GameContext.log(0, s"OnlineGameTableList.removePlayer $playerName is leaving.")
    while (it.hasNext) {
      val table = it.next()
      val pit = table.getPlayers.iterator()
      while (pit.hasNext) {
        val p = pit.next()
        if (p.getName.equals(playerName)) {
          pit.remove()
          // Break from inner loop
        }
      }
      if (table.hasNoHumanPlayers) {
        it.remove()
        return
      }
    }
  }

  def getTableReadyToPlay(playerName: String): OnlineGameTable = {
    this.asScala.find(table => table.isReadyToPlay && table.hasPlayer(playerName)).orNull
  }

  def join(tableName: String, newPlayer: Player): Unit = {
    this.asScala.find(_.getName.equals(tableName)).foreach(_.addPlayer(newPlayer))
  }

  override def toString: String = synchronized {
    val bldr = new StringBuilder("Tables:")
    this.asScala.foreach(bldr.append)
    bldr.toString()
  }
}
