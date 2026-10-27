// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.grid

import com.barrybecker4.game.twoplayer.comparison.model.{PerformanceResultsPair, ResultsModel}
import com.barrybecker4.game.twoplayer.comparison.model.config.{SearchOptionsConfig, SearchOptionsConfigList}
import com.barrybecker4.game.twoplayer.comparison.ui.grid.cellrenderers.{FirstColumnCellRenderer, ResultGridCellRenderer, ResultHeaderCellRenderer}
import com.barrybecker4.ui.table.{BasicTableModel, TableBase, TableColumnMeta}

import javax.swing.table.{TableColumn, TableModel}
import scala.jdk.CollectionConverters._
import scala.jdk.javaapi.CollectionConverters as JavaConverters

/**
 * Turns the list of search option configurations into a NxN grid
 * to compare the performance of all the search configurations in the list.
 *
 * @author Barry Becker
 */
class ComparisonGrid private (optionsList: SearchOptionsConfigList, colNames: Array[String])
  extends TableBase(JavaConverters.asScala(optionsList).toSeq, colNames) {

  ComparisonGrid.HEADER_HEIGHT
  setRowHeight(60)

  var i = 1
  while (i < getNumColumns) {
    val col = table.getColumnModel.getColumn(i)
    col.setHeaderRenderer(new ResultHeaderCellRenderer())
    i += 1
  }

  def updateRowHeight(height: Int): Unit = {
    getTable.setRowHeight((height - ComparisonGrid.HEADER_HEIGHT) / getNumRows)
  }

  def setGameName(gameName: String): Unit = {
    getTable.getColumnModel.getColumn(0).setHeaderValue(gameName)
  }

  override def updateColumnMeta(columnMeta: Array[TableColumnMeta]): Unit = {
    columnMeta(0).setPreferredWidth(210)
    columnMeta(0).setMaxWidth(310)
    columnMeta(0).setCellRenderer(new FirstColumnCellRenderer())

    var i = 1
    while (i < getNumColumns) {
      columnMeta(i).setCellRenderer(new ResultGridCellRenderer())
      val col = table.getColumnModel.getColumn(i)
      col.setHeaderRenderer(new ResultHeaderCellRenderer())
      i += 1
    }
  }

  override def createTableModel(columnNames: Array[String]): TableModel =
    new BasicTableModel(columnNames.asInstanceOf[Array[Object]], 0, false)

  override def getModel: BasicTableModel =
    table.getModel.asInstanceOf[BasicTableModel]

  protected override def addRow(row: Any): Unit = {
    val optionsConfig = row.asInstanceOf[SearchOptionsConfig]

    if (colNames == null) return

    val d = new Array[AnyRef](colNames.length + 1)

    d(0) = optionsConfig.getName
    var i = 1
    while (i <= colNames.length) {
      d(i) = new PerformanceResultsPair()
      i += 1
    }
    getModel.addRow(d)
  }

  def updateWithResults(model: ResultsModel): Unit = {
    val size = colNames.length - 1
    var i = 0
    while (i < size) {
      var j = 0
      while (j < size) {
        getModel.setValueAt(model.getResults(i, j), i, j + 1)
        j += 1
      }
      i += 1
    }
  }
}

object ComparisonGrid {
  private val HEADER_HEIGHT = 40

  def createInstance(optionsList: SearchOptionsConfigList): ComparisonGrid =
    new ComparisonGrid(optionsList, createColumnNames(optionsList))

  private def createColumnNames(optionsList: SearchOptionsConfigList): Array[String] = {
    val names = new Array[String](optionsList.size() + 1)
    names(0) = "---"
    var i = 1
    optionsList.asScala.foreach { config =>
      names(i) = config.getName
      i += 1
    }
    names
  }
}
