/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker

import com.barrybecker4.game.common.{GameOptions, MoveList}
import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.multiplayer.common.{MultiGameController, MultiGamePlayer}
import com.barrybecker4.game.multiplayer.poker.model.{Dealer, PokerAction, PokerRound, PokerTable}
import com.barrybecker4.game.multiplayer.poker.player.{PokerPlayer, PokerRobotPlayer}
import com.barrybecker4.game.multiplayer.poker.ui.PokerGameViewer

class PokerController extends MultiGameController[PokerRound, PokerTable](PokerController.DEFAULT_NUM_ROWS, PokerController.DEFAULT_NUM_COLS) {
  import PokerController.*

  private var round: PokerRound = scala.compiletime.uninitialized

  override def getBoard: PokerTable = super.getBoard.asInstanceOf[PokerTable]

  override def reset(): Unit = {
    super.reset()
    initializeData()
    round = getBoard.getRound
    round.anteUp(getPlayers, getOptions.asInstanceOf[PokerOptions].getAnte)
  }

  override def createOptions: GameOptions = PokerOptions()

  override def getMoveList: MoveList[PokerRound] = getBoard.getMoveList

  override def createTable(nrows: Int, ncols: Int): PokerTable = PokerTable(nrows, ncols)

  def getRound: PokerRound = round

  override def initPlayers(): Unit = {
    if (getPlayers == null) {
      val players = PlayerList()

      players.add(PokerPlayer.createPokerPlayer("Player 1", 100, 
        MultiGamePlayer.getNewPlayerColor(players), true))

      players.add(PokerPlayer.createPokerPlayer("Player 2", 100, 
        MultiGamePlayer.getNewPlayerColor(players), false))
      players.get(1).setName(players.get(1).getName + '(' + 
        players.get(1).asInstanceOf[PokerRobotPlayer].getType + ')')
      setPlayers(players)
    }

    deal()
    currentPlayerIndex = 0

    getBoard.initPlayers(getPlayers)
  }

  def getCurrentMaxContribution: Int = round.getCurrentMaxContribution(getPlayers)

  def getAllInAmount: Int = round.getAllInAmount(getPlayers)

  def getPotValue: Int = round.getPotValue

  def clearPot(): Unit = round.clearPot()

  override def getServerPort: Int = POKER_SERVER_PORT

  override def isDone: Boolean = {
    if (getLastMove == null) return false
    
    var numPlayersStillPlaying = 0
    val it = getPlayers.iterator()
    while (it.hasNext) {
      val p = it.next()
      val player = p.getActualPlayer.asInstanceOf[PokerPlayer]
      if (!player.isOutOfGame) {
        numPlayersStillPlaying += 1
      }
    }
    numPlayersStillPlaying == 1
  }

  override def doAdvanceToNextPlayer(): Unit = {
    val pviewer = getViewer.asInstanceOf[PokerGameViewer]
    pviewer.refresh()
    advanceToNextPlayerIndex()

    if (round.roundOver(getPlayers, playIndex)) {
      makeMove(round)
      pviewer.refresh()

      doRoundOverBookKeeping(pviewer)
    }

    if (isDone) {
      pviewer.sendGameChangedEvent(null)
    }

    if (!getCurrentPlayer.isHuman && !isDone) {
      pviewer.doComputerMove(getCurrentPlayer)
    }

    pviewer.sendGameChangedEvent(null)
  }

  private def doRoundOverBookKeeping(pviewer: PokerGameViewer): Unit = {
    val winners = determineWinners()
    val numWinners = winners.size
    val winnings = this.getPotValue / numWinners

    for (i <- 0 until winners.size) {
      winners.get(i).claimPot(winnings)
    }
    this.clearPot()
    pviewer.showRoundOver(winners, winnings)

    if (!isDone) {
      deal()
      round.anteUp(getPlayers, getOptions.asInstanceOf[PokerOptions].getAnte)
      
      var done = false
      while (!done) {
        startingPlayerIndex_ = (startingPlayerIndex_ + 1) % this.getPlayers.getNumPlayers
        done = !getPlayer(startingPlayerIndex_).asInstanceOf[PokerPlayer].isOutOfGame
      }

      currentPlayerIndex = startingPlayerIndex_
      playIndex = 0
    }
  }

  override def determineWinners(): java.util.List[PokerPlayer] = 
    round.determineWinners(getPlayers)

  override def advanceToNextPlayerIndex(): Int = {
    playIndex += 1
    currentPlayerIndex = (currentPlayerIndex + 1) % getPlayers.size
    while (getPlayer(currentPlayerIndex).getActualPlayer.asInstanceOf[PokerPlayer].hasFolded) {
      currentPlayerIndex = (currentPlayerIndex + 1) % getPlayers.size
    }
    currentPlayerIndex
  }

  override def setPlayers(players: PlayerList): Unit = {
    super.setPlayers(players)
    deal()
  }

  private def deal(): Unit = 
    Dealer().dealCardsToPlayers(getPlayers, 5)
}

object PokerController {
  private val DEFAULT_NUM_ROWS = 32
  private val DEFAULT_NUM_COLS = 32
  private val POKER_SERVER_PORT = 4443
}
