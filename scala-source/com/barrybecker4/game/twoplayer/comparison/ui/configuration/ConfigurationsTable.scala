// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.configuration

import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.comparison.model.config.{SearchOptionsConfig, SearchOptionsConfigList}
import com.barrybecker4.ui.table.{BasicCellRenderer, BasicTableModel, TableBase, TableColumnMeta}

import javax.swing.table.TableModel
import scala.jdk.CollectionConverters._
import scala.jdk.javaapi.CollectionConverters as JavaConverters

/**
 * Contains a list of search option configurations .
 *
 * @author Barry Becker
 */
class ConfigurationsTable(optionsList: SearchOptionsConfigList)
  extends TableBase(JavaConverters.asScala(optionsList).toSeq, ConfigurationsTable.columnNames) {

  override def updateColumnMeta(columnMeta: Array[TableColumnMeta]): Unit = {
    columnMeta(ConfigurationsTable.NAME_INDEX).setPreferredWidth(210)
    columnMeta(ConfigurationsTable.NAME_INDEX).setMaxWidth(310)
    columnMeta(ConfigurationsTable.NAME_INDEX).setCellRenderer(new BasicCellRenderer())
    columnMeta(ConfigurationsTable.ALGORITHM_INDEX).setPreferredWidth(110)
    columnMeta(ConfigurationsTable.ALGORITHM_INDEX).setMaxWidth(210)

    columnMeta(ConfigurationsTable.BRUTE_OPTIONS_INDEX).setCellRenderer(new BasicCellRenderer())
    columnMeta(ConfigurationsTable.BEST_MOVE_OPTIONS_INDEX).setCellRenderer(new BasicCellRenderer())
    columnMeta(ConfigurationsTable.MONTE_CARLO_OPTIONS_INDEX).setCellRenderer(new BasicCellRenderer())

    var i = 0
    while (i < getNumColumns) {
      columnMeta(i).setTooltip(ConfigurationsTable.columnTips(i))
      i += 1
    }
  }

  override def createTableModel(columnNames: Array[String]): TableModel =
    new BasicTableModel(ConfigurationsTable.columnNames.asInstanceOf[Array[Object]], 0, false)

  def removeRow(rowIndex: Int): Unit = {
    getPlayerModel.removeRow(rowIndex)
  }

  def getSearchOptions: SearchOptionsConfigList = {
    val model = getPlayerModel
    val nRows = model.getRowCount
    val searchOptions = new SearchOptionsConfigList(nRows)

    var i = 0
    while (i < nRows) {
      val options = model.getValueAt(i, ConfigurationsTable.INSTANCE_INDEX).asInstanceOf[SearchOptionsConfig]
      searchOptions.add(options)
      i += 1
    }

    searchOptions
  }

  def getPlayerModel: BasicTableModel =
    getModel.asInstanceOf[BasicTableModel]

  def addConfiguration(config: SearchOptionsConfig): Unit =
    addRow(config)

  protected override def addRow(row: Any): Unit = {
    val optionsConfig = row.asInstanceOf[SearchOptionsConfig]
    val sOptions = optionsConfig.getSearchOptions

    val d = new Array[AnyRef](ConfigurationsTable.NUM_COLS + 1)

    d(ConfigurationsTable.NAME_INDEX) = optionsConfig.getName
    d(ConfigurationsTable.ALGORITHM_INDEX) = sOptions.getSearchStrategyMethod
    d(ConfigurationsTable.BRUTE_OPTIONS_INDEX) = sOptions.getBruteSearchOptions.toString
    d(ConfigurationsTable.BEST_MOVE_OPTIONS_INDEX) = sOptions.getBestMovesSearchOptions.toString
    d(ConfigurationsTable.MONTE_CARLO_OPTIONS_INDEX) = sOptions.getMonteCarloSearchOptions.toString
    d(ConfigurationsTable.INSTANCE_INDEX) = optionsConfig

    getPlayerModel.addRow(d)
  }

  def updateRow(row: Int, optionsConfig: SearchOptionsConfig): Unit = {
    val sOptions = optionsConfig.getSearchOptions

    val d = new Array[AnyRef](ConfigurationsTable.NUM_COLS + 1)

    d(ConfigurationsTable.NAME_INDEX) = optionsConfig.getName
    d(ConfigurationsTable.ALGORITHM_INDEX) = sOptions.getSearchStrategyMethod
    d(ConfigurationsTable.BRUTE_OPTIONS_INDEX) = sOptions.getBruteSearchOptions.toString
    d(ConfigurationsTable.BEST_MOVE_OPTIONS_INDEX) = sOptions.getBestMovesSearchOptions.toString
    d(ConfigurationsTable.MONTE_CARLO_OPTIONS_INDEX) = sOptions.getMonteCarloSearchOptions.toString
    d(ConfigurationsTable.INSTANCE_INDEX) = optionsConfig

    getPlayerModel.removeRow(row)
    getPlayerModel.insertRow(row, d)
  }
}

object ConfigurationsTable {
  val NAME_INDEX = 0
  val ALGORITHM_INDEX = 1
  val BRUTE_OPTIONS_INDEX = 2
  val BEST_MOVE_OPTIONS_INDEX = 3
  val MONTE_CARLO_OPTIONS_INDEX = 4
  val INSTANCE_INDEX = 5

  val NAME = "Name"
  val ALGORITHM = "Search Algorithm"
  val BRUTE_OPTIONS = "Brute Force Options"
  val BEST_MOVE_OPTIONS = "Best Move Options"
  val MONTE_CARLO_OPTIONS = "Monte Carlo Options"
  val INST = ""

  val columnNames: Array[String] = Array(
    NAME, ALGORITHM, BRUTE_OPTIONS, BEST_MOVE_OPTIONS, MONTE_CARLO_OPTIONS, INST)

  private val columnTips: Array[String] = Array(
    "Some descriptive name",
    "Name of the search algorithm",
    "Brute force search options",
    "Options for how many of the better moves to select during search",
    "Options for when the search algorithm involves a stochastic process",
    "")

  val NUM_COLS: Int = columnNames.length
}
