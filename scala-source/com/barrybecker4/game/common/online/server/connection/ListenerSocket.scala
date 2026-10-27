// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.online.server.connection

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.online.{GameCommand, OnlineChangeListener}

import java.io.{IOException, ObjectInputStream, ObjectOutputStream}
import java.net.{ConnectException, Socket, UnknownHostException}

/**
 * A socket allowing communication from the client to the GameServer.
 * We pass data using object serialization over the input and output streams.
 *
 * @author Barry Becker
 */
class ListenerSocket {

  private var oStream: ObjectOutputStream = null
  private var connected = false

  def start(host: String, port: Int, changeListeners: java.util.List[OnlineChangeListener]): Unit = {
    try {
      connected = false
      makeConnection(host, port, changeListeners)
      connected = true
      GameContext.log(0, "connected.")
    } catch {
      case e: ConnectException =>
        connected = false
        GameContext.log(0, "failed to get connection. " +
          "Probably because the server is not running or is inaccessable. " +
          "Playing a local game instead. " + e.getMessage)
      case e: UnknownHostException =>
        exceptionOccurred("Unknown host: " + host, e)
      case e: IOException =>
        exceptionOccurred("No I/O", e)
      case e: SecurityException =>
        exceptionOccurred("Failed to createListenSocket. \n" +
          "You don't have permission to open a socket to " + host +
          " in the current context." + e.getMessage, e)
    }
  }

  private def makeConnection(host: String, port: Int, changeListeners: java.util.List[OnlineChangeListener]): Unit = {
    GameContext.log(1, "Attempting to connect to Server=" + host + " port=" + port)
    val socket = new Socket(host, port)
    oStream = new ObjectOutputStream(socket.getOutputStream)
    val iStream = new ObjectInputStream(socket.getInputStream)

    val worker = new UpdateWorker(iStream, changeListeners)
    new Thread(worker).start()
  }

  def isConnected: Boolean = connected

  def sendCommand(cmd: GameCommand): Unit = {
    try {
      assert(oStream != null && cmd != null, "No socket: oStream=" + oStream + " cmd=" + cmd)
      oStream.writeObject(cmd)
      oStream.flush()
    } catch {
      case e: IOException =>
        exceptionOccurred("Send failed.", e)
    }
  }

  private def exceptionOccurred(msg: String, t: Throwable): Unit = {
    connected = false
    GameContext.log(0, msg)
    t.printStackTrace()
  }
}
