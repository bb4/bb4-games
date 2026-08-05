/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic

import com.barrybecker4.game.common.player.PlayerAction
import java.util.{List as JList}

class GalacticAction(playerName: String, private val orders_ : JList[Order])
  extends PlayerAction(playerName):

  def getOrders: JList[Order] = orders_
