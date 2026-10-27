// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.online.server

import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.online.GameCommand
import com.barrybecker4.ui.components.Appendable

import java.io.{IOException, ObjectInputStream, ObjectOutputStream}
import java.net.Socket
import scala.jdk.CollectionConverters._

/**
 * A client worker is created for each client player connection to this server.
 * It runs on the server and sends commands to its particular client.
 */
class ClientWorker(
  clientConnection_ : Socket,
  text: Appendable,
  cmdProcessor: ServerCommandProcessor,
  clientConnections: java.util.List[ClientWorker]
) extends Runnable {

  private var oStream_ : ObjectOutputStream = null
  private var stopped = false

  def stop(): Unit = {
    stopped = true
  }

  override def run(): Unit = {
    var iStream: ObjectInputStream = null
    try {
      iStream = new ObjectInputStream(clientConnection_.getInputStream)
      oStream_ = new ObjectOutputStream(clientConnection_.getOutputStream)
    } catch {
      case e: IOException =>
        GameContext.log(0, "in or out stream creation failed.")
        e.printStackTrace()
        throw new RuntimeException(e)
    }

    try {
      var cmd = GameCommand(GameCommand.Name.UPDATE_TABLES, cmdProcessor.getTables)
      update(cmd)

      while (!stopped) {
        cmd = iStream.readObject().asInstanceOf[GameCommand]
        processCommand(cmd)
        ThreadUtil.sleep(100)
      }
    } catch {
      case e: ClassNotFoundException =>
        e.printStackTrace()
      case e: IOException =>
        GameContext.log(0, "Read failed.")
        e.printStackTrace()
    } finally {
      try {
        if (oStream_ != null) {
          oStream_.close()
        }
      } catch {
        case _: IOException =>
      }
      try {
        iStream.close()
      } catch {
        case _: IOException =>
      }
      try {
        clientConnection_.close()
      } catch {
        case _: IOException =>
      }
    }

    GameContext.log(1, "Connection closed removing thread")
    clientConnections.remove(this)
  }

  /**
   * We got a change to the tables, update internal structure and broadcast new list.
   * @param cmd the command to process.
   */
  private def processCommand(cmd: GameCommand): Unit = {
    val responses = cmdProcessor.processCommand(cmd)

    responses.asScala.foreach { response =>
      clientConnections.asScala.foreach { worker =>
        GameContext.log(0, "sending resonse to client: " + response)
        worker.update(response)
        ThreadUtil.sleep(100)
      }
    }

    if (text == null) {
      GameContext.log(0, cmd.toString)
    } else {
      text.append(cmd.toString + '\n')
    }
  }

  /**
   * Broadcast the current list of tables to all the online clients.
   * Must reset the stream first, otherwise tables_ will always be the same as first sent.
   */
  def update(response: GameCommand): Unit = synchronized {
    oStream_.reset()
    oStream_.writeObject(response)
    oStream_.flush()
  }
}
