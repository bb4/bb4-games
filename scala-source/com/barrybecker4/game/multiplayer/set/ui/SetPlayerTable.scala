/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui

import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer
import com.barrybecker4.game.multiplayer.common.ui.PlayerTable
import com.barrybecker4.game.multiplayer.set.SetPlayer

import java.awt.Color

class SetPlayerTable(players: PlayerList)
  extends PlayerTable(players, SetPlayerTable.setColumnNames):

  override def getPlayers: PlayerList =
    val model = table.getModel
    val nRows = model.getRowCount
    val result = new PlayerList()
    for i <- 0 until nRows do
      result.add(SetPlayer.createSetPlayer(
        model.getValueAt(i, PlayerTable.NAME_INDEX).asInstanceOf[String],
        model.getValueAt(i, PlayerTable.COLOR_INDEX).asInstanceOf[Color],
        model.getValueAt(i, PlayerTable.HUMAN_INDEX).asInstanceOf[Boolean]))
    result

  override def addRow(player: Any): Unit =
    val p = player.asInstanceOf[Player]
    val d = new Array[AnyRef](getNumColumns)
    d(PlayerTable.NAME_INDEX) = p.getName
    d(PlayerTable.COLOR_INDEX) = p.getColor
    d(PlayerTable.HUMAN_INDEX) = Boolean.box(p.isHuman)
    getPlayerModel.addRow(d)

  override def createPlayer(): Player =
    val ct = table.getRowCount
    val newColor = MultiGamePlayer.getNewPlayerColor(getPlayers)
    SetPlayer.createSetPlayer(s"Player ${ct + 1}", newColor, true)

object SetPlayerTable:
  private val setColumnNames = Array[String](PlayerTable.NAME, PlayerTable.COLOR, PlayerTable.HUMAN)
