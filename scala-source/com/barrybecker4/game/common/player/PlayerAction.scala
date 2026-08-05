/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.player

import java.io.Serializable

/**
 * This constitutes a move by a player in a game.
 * It is what will be serialized and sent between client and server to
 * communicate the player's action (whether robot or human).
 *
 * @author Barry Becker
 */
abstract class PlayerAction(private val playerName_ : String) extends Serializable {

  def getPlayerName: String = playerName_
}
