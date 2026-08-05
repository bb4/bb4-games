/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.model

import com.barrybecker4.game.common.{GameContext, Move}
import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.multiplayer.poker.hand.{Hand, HandScore, PokerHandScorer}
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer
import java.util.ArrayList
import scala.jdk.CollectionConverters.*

class PokerRound extends Move {
  private var pot_ : Int = 0

  def addToPot(amount: Int): Unit = {
    assert(amount > 0, "You must add a positive amount")
    pot_ += amount
  }

  def getPotValue: Int = pot_

  def clearPot(): Unit = 
    pot_ = 0

  def anteUp(players: PlayerList, anteAmount: Int): Unit = {
    if (getPotValue == 0) {
      val it = players.iterator()
      while (it.hasNext) {
        val p = it.next()
        val player = p.getActualPlayer.asInstanceOf[PokerPlayer]
        player.contributeToPot(this, anteAmount)
      }
    }
  }

  def getCurrentMaxContribution(players: PlayerList): Int = {
    var max = Integer.MIN_VALUE
    val it = players.iterator()
    while (it.hasNext) {
      val p = it.next()
      val player = p.getActualPlayer.asInstanceOf[PokerPlayer]
      if (player.getContribution > max) {
        max = player.getContribution
      }
    }
    max
  }

  def getAllInAmount(players: PlayerList): Int = {
    var min = Integer.MAX_VALUE
    val it = players.iterator()
    while (it.hasNext) {
      val p = it.next()
      val player = p.getActualPlayer.asInstanceOf[PokerPlayer]
      if (!player.hasFolded && ((player.getCash + player.getContribution) < min)) {
        min = player.getCash + player.getContribution
      }
    }
    min
  }

  def roundOver(players: PlayerList, playerIndex: Int): Boolean = {
    if (allButOneFolded(players)) {
      return true
    }

    val contrib = getCurrentMaxContribution(players)
    GameContext.log(0, s"in roundover check max contrib=$contrib")

    val it = players.iterator()
    while (it.hasNext) {
      val pp = it.next()
      val p = pp.getActualPlayer.asInstanceOf[PokerPlayer]
      if (!p.hasFolded) {
        assert(p.getContribution <= contrib, 
          s"contrib was supposed to be the max, but $p contradicts that.")
        if (p.getContribution != contrib) {
          return false
        }
      }
    }

    playerIndex >= getNumNonFoldedPlayers(players)
  }

  def determineWinners(players: PlayerList): java.util.List[PokerPlayer] = {
    val winners = ArrayList[PokerPlayer]()
    var first = 0

    while (players.get(first).getActualPlayer.asInstanceOf[PokerPlayer].hasFolded && first < players.size) {
      first += 1
    }
    if (players.get(first).asInstanceOf[PokerPlayer].hasFolded) {
      GameContext.log(0, "All players have folded. That was dumb. The winner will be random.")
    }

    val bestHand = players.get(first).asInstanceOf[PokerPlayer].getHand
    val scorer = PokerHandScorer()
    var bestScore = scorer.getScore(bestHand)

    for (i <- first + 1 until players.size) {
      val player = players.get(i).getActualPlayer.asInstanceOf[PokerPlayer]
      val score = scorer.getScore(player.getHand)
      if (!player.hasFolded && score.compareTo(bestScore) > 0) {
        bestScore = score
      }
    }

    for (i <- first until players.size) {
      val player = players.get(i).getActualPlayer.asInstanceOf[PokerPlayer]
      if (!player.hasFolded && scorer.getScore(player.getHand).compareTo(bestScore) == 0) {
        winners.add(player)
      }
    }

    GameContext.log(0, s"The winning players were $winners")
    winners
  }

  private def allButOneFolded(players: PlayerList): Boolean = {
    var numNotFolded = 0
    val it = players.iterator()
    while (it.hasNext) {
      val pp = it.next()
      val player = pp.getActualPlayer.asInstanceOf[PokerPlayer]
      if (!player.hasFolded) {
        numNotFolded += 1
      }
    }
    numNotFolded == 1
  }

  private def getNumNonFoldedPlayers(players: PlayerList): Int = {
    var count = 0
    val it = players.iterator()
    while (it.hasNext) {
      val p = it.next()
      val player = p.getActualPlayer.asInstanceOf[PokerPlayer]
      if (!player.isOutOfGame) {
        count += 1
      }
    }
    count
  }
}
