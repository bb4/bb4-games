/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.online.ui

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.online.OnlineGameTable
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.multiplayer.common.MultiGameOptions
import com.barrybecker4.ui.table.{BasicTableModel, TableBase, TableButton, TableButtonListener, TableColumnMeta}

import javax.swing.table.TableModel
import java.awt.Color
import java.util.ArrayList

/**
 * A table that has a row for each virtual online game table.
 *
 * @author Barry Becker
 */
abstract class MultiPlayerOnlineGameTablesTable(
  colNames: Array[String],
  tableButtonListener: TableButtonListener
) extends TableBase {

  private val JOIN_TIP = GameContext.getLabel("ACTION_TIP")
  private val MIN_NUM_PLAYERS_TIP = GameContext.getLabel("MIN_NUM_PLAYERS_TIP")

  private var selectedTable_ : OnlineGameTable = null
  private val tableList_ = new ArrayList[OnlineGameTable]()

  private val tableButtonListener_ = tableButtonListener

  initColumnMeta(colNames)
  assert(tableButtonListener != null)
  initializeTable(null)

  def this(tableButtonListener: TableButtonListener) = {
    this(
      Array(
        MultiPlayerOnlineGameTablesTable.JOIN,
        MultiPlayerOnlineGameTablesTable.MIN_NUM_PLAYERS,
        MultiPlayerOnlineGameTablesTable.PLAYER_NAMES
      ),
      tableButtonListener
    )
  }

  override def updateColumnMeta(columnMeta: Array[TableColumnMeta]): Unit = {
    columnMeta(MultiPlayerOnlineGameTablesTable.NUM_PLAYERS_INDEX).setTooltip(MIN_NUM_PLAYERS_TIP)

    columnMeta(MultiPlayerOnlineGameTablesTable.PLAYER_NAMES_INDEX).setPreferredWidth(200)
    columnMeta(MultiPlayerOnlineGameTablesTable.PLAYER_NAMES_INDEX).setTooltip(
      MultiPlayerOnlineGameTablesTable.PLAYER_NAMES_TIP)

    val actionCol = columnMeta(MultiPlayerOnlineGameTablesTable.JOIN_INDEX)
    actionCol.setTooltip("Select which table you want to joint")

    val joinCellEditor = new TableButton(GameContext.getLabel("JOIN"), "id")
    joinCellEditor.addTableButtonListener(tableButtonListener_)
    joinCellEditor.setToolTipText(JOIN_TIP)
    actionCol.setCellRenderer(joinCellEditor)
    actionCol.setCellEditor(joinCellEditor)
    actionCol.setPreferredWidth(70)
  }

  override def createTableModel(columnNames: Array[String]): TableModel =
    new BasicTableModel(columnNames.asInstanceOf[Array[Object]], 0, true)

  def getGameTable(i: Int): OnlineGameTable =
    tableList_.get(i)

  def getSelectedTable: OnlineGameTable =
    selectedTable_

  def getPlayerModel: BasicTableModel =
    getModel.asInstanceOf[BasicTableModel]

  def removeRow(table: OnlineGameTable): Unit = {
    tableList_.remove(table)
    removeRowFromModel(table)
  }

  private def removeRowFromModel(table: OnlineGameTable): Unit = {
    var found = false
    val model = getPlayerModel
    var ct = 0
    while (ct < model.getRowCount && !found) {
      val names = model.getValueAt(ct, MultiPlayerOnlineGameTablesTable.PLAYER_NAMES_INDEX).asInstanceOf[String]
      if (names == table.getPlayerNames) {
        model.removeRow(ct)
        found = true
      }
      ct += 1
    }
  }

  def removeAllRows(): Unit = {
    val m = getPlayerModel
    var i = m.getRowCount - 1
    while (i >= 0) {
      m.removeRow(i)
      i -= 1
    }
    tableList_.clear()
  }

  def createOnlineTable(initialPlayerName: String, options: MultiGameOptions): OnlineGameTable

  def createPlayerForName(playerName: String): Player

  protected override def addRow(onlineTable: Any): Unit = {
    addRow(onlineTable.asInstanceOf[OnlineGameTable], true)
  }

  def addRow(onlineTable: OnlineGameTable, localPlayerAtTable: Boolean): Unit = {
    getPlayerModel.addRow(getRowObject(onlineTable, localPlayerAtTable))
    tableList_.add(onlineTable)
    selectedTable_ = onlineTable
  }

  def getRowObject(onlineTable: OnlineGameTable, localPlayerAtTable: Boolean): Array[AnyRef]

  def addRow(playersName: String, options: MultiGameOptions): Unit = {
    val onlineTable = createOnlineTable(playersName, options)
    addRow(onlineTable, true)
  }
}

object MultiPlayerOnlineGameTablesTable {
  val JOIN_INDEX = 0
  val NUM_PLAYERS_INDEX = 1
  val PLAYER_NAMES_INDEX = 2
  val NUM_BASE_COLUMNS = 3

  val JOIN: String = GameContext.getLabel("ACTION")
  val MIN_NUM_PLAYERS: String = GameContext.getLabel("MIN_NUM_PLAYERS")
  val PLAYER_NAMES: String = GameContext.getLabel("PLAYER_NAMES")
  val PLAYER_NAMES_TIP: String = GameContext.getLabel("PLAYER_NAMES_TIP")

  private var counter_ = 0

  def getUniqueName: String = synchronized {
    val name = "Table " + counter_
    counter_ += 1
    name
  }

  def getRandomColor: Color = {
    val r = GameContext.random().nextInt(256)
    new Color(r, 255 - r, GameContext.random().nextInt(256))
  }
}
