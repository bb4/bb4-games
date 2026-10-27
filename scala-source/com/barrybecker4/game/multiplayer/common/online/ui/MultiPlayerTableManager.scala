// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.common.online.ui

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.online.{GameCommand, OnlineGameTable, OnlineGameTableList}
import com.barrybecker4.game.common.online.server.IServerConnection
import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.common.ui.dialogs.GameStartListener
import com.barrybecker4.game.multiplayer.common.{MultiGameOptions, MultiGamePlayer}
import com.barrybecker4.game.multiplayer.common.online.SurrogateMultiPlayer
import com.barrybecker4.ui.table.BasicTableModel

import javax.swing.JOptionPane
import scala.jdk.CollectionConverters._

/**
 * Manage multi-player online game tables.
 * Shows a list of the currently active "tables" to the user in a table.
 * Used only on the client to show list of active game tables.
 *
 * @author Barry Becker
 */
class MultiPlayerTableManager(
  connection: IServerConnection,
  onlineGameTablesTable: MultiPlayerOnlineGameTablesTable,
  startListener: GameStartListener
) {

  private var currentName_ : String = null
  private val connection_ = connection
  private val onlineGameTablesTable_ = onlineGameTablesTable
  private val startListener_ = startListener

  def setCurrentName(name: String): Unit = {
    assert(name != null)
    currentName_ = name
  }

  /**
   * The server has sent out a message to all the clients.
   * @param cmd the command to handle.
   */
  def handleServerUpdate(cmd: GameCommand): Boolean = {
    if (onlineGameTablesTable_ == null) {
      return false // not initialized yet.
    }

    var handled = false
    System.out.println("got an update of the multi-player table list from the server:\n" + cmd)
    cmd.getName match {
      case GameCommand.Name.UPDATE_TABLES =>
        updateTables(cmd.getArgument.asInstanceOf[OnlineGameTableList])
        handled = true
      case GameCommand.Name.START_GAME =>
        val readyTable = onlineGameTablesTable_.getSelectedTable
        startGame(readyTable)
        handled = true
      case GameCommand.Name.CHAT_MESSAGE =>
      case GameCommand.Name.DO_ACTION =>
      case _ =>
        assert(false, "Unexpected command name :" + cmd.getName)
    }
    handled
  }

  def updateTables(tableList: OnlineGameTableList): Unit = {
    onlineGameTablesTable_.removeAllRows()
    if (currentName_ == null) return

    tableList.asScala.foreach { table =>
      onlineGameTablesTable_.addRow(table, table.hasPlayer(currentName_))
    }

    val readyTable = tableList.getTableReadyToPlay(currentName_)

    if (readyTable != null) {
      JOptionPane.showMessageDialog(
        null,
        "All the players required \n(" + readyTable.getPlayersString
          + ")\n have joined this table. Play will now begin. ",
        "Ready to Start", JOptionPane.INFORMATION_MESSAGE)

      startGame(readyTable)

      val startCmd = GameCommand(GameCommand.Name.START_GAME, readyTable)
      connection_.sendCommand(startCmd)
    }
  }

  def startGame(readyTable: OnlineGameTable): Unit = {
    GameContext.log(0, "Start the game for player:" + currentName_
      + " on the client. Table=" + readyTable)

    onlineGameTablesTable_.removeRow(readyTable)
    assert(
      onlineGameTablesTable_.getNumRows == 0,
      "still have game tables even though just removed " + readyTable
        + " tables:" + onlineGameTablesTable_.toString)

    val it = readyTable.getPlayers.iterator
    val players = new PlayerList()
    while (it.hasNext) {
      val player = it.next().asInstanceOf[MultiGamePlayer]
      if (player.getName != this.currentName_) {
        GameContext.log(0, "creating surrogate for " + player.getName)
        players.add(new SurrogateMultiPlayer(player, connection_))
      } else {
        players.add(player)
        connection_.addOnlineChangeListener(new NoOpOnlineGameChangeListener(player))
      }
    }

    GameContext.log(0, "starting game with players=" + players)
    startListener_.startGame(players)
  }

  def createNewGameTable(options: MultiGameOptions): Unit = {
    val newTable =
      onlineGameTablesTable_.createOnlineTable(currentName_, options)

    connection_.addGameTable(newTable)
  }

  def joinDifferentTable(joinRow: Int): Unit = {
    System.out.println("in join different table. row=" + joinRow)
    val m = onlineGameTablesTable_.getPlayerModel

    var i = 0
    while (i < m.getRowCount) {
      val enableJoin = (i != joinRow) && !onlineGameTablesTable_.getGameTable(i).isReadyToPlay
      m.setValueAt(enableJoin, i, 0)
      i += 1
    }
    connection_.joinTable(
      onlineGameTablesTable_.createPlayerForName(currentName_),
      onlineGameTablesTable_.getGameTable(joinRow))

    onlineGameTablesTable_.getTable.removeEditor()
  }
}
