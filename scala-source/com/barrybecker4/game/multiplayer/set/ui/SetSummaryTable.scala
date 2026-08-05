/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.multiplayer.common.ui.SummaryTable
import com.barrybecker4.game.multiplayer.set.SetPlayer

import javax.swing.ListSelectionModel

class SetSummaryTable(players: PlayerList) extends SummaryTable(players, SetSummaryTable.COLUMN_NAMES):

  getTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)

  override def addRow(player: Any): Unit =
    val p = player.asInstanceOf[SetPlayer]
    val d = new Array[AnyRef](getNumColumns)
    // Use companion indices — parent NAME_INDEX/COLOR_INDEX are not initialized yet
    // when TableBase's constructor calls addRow.
    d(SetSummaryTable.NAME_INDEX) = p.getName
    d(SetSummaryTable.COLOR_INDEX) = p.getColor
    d(SetSummaryTable.NUM_SETS_INDEX) = "" + p.getNumSetsFound
    getPlayerModel.addRow(d)

object SetSummaryTable:
  val NAME_INDEX = 0
  val COLOR_INDEX = 1
  val NUM_SETS_INDEX = 2
  private val NUM_SETS = GameContext.getLabel("NUM_SETS")
  val COLUMN_NAMES = Array[String](
    GameContext.getLabel("NAME"),
    GameContext.getLabel("COLOR"),
    NUM_SETS
  )
