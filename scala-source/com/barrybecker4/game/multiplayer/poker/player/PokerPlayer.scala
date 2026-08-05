/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.player

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer
import com.barrybecker4.game.multiplayer.poker.PokerController
import com.barrybecker4.game.multiplayer.poker.hand.{Hand, HandScore, PokerHandScorer}
import com.barrybecker4.game.multiplayer.poker.model.{PokerAction, PokerRound}
import com.barrybecker4.game.multiplayer.poker.ui.render.PokerPlayerMarker
import java.awt.Color
import java.text.MessageFormat

abstract class PokerPlayer(name: String, color: Color, isHuman: Boolean) 
    extends MultiGamePlayer(name, color, isHuman) {

  private var hand: Hand = scala.compiletime.uninitialized
  private var piece: PokerPlayerMarker = PokerPlayerMarker(this)
  private var cash: Int = if (isHuman) 100 else 100
  private var hasFolded_ : Boolean = false
  private var handScore: HandScore = scala.compiletime.uninitialized
  private var outOfGame: Boolean = false
  private var contribution: Int = 0
  var action: PokerAction = scala.compiletime.uninitialized

  def this(name: String, money: Int, color: Color, isHuman: Boolean) = {
    this(name, color, isHuman)
    this.cash = money
  }

  def getDefaultName(i: Int): String = {
    val args = Array[Object](Integer.toString(i))
    MessageFormat.format(GameContext.getLabel("POKER_DEFAULT_NAME"), args*)
  }

  def getHand: Hand = hand

  def getHandScore: HandScore = handScore

  def setHand(hand: Hand): Unit = {
    this.hand = hand
    this.handScore = PokerHandScorer().getScore(hand)
  }

  def getCash: Int = cash

  def setFold(folded: Boolean): Unit = 
    hasFolded_ = folded

  def hasFolded: Boolean = hasFolded_

  def isOutOfGame: Boolean = outOfGame || (cash <= 0)

  override def getPiece: PokerPlayerMarker = piece

  def setPiece(piece: PokerPlayerMarker): Unit = 
    this.piece = piece

  def contributeToPot(round: PokerRound, amount: Int): Unit = {
    if (cash < amount) {
      outOfGame = true
      hasFolded_ = true
      return
    }
    if (amount > 0) {
      cash -= amount
      contribution += amount
      round.addToPot(amount)
    }
  }

  def getCallAmount(pc: PokerController): Int = {
    val currentMaxContrib = pc.getCurrentMaxContribution
    currentMaxContrib - getContribution
  }

  def resetPlayerForNewRound(): Unit = {
    hasFolded_ = false
    contribution = 0
    outOfGame = false
  }

  def getContribution: Int = contribution

  def claimPot(potAmount: Int): Unit = 
    cash += potAmount

  override def additionalInfo(): String = {
    val sb = StringBuilder()
    if (getHand != null) {
      sb.append(" Hand: ").append(getHand)
    }
    sb.append(" Money: ").append(cash)
    sb.toString()
  }
}

object PokerPlayer {
  def createPokerPlayer(name: String, money: Int, color: Color, isHuman: Boolean): PokerPlayer = {
    if (isHuman)
      PokerHumanPlayer(name, money, color)
    else
      PokerRobotPlayer.getSequencedRobotPlayer(name, money, color)
  }
}
