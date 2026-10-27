/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker

import com.barrybecker4.game.multiplayer.common.MultiGameOptions
import PokerOptions.*

class PokerOptions(maxNumPlayers: Int, numRobotPlayers: Int, 
                   ante: Int, maxAbsoluteRaise: Int, initialCash: Int) 
    extends MultiGameOptions(maxNumPlayers, numRobotPlayers) {

  private var ante_ : Int = ante
  private var maxAbsoluteRaise_ : Int = maxAbsoluteRaise
  private var initialChips_ : Int = initialCash

  def this() = this(0, 0, DEFAULT_ANTE, DEFAULT_MAX_ABS_RAISE, DEFAULT_INITIAL_CASH)

  override def testValidity(): String = {
    val superMsgs = super.testValidity()
    var msgs = if (superMsgs != null) superMsgs else ""
    
    if (getAnte > getMaxAbsoluteRaise) {
      msgs += "The ante cannot be larger than the maximum raise amount\n"
    }
    if (getAnte > (getInitialCash >> 1)) {
      msgs += "The initial cash for a player cannot be less than twice the ante."
    }
    if (getInitialCash < getMaxAbsoluteRaise) {
      msgs += "The initial cash for a player cannot be less than the maximum raise amount."
    }

    if (msgs.length > 0) msgs else null
  }

  def getAnte: Int = ante_

  def setAnte(ante: Int): Unit = 
    this.ante_ = ante

  def getMaxAbsoluteRaise: Int = maxAbsoluteRaise_

  def setMaxAbsoluteRaise(maxAbsoluteRaise: Int): Unit = 
    this.maxAbsoluteRaise_ = maxAbsoluteRaise

  def getInitialCash: Int = initialChips_

  def setInitialChips(initialChips: Int): Unit = 
    this.initialChips_ = initialChips
}

object PokerOptions {
  val DEFAULT_ANTE = 2
  val DEFAULT_MAX_ABS_RAISE = 50
  val DEFAULT_INITIAL_CASH = 100
}
