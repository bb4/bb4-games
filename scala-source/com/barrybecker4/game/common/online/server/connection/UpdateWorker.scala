// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.online.server.connection

import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.online.{GameCommand, OnlineChangeListener}

import java.io.{IOException, ObjectInputStream}
import java.net.SocketException
import scala.jdk.CollectionConverters._

/**
 * A client worker is created for each client player connection to this server.
 */
class UpdateWorker(
  inputStream: ObjectInputStream,
  changeListeners: java.util.List[OnlineChangeListener]
) extends Runnable {

  private var isConnected = true

  override def run(): Unit = {
    try {
      while (isConnected) {
        try {
          processNextCommand()
        } catch {
          case e: SocketException =>
            exceptionOccurred("Read failed (probably because player closed client).", e)
          case e: IOException =>
            exceptionOccurred("Read failed.", e)
          case e: ClassNotFoundException =>
            exceptionOccurred("Class not found.", e)
        }
      }
    } finally {
      try {
        inputStream.close()
      } catch {
        case _: IOException =>
      }
      changeListeners.clear()
    }
    GameContext.log(0, "UpdateWorker terminated.")
  }

  private def processNextCommand(): Unit = {
    val cmd = inputStream.readObject().asInstanceOf[GameCommand]
    GameContext.log(0, "Client Connection: got an update from the server:" + cmd)

    var processed = false
    var count = 0

    // the command may not get processed right away if the surrogate player is not waiting for it yet.
    while (!processed && count < 10) {
      changeListeners.asScala.foreach { listener =>
        if (listener.handleServerUpdate(cmd)) {
          processed = true
        }
      }
      if (!processed) {
        GameContext.log(0, " --- Unable to process cmd=" + cmd + " yet.")
        ThreadUtil.sleep(1000)
        count += 1
      } else {
        GameContext.log(0, " --- Cmd " + cmd + " processed on client.")
      }
    }
  }

  private def exceptionOccurred(msg: String, e: Throwable): Unit = {
    GameContext.log(0, msg + e.getMessage)
    isConnected = false
  }
}
