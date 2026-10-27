/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common

import com.barrybecker4.game.common.online.server.IServerConnection
import com.barrybecker4.game.common.player.{Player, PlayerAction, PlayerList}
import com.barrybecker4.game.multiplayer.common.online.SurrogateMultiPlayer
import com.barrybecker4.ui.util.ColorUtil
import java.awt.Color
import scala.util.Random

/**
 * A player in a multi-player game.
 * @author Barry Becker
 */
abstract class MultiGamePlayer(name: String, color: Color, isHuman: Boolean) 
    extends Player(name, color, isHuman) {

  def getAction(controller: MultiGameController[?, ?]): PlayerAction

  def setAction(action: PlayerAction): Unit

  def getPiece: MultiPlayerMarker = {
    assert(false, s"no piece support for ${this.getClass.getName}. Use getActualPlayer().getPiece")
    null
  }

  override def createSurrogate(connection: IServerConnection): Player =
    new SurrogateMultiPlayer(this, connection)
}

object MultiGamePlayer {
  private val SATURATION: Float = 0.8f
  private val BRIGHTNESS: Float = 0.999f

  def getNewPlayerColor(players: PlayerList): Color = {
    var uniqueEnough = false
    var candidateHue: Float = 0f

    while (!uniqueEnough) {
      candidateHue = Random.nextFloat()
      uniqueEnough = isHueUniqueEnough(candidateHue, players)
    }

    Color.getHSBColor(candidateHue, SATURATION, BRIGHTNESS)
  }

  private def isHueUniqueEnough(hue: Float, players: PlayerList): Boolean = {
    var ct = 0
    val tolerance = 1.0f / (1.0f + 1.8f * players.size)
    while (ct < players.size) {
      if (players.get(ct) == null) {
        ct += 1
      } else if (Math.abs(ColorUtil.getColorHue(players.get(ct).getColor) - hue) > tolerance) {
        ct += 1
      } else {
        return false
      }
    }
    ct == players.size
  }
}
