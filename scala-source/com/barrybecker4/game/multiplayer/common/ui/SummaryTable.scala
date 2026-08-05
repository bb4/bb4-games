/* Copyright by Barry G. Becker, 2000-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.ui

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.ui.table.{BasicTableModel, ColorCellEditor, ColorCellRenderer, TableBase, TableColumnMeta}

import javax.swing.table.TableModel
import scala.jdk.javaapi.CollectionConverters

/**
 * Shows a list of the remaining players stats at the end of the game.
 * None of the cells are editable.
 *
 * @author Barry Becker
 */
abstract class SummaryTable(players: PlayerList, columnNames: Array[String])
  extends TableBase(CollectionConverters.asScala(players).toSeq, columnNames) {

  // Companion constants — instance vals are not initialized when TableBase's ctor
  // calls updateColumnMeta / addRow on the subclass.
  def NAME_INDEX: Int = SummaryTable.NAME_INDEX
  def COLOR_INDEX: Int = SummaryTable.COLOR_INDEX

  val NAME: String = GameContext.getLabel("NAME")
  val COLOR: String = GameContext.getLabel("COLOR")

  override def updateColumnMeta(columnMeta: Array[TableColumnMeta]): Unit = {
    val colorMeta = columnMeta(SummaryTable.COLOR_INDEX)
    colorMeta.setCellRenderer(new ColorCellRenderer())
    colorMeta.setCellEditor(new ColorCellEditor(GameContext.getLabel("SELECT_PLAYER_COLOR")))
    colorMeta.setPreferredWidth(25)
    colorMeta.setMinWidth(20)
    colorMeta.setMaxWidth(25)

    columnMeta(SummaryTable.NAME_INDEX).setPreferredWidth(100)
  }

  override def createTableModel(columnNames: Array[String]): TableModel =
    new BasicTableModel(columnNames.asInstanceOf[Array[Object]], 0, false)

  def getPlayerModel: BasicTableModel =
    table.getModel.asInstanceOf[BasicTableModel]
}

object SummaryTable:
  val NAME_INDEX = 0
  val COLOR_INDEX = 1
