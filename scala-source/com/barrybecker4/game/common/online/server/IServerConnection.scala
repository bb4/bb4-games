/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online.server

import com.barrybecker4.game.common.online.{GameCommand, OnlineChangeListener, OnlineGameTable}
import com.barrybecker4.game.common.player.{Player, PlayerAction}

/**
 * Opens a connection to the Game server from the client so we can talk to it.
 *
 * @author Barry Becker
 */
trait IServerConnection {

  def isConnected: Boolean

  def addOnlineChangeListener(listener: OnlineChangeListener): Unit

  def sendCommand(cmd: GameCommand): Unit

  def enterRoom(): Unit

  def addGameTable(newTable: OnlineGameTable): Unit

  def nameChanged(oldName: String, newName: String): Unit

  def joinTable(p: Player, table: OnlineGameTable): Unit

  def leaveRoom(playerName: String): Unit

  def playerActionPerformed(action: PlayerAction): Unit
}
