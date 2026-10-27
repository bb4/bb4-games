/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.online.ui

import com.barrybecker4.game.common.online.OnlineGameTable
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.multiplayer.common.MultiGameOptions
import com.barrybecker4.game.multiplayer.common.online.ui.MultiPlayerOnlineGameTablesTable
import com.barrybecker4.game.multiplayer.trivial.online.OnlineTrivialTable
import com.barrybecker4.game.multiplayer.trivial.player.TrivialHumanPlayer
import com.barrybecker4.ui.table.TableButtonListener

/**
 * Show Trivial specific game options in the table row.
 *
 * @author Barry Becker
 */
class TrivialOnlineGameTablesTable(tableButtonListener: TableButtonListener)
  extends MultiPlayerOnlineGameTablesTable(TrivialOnlineGameTablesTable.TRIVIAL_COLUMN_NAMES, tableButtonListener):

  override def getRowObject(onlineTable: OnlineGameTable, localPlayerAtTable: Boolean): Array[AnyRef] =
    val d = new Array[AnyRef](getNumColumns)

    d(MultiPlayerOnlineGameTablesTable.JOIN_INDEX) = if localPlayerAtTable then "Leave" else "Join"
    d(MultiPlayerOnlineGameTablesTable.NUM_PLAYERS_INDEX) = Integer.valueOf(onlineTable.getNumPlayersNeeded)
    d(MultiPlayerOnlineGameTablesTable.PLAYER_NAMES_INDEX) = onlineTable.getPlayerNames
    d

  override def createOnlineTable(ownerPlayerName: String, options: MultiGameOptions): OnlineGameTable =
    val player = createPlayerForName(ownerPlayerName)
    new OnlineTrivialTable(MultiPlayerOnlineGameTablesTable.getUniqueName, player, options)

  override def createPlayerForName(playerName: String): Player =
    new TrivialHumanPlayer(playerName, MultiPlayerOnlineGameTablesTable.getRandomColor)

object TrivialOnlineGameTablesTable:
  private val TRIVIAL_COLUMN_NAMES = Array[String](
    MultiPlayerOnlineGameTablesTable.JOIN, 
    MultiPlayerOnlineGameTablesTable.MIN_NUM_PLAYERS, 
    MultiPlayerOnlineGameTablesTable.PLAYER_NAMES
  )
