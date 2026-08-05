/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.ui

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.ui.table.{BasicTableModel, ColorCellEditor, ColorCellRenderer, TableBase, TableColumnMeta}

import javax.swing.JOptionPane
import javax.swing.table.TableModel
import scala.jdk.CollectionConverters._
import scala.jdk.javaapi.CollectionConverters as JavaConverters

import java.util.Vector

/**
 * PlayerTable contains a list of players.
 * All of the cells are editable.
 * It is initialized with a list of Players and returns a list of Players.
 *
 * @author Barry Becker
 */
abstract class PlayerTable(players: PlayerList, columnNames: Array[String])
  extends TableBase(JavaConverters.asScala(players).toSeq, columnNames) {

  /** remember the deleted rows, so we can add them back when the user clicks add again  */
  private val deletedRows = new java.util.ArrayList[Vector[?]]()

  override def updateColumnMeta(columnMeta: Array[TableColumnMeta]): Unit = {
    columnMeta(PlayerTable.NAME_INDEX).setPreferredWidth(130)
    columnMeta(PlayerTable.HUMAN_INDEX).setPreferredWidth(80)
    val colorColumnMeta = new TableColumnMeta(PlayerTable.COLOR, null, 20, 40, 40)
    colorColumnMeta.setCellRenderer(new ColorCellRenderer())
    colorColumnMeta.setCellEditor(new ColorCellEditor(GameContext.getLabel("SELECT_PLAYER_COLOR")))
    columnMeta(PlayerTable.COLOR_INDEX) = colorColumnMeta
  }

  def getPlayers: PlayerList

  /**
   * add another row to the end of the table.
   */
  def addRow(): Unit = {
    if (deletedRows.isEmpty) {
      val player = createPlayer()
      addRow(player)
    } else {
      getPlayerModel.addRow(deletedRows.remove(0))
    }
  }

  def createPlayer(): Player

  /**
   * remove the selected rows from the table
   */
  def removeSelectedRows(): Unit = {
    val nSelected = table.getSelectedRowCount
    val selectedRows = table.getSelectedRows
    if (selectedRows.length == table.getRowCount) {
      JOptionPane.showMessageDialog(null, "You are not allowed to delete all the players!")
      return
    }
    val model = getPlayerModel
    var i = nSelected - 1
    while (i >= 0) {
      val selRow = selectedRows(i)
      GameContext.log(0, "adding this to delete list:" +
        model.getDataVector.elementAt(selRow).getClass.getName)
      deletedRows.add(model.getDataVector.elementAt(selRow).asInstanceOf[Vector[?]])
      model.removeRow(selRow)
      i -= 1
    }
  }

  def getPlayerModel: BasicTableModel =
    getModel.asInstanceOf[BasicTableModel]

  override def createTableModel(columnNames: Array[String]): TableModel =
    new BasicTableModel(columnNames.asInstanceOf[Array[Object]], 0, true)
}

object PlayerTable {
  val NAME_INDEX = 0
  val COLOR_INDEX = 1
  val HUMAN_INDEX = 2

  val NAME: String = GameContext.getLabel("NAME")
  val COLOR: String = GameContext.getLabel("COLOR")
  val HUMAN: String = GameContext.getLabel("HUMAN")
}
