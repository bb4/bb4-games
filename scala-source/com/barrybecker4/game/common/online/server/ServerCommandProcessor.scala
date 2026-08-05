/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online.server

import com.barrybecker4.game.common.{GameContext, GameController}
import com.barrybecker4.game.common.online.{GameCommand, OnlineGameTable, OnlineGameTableList}
import com.barrybecker4.game.common.player.{Player, PlayerAction, PlayerList}
import com.barrybecker4.game.common.plugin.{GamePlugin, PluginManager}
import com.barrybecker4.game.common.ui.panel.GamePanel
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.multiplayer.common.MultiGameController

import java.util.{LinkedList, List => JList, Objects}
import scala.jdk.CollectionConverters._

/**
 * Handles the processing of all commands send to the online game server from human players.
 * All the human players will be surrogates but the robots are here and the server needs to
 * issue their actions to their corresponding surrogates on the clients.
 *
 * @author Barry Becker
 */
class ServerCommandProcessor private (
  private val controller: GameController[?, ?],
  private val tableManager: GameTableManager
) {

  def getTables: OnlineGameTableList =
    tableManager.getTables

  def getPort: Int =
    controller.getServerPort

  /**
   * Update our internal game table list, or server controller given the cmd from the client.
   * @param cmd to process. The command that the player has issued.
   * @return the response command(s) to send to all the clients.
   */
  def processCommand(cmd: GameCommand): JList[GameCommand] = {
    val responses = new LinkedList[GameCommand]()
    val useUpdateTable = applyCommand(cmd, responses)

    if (useUpdateTable) {
      val response = GameCommand(GameCommand.Name.UPDATE_TABLES, getTables)
      responses.add(0, response) // add as first command in response.
    }

    responses
  }

  private def applyCommand(cmd: GameCommand, responses: LinkedList[GameCommand]): Boolean =
    cmd.getName match {
      case GameCommand.Name.ENTER_ROOM =>
        handleEnterRoom()
        true
      case GameCommand.Name.LEAVE_ROOM =>
        handleLeaveRoom(cmd)
        true
      case GameCommand.Name.ADD_TABLE =>
        tableManager.addTable(cmd.getArgument.asInstanceOf[OnlineGameTable])
        true
      case GameCommand.Name.JOIN_TABLE =>
        tableManager.joinTable(cmd.getArgument.asInstanceOf[OnlineGameTable])
        true
      case GameCommand.Name.CHANGE_NAME =>
        handleChangeName(cmd)
        true
      case GameCommand.Name.UPDATE_TABLES =>
        true
      case GameCommand.Name.CHAT_MESSAGE =>
        handleChatMessage(cmd, responses)
        false
      case GameCommand.Name.START_GAME =>
        handleStartGame(cmd)
        false
      case GameCommand.Name.DO_ACTION =>
        doPlayerAction(cmd, responses)
        false
    }

  private def handleEnterRoom(): Unit = {
    GameContext.log(2, "Entering room.")
  }

  private def handleLeaveRoom(cmd: GameCommand): Unit = {
    GameContext.log(0, "Player " + cmd.getArgument + " is now leaving the room.")
    tableManager.removePlayer(cmd.getArgument.asInstanceOf[String])
  }

  private def handleChangeName(cmd: GameCommand): Unit = {
    val names = cmd.getArgument.asInstanceOf[String].split(GameCommand.CHANGE_TO)
    if (names.length > 1) {
      tableManager.changeName(names(0), names(1))
    }
  }

  private def handleChatMessage(cmd: GameCommand, responses: LinkedList[GameCommand]): Unit = {
    GameContext.log(2, "chat message=" + cmd.getArgument)
    responses.add(cmd)
  }

  private def handleStartGame(cmd: GameCommand): Unit = {
    val tableToStart = cmd.getArgument.asInstanceOf[OnlineGameTable]
    startGame(tableToStart)
    tableManager.removeTable(tableToStart)
  }

  /**
   * One of the client players has acted. We need to apply this to the server controller
   * and then broadcast out the same command so the surrogate(s) on the client can be updated.
   * When a robot (on the server) moves, then that action is broadcast to the clients so
   * the surrogates on the clients can be updated.
   * @param cmd the client players command/action
   * @param responses at the least this will be the players action that we received, but it may contain robot
   *       actions for the robots on the server that play immediately after the player.
   */
  private def doPlayerAction(cmd: GameCommand, responses: LinkedList[GameCommand]): Unit = {
    val action = cmd.getArgument.asInstanceOf[PlayerAction]
    GameContext.log(0, "ServerCmdProc: doPlayerAction (" + action + "). Surrogates to handle")
    controller.handlePlayerAction(action)

    responses.add(cmd)

    controller match {
      case multi: MultiGameController[?, ?] =>
        multi.getRecentRobotActions.asScala.foreach { act =>
          val robotCmd = GameCommand(GameCommand.Name.DO_ACTION, act)
          GameContext.log(0, "adding response command for robot action on server :" + robotCmd)
          responses.add(robotCmd)
        }
      case _ =>
        GameContext.log(1, "DO_ACTION received but controller is not MultiGameController; skipping robot actions")
    }
  }

  /**
   * When all the conditions are met for starting a new game, we create a new game controller of the
   * appropriate type and start the game here on the server.
   * All human players will be surrogates and robots will be themselves.
   * @param table the table to start a game for
   */
  private def startGame(table: OnlineGameTable): Unit = {
    GameContext.log(0, "Now starting game on Server! " + table)

    // Create players from the table and start.
    val players = table.getPlayers
    assert(players.size == table.getNumPlayersNeeded)
    val newPlayers = new PlayerList()
    players.asScala.foreach { player =>
      if (player.isHuman) {
        newPlayers.add(player.createSurrogate(controller.getServerConnection))
      } else {
        newPlayers.add(player)
      }
    }
    controller.reset()
    controller.setPlayers(newPlayers)

    // if getFirstPlayer returns null, then it is not a turn based game
    val firstPlayer = controller.getPlayers.getFirstPlayer
    if (firstPlayer != null && !firstPlayer.isHuman) {
      controller.computerMovesFirst()
    }
  }
}

object ServerCommandProcessor {

  def apply(gameName: String): ServerCommandProcessor =
    new ServerCommandProcessor(createController(gameName), new GameTableManager())

  /** Package-private for tests: supply controller and table manager directly. */
  private[server] def apply(controller: GameController[?, ?], tableManager: GameTableManager): ServerCommandProcessor =
    new ServerCommandProcessor(
      Objects.requireNonNull(controller),
      Objects.requireNonNull(tableManager))

  /**
   * Factory method to create the game controller via reflection.
   * The server should not have a ui component.
   */
  private def createController(gameName: String): GameController[?, ?] = {
    val plugin: GamePlugin = PluginManager.getInstance.getPlugin(gameName)
    GameContext.loadResources(gameName)

    // for now, also create a corresponding viewer. The server should really not have knowledge of a UI component.
    // fix this by doing plugin.getModelInstance, then getting the controller from that.
    val panel: GamePanel = plugin.getPanelInstance
    panel.init(null)
    val viewer: GameBoardViewer[?, ?] = panel.getViewer

    viewer.getController
  }
}
