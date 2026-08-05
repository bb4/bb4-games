/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.player

import com.barrybecker4.game.common.online.OnlinePlayer
import com.barrybecker4.game.common.online.server.IServerConnection
import java.awt.Color
import java.io.Serializable

/**
 * Represents a player in a game (either human or computer).
 *
 * @author Barry Becker
 */
class Player(private var options: PlayerOptions, humanPlayer: Boolean) 
    extends Serializable with OnlinePlayer {

  private var pType: Int = if (humanPlayer) Player.HUMAN_PLAYER else Player.COMPUTER_PLAYER
  private var wonField: Boolean = false

  def this(name: String, color: Color, isHuman: Boolean) = {
    this(new PlayerOptions(name, color), isHuman)
  }

  def getName: String = options.getName

  def setName(name: String): Unit = {
    this.options.setName(name)
  }

  def getColor: Color = options.getColor

  def getOptions: PlayerOptions = options

  def isHuman: Boolean = pType == Player.HUMAN_PLAYER

  def setHuman(human: Boolean): Unit = {
    pType = if (human) Player.HUMAN_PLAYER else Player.COMPUTER_PLAYER
  }

  def hasWon: Boolean = wonField

  def setWon(won: Boolean): Unit = {
    wonField = won
  }

  override def isSurrogate: Boolean = false

  def createSurrogate(connection: IServerConnection): Player =
    new SurrogatePlayer(this, connection)

  override def getActualPlayer: Player = this

  override def equals(p: Any): Boolean = {
    if (p == null) {
      return false
    }
    else if (!p.isInstanceOf[Player]) {
      return false
    }
    val p1 = p.asInstanceOf[Player]
    getName.equals(p1.getName) && isHuman == p1.isHuman
  }

  override def hashCode: Int = {
    var hash = if (isHuman) 100000000 else 0
    hash += 10 * getName.hashCode
    hash
  }

  override def toString: String = {
    val sb = new StringBuilder()
    sb.append("[ *").append(getName).append("* ")
    if (!isHuman)
      sb.append(" (computer)")
    sb.append(additionalInfo()).append(" ]")
    sb.toString()
  }

  def additionalInfo(): String = ""
}

object Player {
  private val HUMAN_PLAYER: Int = 1
  private val COMPUTER_PLAYER: Int = 2
}
