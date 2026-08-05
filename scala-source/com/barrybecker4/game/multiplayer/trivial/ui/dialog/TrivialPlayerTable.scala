/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui.dialog

import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer
import com.barrybecker4.game.multiplayer.common.ui.PlayerTable
import com.barrybecker4.game.multiplayer.trivial.player.TrivialPlayer

import javax.swing.table.TableModel
import java.awt.Color

/**
 * contains a list of players.
 * All the cells are editable.
 * It is initialized with a list of Players and returns a list of Players
 *
 * @author Barry Becker
 */
class TrivialPlayerTable(players: PlayerList) extends PlayerTable(players, TrivialPlayerTable.trivialColumnNames):

  /**
   * @return  the players represented by rows in the table
   */
  override def getPlayers: PlayerList =
    val model = this.table.getModel
    val nRows = model.getRowCount
    val players = new PlayerList()
    for i <- 0 until nRows do
      players.add(TrivialPlayer.createTrivialPlayer(
        model.getValueAt(i, PlayerTable.NAME_INDEX).asInstanceOf[String],
        model.getValueAt(i, PlayerTable.COLOR_INDEX).asInstanceOf[Color],
        model.getValueAt(i, PlayerTable.HUMAN_INDEX).asInstanceOf[Boolean]))
    players

  /**
   * add a row based on a player object
   * @param player to add
   */
  override def addRow(player: Any): Unit =
    val p = player.asInstanceOf[Player]
    val d = new Array[AnyRef](getNumColumns)
    d(PlayerTable.NAME_INDEX) = p.getName
    d(PlayerTable.COLOR_INDEX) = p.getColor
    d(PlayerTable.HUMAN_INDEX) = Boolean.box(p.isHuman)
    getPlayerModel.addRow(d)

  override def createPlayer(): Player =
    val ct = this.table.getRowCount
    val newColor = MultiGamePlayer.getNewPlayerColor(getPlayers)
    TrivialPlayer.createTrivialPlayer("Robot " + (ct + 1), newColor, true)

object TrivialPlayerTable:
  private val trivialColumnNames = Array[String](
    PlayerTable.NAME,
    PlayerTable.COLOR,
    PlayerTable.HUMAN
  )
