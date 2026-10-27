/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.online

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.online.{GameCommand, OnlineChangeListener}
import com.barrybecker4.game.common.online.server.IServerConnection
import com.barrybecker4.game.common.player.PlayerAction
import com.barrybecker4.game.multiplayer.common.{MultiGameController, MultiGamePlayer}

/**
 * On the server, all players are surrogates except for the robot players.
 * On the client, all players are surrogates except for the human player that is controlling that client.
 *
 * @author Barry Becker
 */
class SurrogateMultiPlayer(player: MultiGamePlayer, connection: IServerConnection)
  extends MultiGamePlayer(player.getName, player.getColor, player.isHuman)
  with OnlineChangeListener {

  /** wait about 4 seconds for the player to move before timing out. */
  private val TIMEOUT_DURATION = 6000

  connection.addOnlineChangeListener(this)

  /**
   * Update ourselves based on what was broadcast to or from the server.
   */
  override def handleServerUpdate(cmd: GameCommand): Boolean = synchronized {
    if (cmd.getName == GameCommand.Name.DO_ACTION) {
      val action = cmd.getArgument.asInstanceOf[PlayerAction]
      if (action.getPlayerName == player.getName) {
        GameContext.log(0, "Setting surrogate(" + player.getName
          + ") action=" + action + " on " + this + ",  Thread=" + Thread.currentThread.getName)
        player.synchronized {
          player.setAction(action)
          player.notifyAll() // unblock the wait below
        }
        return true
      }
    }
    false
  }

  override def setAction(action: PlayerAction): Unit = {
    throw new IllegalStateException("must not set action directly on a surrogate")
  }

  /**
   * Blocks until the action has been received.
   * Wait gives other threads time to execute until we receive a notify and can continue.
   * @param controller game controller
   * @return an action for this player. Block until the real player, for which we are a surrogate,
   *    has played and we have an action to return.
   */
  override def getAction(controller: MultiGameController[?, ?]): PlayerAction = {
    try {
      val t1 = System.currentTimeMillis()
      System.out.println(player.getName + " now waiting for surrogate action on "
        + this + ",  Thread=" + Thread.currentThread.getName)

      var action: PlayerAction = null
      player.synchronized {
        while (action == null) {
          player.wait(TIMEOUT_DURATION)
          if ((System.currentTimeMillis() - t1) > (TIMEOUT_DURATION - 10)) {
            System.out.println("****** TIMEOUT! Waiting for " + player.getName + " to play.")
          }
          action = player.getAction(controller)
          player.setAction(null)
        }
      }

      val time = (System.currentTimeMillis() - t1) / 1000.0f
      System.out.println("got action =" + action + " for "
        + player.getName + " after " + time + "seconds on "
        + this + ",  Thread=" + Thread.currentThread.getName)
      action

    } catch {
      case e: InterruptedException =>
        e.printStackTrace()
        null
    }
  }

  /**
   * The player that we are representing (that is actually located somewhere else)
   * @return the specific game player backed by another player of the same type somewhere else.
   */
  override def getActualPlayer: MultiGamePlayer = player

  override def isSurrogate: Boolean = true
}
