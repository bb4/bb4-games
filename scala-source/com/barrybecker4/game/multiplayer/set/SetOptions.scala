/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set

import com.barrybecker4.game.multiplayer.common.MultiGameOptions
import SetOptions.*

class SetOptions(maxNumPlayers: Int, numRobotPlayers: Int, initialNumCards: Int) 
    extends MultiGameOptions(maxNumPlayers, numRobotPlayers) {

  private var initialNumCardsShown_ = initialNumCards

  def this() = {
    this(DEFAULT_SET_PLAYER_LIMIT, 0, INITIAL_NUM_CARDS_SHOWN)
    setMaxNumPlayers(DEFAULT_SET_PLAYER_LIMIT)
  }

  def getInitialNumCardsShown: Int = initialNumCardsShown_

  def setInitialNumCardsShown(initialNumCardsShown: Int): Unit = 
    this.initialNumCardsShown_ = initialNumCardsShown
}

object SetOptions {
  private val INITIAL_NUM_CARDS_SHOWN = 12
  private val DEFAULT_SET_PLAYER_LIMIT = 5
}
