/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online.server

import com.barrybecker4.common.app.CommandLineOptions
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.ui.components.Appendable

import java.io.IOException
import java.net.{BindException, ServerSocket}
import java.util.{LinkedList, List => JList}
import scala.jdk.CollectionConverters._

/**
 * The abstract server for online games.
 * This can be run from within a ui or from a command line.
 * If running from the command line use:
 *       java OnlineGameServer -game &lt;gameName&gt;
 * e.g.  java OnlineGameServer -game poker
 * If running from the UI, see
 *   OnlineGameServerFrame
 * Manages the tables for the game room.
 * Has a GameController for each table.
 * Consider using executor framework to manage threads.
 *
 * @author Barry Becker
 */
class OnlineGameServer(gameType: String, text: Appendable) {

  private var server: ServerSocket = null
  private var stopped = false

  /** keep a list of the threads that we have for each client connection.  */
  private val clientConnections: JList[ClientWorker] = new LinkedList[ClientWorker]()

  openListenSocket(gameType)

  def shutDown(): Unit = {
    stopped = true
    clientConnections.asScala.foreach(_.stop())
    try {
      server.close()
    } catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }

  /**
   * open a server socket to listen on our assigned port for
   * requests from clients. Updates will be broadcast on this socket.
   * Maintain a list of clientConnections corresponding to the players
   * that we need to broadcast to when something changes.
   */
  private def openListenSocket(gameType: String): Unit = {
    val cmdProcessor = ServerCommandProcessor(gameType)
    val port = cmdProcessor.getPort

    server = createServerSocket(port)
    acceptClientConnections(cmdProcessor)
  }

  /**
   * Allows the server to listen for client messages.
   * @param port port to listen on.
   * @return new server socket
   */
  private def createServerSocket(port: Int): ServerSocket = {
    try {
      new ServerSocket(port)
    } catch {
      case e: BindException =>
        GameContext.log(0, "Address already in use! " +
          "Perhaps there is already another game server sunning on this port:" + port)
        throw new RuntimeException(e)
      case e: IOException =>
        GameContext.log(0, "Could not listen on port " + port)
        e.printStackTrace()
        throw new RuntimeException(e)
    }
  }

  private def acceptClientConnections(cmdProcessor: ServerCommandProcessor): Unit = {
    while (!stopped) {
      try {
        // accept new connections from players wanting to join.
        val worker = new ClientWorker(server.accept(), text, cmdProcessor, clientConnections)
        clientConnections.add(worker)
        new Thread(worker).start()
      } catch {
        case e: IOException =>
          GameContext.log(0, "Accept failed on port " + server.getLocalPort)
          e.printStackTrace()
          return
      }
    }
  }
}

object OnlineGameServer {

  val GAME_OPTION: String = "game" // NON-NLS

  /**
   * @param options command line options to verify
   * @return true if valid.
   */
  def verifyCmdLineOptions(options: CommandLineOptions): Boolean = {
    if (options.contains("help") || !options.contains(GAME_OPTION)) {
      GameContext.log(0, "Usage: -game <game name>\n")
      return false
    }
    if (options.getValueForOption(GAME_OPTION) == null) {
      GameContext.log(0, "You must specify a valid game. See plugins.xml for list of available games.")
      return false
    }
    true
  }

  /**
   * create and show the server.
   */
  def main(args: Array[String]): Unit = {
    val options = new CommandLineOptions(args)

    if (verifyCmdLineOptions(options)) {
      val gameName = options.getValueForOption(GAME_OPTION)
      new OnlineGameServer(gameName, null)
    }
  }
}
