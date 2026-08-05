/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online.server.connection

import com.barrybecker4.game.common.online.{GameCommand, OnlineChangeListener, OnlineGameTable}
import com.barrybecker4.game.common.online.server.IServerConnection
import com.barrybecker4.game.common.player.{Player, PlayerAction}

import java.util.concurrent.CopyOnWriteArrayList
import scala.jdk.CollectionConverters._

/**
 * Opens a socket to the Game server from the client so we can talk to it.
 * We pass data using object serialization over the input and output streams.
 *
 * @author Barry Becker
 */
class ServerConnection(port: Int) extends IServerConnection {

  /**
   * Hardcoded for now, but should be configurable.
   * localhost "127.0.0.1";
   * or maybe "192.168.1.100";
   */
  private val DEFAULT_HOST = "192.168.1.66"

  private val socket = new ListenerSocket()

  /** a list of things that want to hear about broadcasts from the server about changed game state. */
  private val changeListeners_ = new CopyOnWriteArrayList[OnlineChangeListener]()

  socket.start(DEFAULT_HOST, port, changeListeners_)

  override def isConnected: Boolean =
    socket.isConnected

  override def sendCommand(cmd: GameCommand): Unit = {
    socket.sendCommand(cmd)
  }

  override def addOnlineChangeListener(listener: OnlineChangeListener): Unit = {
    changeListeners_.add(listener)
  }

  def removeOnlineChangeListener(listener: OnlineChangeListener): Unit = {
    changeListeners_.remove(listener)
  }

  /**
   * Request an initial update when we enter the room with the game tables.
   */
  override def enterRoom(): Unit = {
    sendCommand(GameCommand(GameCommand.Name.ENTER_ROOM, ""))
  }

  /**
   * Tell the server to add another game table to the list that is available.
   * @param newTable  to add.
   */
  override def addGameTable(newTable: OnlineGameTable): Unit = {
    sendCommand(GameCommand(GameCommand.Name.ADD_TABLE, newTable))
  }

  override def nameChanged(oldName: String, newName: String): Unit = {
    val changer = oldName + GameCommand.CHANGE_TO + newName
    sendCommand(GameCommand(GameCommand.Name.CHANGE_NAME, changer))
  }

  /**
   * Tell the server to add player p to this table.
   * The server will look at the most recently added player to this table to determine who was added.
   */
  override def joinTable(p: Player, table: OnlineGameTable): Unit = {
    table.addPlayer(p)
    sendCommand(GameCommand(GameCommand.Name.JOIN_TABLE, table))
  }

  override def leaveRoom(playerName: String): Unit = {
    sendCommand(GameCommand(GameCommand.Name.LEAVE_ROOM, playerName))
  }

  override def playerActionPerformed(action: PlayerAction): Unit = {
    sendCommand(GameCommand(GameCommand.Name.DO_ACTION, action))
  }
}
