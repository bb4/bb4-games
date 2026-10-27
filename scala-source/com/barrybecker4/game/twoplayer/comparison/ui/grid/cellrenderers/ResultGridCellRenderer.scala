// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.grid.cellrenderers

import com.barrybecker4.game.twoplayer.comparison.model.PerformanceResultsPair

import javax.swing._
import javax.swing.table.{TableCellRenderer, TableModel}
import java.awt.{Color, Component, Dimension, GridBagConstraints, GridBagLayout}

/**
 * Draws all the segmented bars representing the results of two games played between
 * two players. The first bar segment of each bar represents the first player playing first,
 * and the second segment is for when the second player plays first.
 *
 * @author Barry Becker
 */
class ResultGridCellRenderer extends JPanel with TableCellRenderer {

  private var perfResults: PerformanceResultsPair = null

  private val wonBar = new WonBar()
  private val timeBar = new DualBar(ResultGridCellRenderer.TIME_BAR_COLOR)
  private val numMovesBar = new DualBar(ResultGridCellRenderer.NUM_MOVES_BAR_COLOR)

  setLayout(new GridBagLayout())
  setMinimumSize(new Dimension(100, 100))

  add(wonBar, createConstraints(0, ResultGridCellRenderer.WON_BAR_PROPORTION))
  add(timeBar, createConstraints(1, ResultGridCellRenderer.TIME_BAR_PROPORTION))
  add(numMovesBar, createConstraints(2, ResultGridCellRenderer.NUM_MOVES_BAR_PROPORTION))

  private def createConstraints(gridy: Int, weighty: Double): GridBagConstraints = {
    val constrs = new GridBagConstraints()
    constrs.gridx = 0
    constrs.gridy = gridy
    constrs.weightx = 1.0
    constrs.weighty = weighty
    constrs.fill = GridBagConstraints.BOTH
    constrs
  }

  override def getTableCellRendererComponent(
    table: JTable,
    value: AnyRef, isSelected: Boolean, hasFocus: Boolean,
    row: Int, col: Int
  ): Component = {
    setData(table.getModel, row, col)
    this
  }

  private def setData(tableModel: TableModel, row: Int, col: Int): Unit = {
    perfResults = tableModel.getValueAt(row, col).asInstanceOf[PerformanceResultsPair]
    setToolTipText(perfResults.toHtmlString)

    wonBar.setOutcomes(perfResults.getOutcomes, perfResults.getFinalImages)
    timeBar.setBarSegments(perfResults.getNormalizedTimes, perfResults.getTimesFormatted)
    numMovesBar.setBarSegments(perfResults.getNormalizedNumMoves, perfResults.getNumMoves)
  }
}

object ResultGridCellRenderer {
  private val TIME_BAR_COLOR = new Color(170, 140, 100)
  private val NUM_MOVES_BAR_COLOR = new Color(90, 110, 70)

  private val WON_BAR_PROPORTION = 0.8
  private val TIME_BAR_PROPORTION = 0.1
  private val NUM_MOVES_BAR_PROPORTION = 0.1
}
