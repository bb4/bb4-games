/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.online.ui

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.online.OnlineGameTable
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.multiplayer.common.MultiGameOptions
import com.barrybecker4.game.multiplayer.common.online.ui.MultiPlayerOnlineGameTablesTable
import com.barrybecker4.game.multiplayer.poker.PokerOptions
import com.barrybecker4.game.multiplayer.poker.online.OnlinePokerTable
import com.barrybecker4.game.multiplayer.poker.player.PokerHumanPlayer
import com.barrybecker4.ui.table.TableButtonListener

class PokerOnlineGameTablesTable(tableButtonListener: TableButtonListener) 
    extends MultiPlayerOnlineGameTablesTable(PokerOnlineGameTablesTable.POKER_COLUMN_NAMES, tableButtonListener) {
  import PokerOnlineGameTablesTable.*

  override def getRowObject(onlineTable: OnlineGameTable, localPlayerAtTable: Boolean): Array[Object] = {
    val d = Array.ofDim[Object](getNumColumns)
    d(JOIN_INDEX) = if (localPlayerAtTable) "leave" else "join"
    d(NUM_PLAYERS_INDEX) = onlineTable.getNumPlayersNeeded.asInstanceOf[Object]
    d(PLAYER_NAMES_INDEX) = onlineTable.getPlayerNames
    val options = onlineTable.getGameOptions.asInstanceOf[PokerOptions]
    d(ANTE_INDEX) = options.getAnte.asInstanceOf[Object]
    d(MAX_RAISE_INDEX) = options.getMaxAbsoluteRaise.asInstanceOf[Object]
    d(INITIAL_CASH_INDEX) = options.getInitialCash.asInstanceOf[Object]
    d
  }

  override def createOnlineTable(ownerPlayerName: String, options: MultiGameOptions): OnlineGameTable = {
    val player = createPlayerForName(ownerPlayerName)
    OnlinePokerTable(s"Table_${System.currentTimeMillis()}", player, options)
  }

  override def createPlayerForName(playerName: String): Player = 
    PokerHumanPlayer(playerName, 100, java.awt.Color.BLUE)
}

object PokerOnlineGameTablesTable {
  private val JOIN_INDEX = 0
  private val NUM_PLAYERS_INDEX = 1
  private val PLAYER_NAMES_INDEX = 2
  private val ANTE_INDEX = 3
  private val MAX_RAISE_INDEX = 4
  private val INITIAL_CASH_INDEX = 5

  private val ANTE = GameContext.getLabel("ANTE")
  private val MAX_RAISE = GameContext.getLabel("MAX_RAISE")
  private val INITIAL_CASH = GameContext.getLabel("INITIAL_CASH")

  private val POKER_COLUMN_NAMES = Array(
    "Join", 
    "Min Players", 
    "Player Names", 
    ANTE, MAX_RAISE, INITIAL_CASH
  )
}
