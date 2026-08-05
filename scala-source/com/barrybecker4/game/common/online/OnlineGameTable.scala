/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online

import com.barrybecker4.game.common.GameOptions
import com.barrybecker4.game.common.player.{Player, PlayerList}
import java.io.Serializable
import scala.jdk.CollectionConverters._

/**
 * Some number of players sitting around a virtual game table online.
 * Each table has a name, set of Players and other game specific properties.
 *
 * @author Barry Becker
 */
class OnlineGameTable(
  private var name_ : String, 
  private val owner_ : Player, 
  initialPlayers: Array[Player], 
  private val gameOptions_ : GameOptions
) extends Serializable {

  private val players_ : PlayerList = new PlayerList()
  private var newestHumanPlayer_ : Player = owner_
  players_.addAll(java.util.Arrays.asList(initialPlayers*))

  def this(name: String, initialPlayer: Player, options: GameOptions) = {
    this(name, initialPlayer, Array(initialPlayer), options)
  }

  def isReadyToPlay: Boolean = players_.size == getGameOptions.getMaxNumPlayers

  def getName: String = name_

  def setName(name: String): Unit = {
    name_ = name
  }

  def getPlayers: PlayerList = players_

  def getOwner: Player = owner_

  def getNewestHumanPlayer: Player = newestHumanPlayer_

  def getNumPlayersNeeded: Int = gameOptions_.getMaxNumPlayers

  def getGameOptions: GameOptions = gameOptions_

  def getPlayerNames: String = {
    val buf = new StringBuilder()
    if (players_.isEmpty) {
      return "-"
    }
    players_.asScala.foreach { p =>
      buf.append(p.getName).append(", ")
    }
    buf.substring(0, buf.length - 2)
  }

  def changeName(oldName: String, newName: String): Unit = {
    players_.asScala.foreach { p =>
      if (p.getName.equals(oldName)) {
        p.setName(newName)
      }
    }
  }

  def addPlayer(player: Player): Unit = {
    players_.add(player)
    if (player.isHuman)
      newestHumanPlayer_ = player
  }

  def removePlayer(player: Player): Unit = {
    players_.remove(player)
    if (player.equals(newestHumanPlayer_))
      newestHumanPlayer_ = null
  }

  def hasPlayer(playerName: String): Boolean = {
    players_.asScala.exists(_.getName.equals(playerName))
  }

  def hasNoHumanPlayers: Boolean = {
    players_.asScala.forall(!_.isHuman)
  }

  def getPlayersString: String = players_.toString

  override def toString: String = {
    val buf = new StringBuilder(20)
    buf.append("Name: ").append(name_).append(" - ")
    buf.append("Players:").append(getPlayersString)
    buf.toString()
  }

  override def equals(o: Any): Boolean = {
    if (this == o) return true
    if (!o.isInstanceOf[OnlineGameTable]) return false

    val that = o.asInstanceOf[OnlineGameTable]

    if (name_ != null && !name_.equals(that.name_) || name_ == null && that.name_ != null) return false
    if (newestHumanPlayer_ != null && !newestHumanPlayer_.equals(that.newestHumanPlayer_) ||
        newestHumanPlayer_ == null && that.newestHumanPlayer_ != null) return false
    if (owner_ != null && !owner_.equals(that.owner_) || owner_ == null && that.owner_ != null) return false
    if (players_ != null && !players_.equals(that.players_) || players_ == null && that.players_ != null) return false

    true
  }

  override def hashCode(): Int = {
    var result = if (name_ != null) name_.hashCode else 0
    result = 31 * result + (if (owner_ != null) owner_.hashCode else 0)
    result = 31 * result + (if (players_ != null) players_.hashCode else 0)
    result = 31 * result + (if (newestHumanPlayer_ != null) newestHumanPlayer_.hashCode else 0)
    result
  }
}
