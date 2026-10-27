/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set

import com.barrybecker4.game.common.{GameOptions, Move}
import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.multiplayer.common.{MultiGameController, MultiGamePlayer}
import java.util.{Collections, List}

class SetController extends MultiGameController[Move, SetBoard] {
  import SetController.*

  private var currentPlayerIndex_ = NO_PLAYER_SELECTED

  initializeData()

  override def getBoard: SetBoard = super.getBoard.asInstanceOf[SetBoard]

  override def createTable(rows: Int, cols: Int): SetBoard = 
    SetBoard(getOptions.asInstanceOf[SetOptions].getInitialNumCardsShown)

  override def reset(): Unit = initializeData()

  override def initializeData(): Unit = {
    initPlayers()
    gameChanged()
  }

  override def advanceToNextPlayerIndex(): Int = {
    assert(false, "not used in set game")
    0
  }

  override def createOptions: GameOptions = SetOptions()

  override def isOnlinePlayAvailable: Boolean = false

  def addCards(num: Int): Unit = {
    getBoard.addCards(num)
    gameChanged()
  }

  def removeCard(): Unit = {
    getBoard.removeCard()
    gameChanged()
  }

  def canRemoveCards: Boolean = getBoard.canRemoveCards

  def hasCardsToAdd: Boolean = getBoard.hasCardsToAdd

  def removeCards(cards: List[Card]): Unit = {
    getBoard.removeCards(cards)
    gameChanged()
  }

  def setCurrentPlayer(player: Player): Unit = {
    if (player == null) {
      currentPlayerIndex_ = NO_PLAYER_SELECTED
    } else {
      val idx = (0 until getPlayers.size).indexWhere(i => player == getPlayers.get(i))
      if (idx >= 0) currentPlayerIndex_ = idx
    }
  }

  def getSetsOnBoard: List[Card] = getBoard.getSetsOnBoard

  override def initPlayers(): Unit = {
    if (getPlayers == null) {
      val players = PlayerList()
      players.add(SetPlayer.createSetPlayer("Player 1", MultiGamePlayer.getNewPlayerColor(players), true))
      players.add(SetPlayer.createSetPlayer("Player 2", MultiGamePlayer.getNewPlayerColor(players), false))
      players.get(1).setName(players.get(1).getName + '(' + players.get(1).asInstanceOf[SetRobotPlayer].getRobotType + ')')
      setPlayers(players)
    }
  }

  override def getCurrentPlayer: MultiGamePlayer = {
    if (currentPlayerIndex_ != NO_PLAYER_SELECTED) {
      getPlayers.get(currentPlayerIndex_).asInstanceOf[MultiGamePlayer]
    } else {
      null
    }
  }

  def gameChanged(): Unit = {
    if (getViewer != null) {
      getViewer.asInstanceOf[GameBoardViewer[Move, ?]].sendGameChangedEvent(null.asInstanceOf[Move])

    }
  }

  override def computerMovesFirst(): Unit = 
    assert(false, "No one moves first in set.")

  override def isDone: Boolean = getBoard.isDone

  override def determineWinners(): java.util.List[? <: MultiGamePlayer] = {
    val players = getPlayers
    var first = 0

    var winner = players.get(first).asInstanceOf[SetPlayer]
    var mostSets = winner.getNumSetsFound

    for (i <- first + 1 until players.size) {
      val p = players.get(i).asInstanceOf[SetPlayer]
      if (p.getNumSetsFound > mostSets) {
        mostSets = p.getNumSetsFound
        winner = p
      }
    }

    Collections.singletonList(winner)
  }
}

object SetController {
  private val NO_PLAYER_SELECTED = -1
}
