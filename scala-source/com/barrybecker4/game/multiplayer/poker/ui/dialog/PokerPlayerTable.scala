/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.dialog

import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.multiplayer.common.ui.PlayerTable
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer

class PokerPlayerTable(players: PlayerList) 
    extends PlayerTable(players, Array[String]("Name", "Type", "Color")) {
  import PokerPlayerTable.*

  def getRowObject(player: Player): Array[Object] = {
    val d = Array.ofDim[Object](getNumColumns)
    d(NAME_INDEX) = player.getName
    d(TYPE_INDEX) = if (player.isHuman) TYPE_HUMAN else TYPE_ROBOT
    d(COLOR_INDEX) = player.getColor
    d
  }

  def createPlayerForRow(row: Int): Player = {
    val name = getTable.getValueAt(row, NAME_INDEX).toString
    val playerType = getTable.getValueAt(row, TYPE_INDEX).asInstanceOf[String]
    val color = getTable.getValueAt(row, COLOR_INDEX).asInstanceOf[java.awt.Color]
    val isHuman = playerType == TYPE_HUMAN
    PokerPlayer.createPokerPlayer(name, 100, color, isHuman)
  }
  
  override def createPlayer(): Player = PokerPlayer.createPokerPlayer("Player", 100, null, true)
  override def getPlayers: PlayerList = players
  override protected def addRow(row: Any): Unit = {}
}

object PokerPlayerTable {
  private val NAME_INDEX = 0
  private val TYPE_INDEX = 1
  private val COLOR_INDEX = 2
  private val TYPE_HUMAN = "Human"
  private val TYPE_ROBOT = "Robot"
}
