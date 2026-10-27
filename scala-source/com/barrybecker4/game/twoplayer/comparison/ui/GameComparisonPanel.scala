// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui

import com.barrybecker4.game.twoplayer.comparison.ui.configuration.ConfigurationPanel
import com.barrybecker4.game.twoplayer.comparison.ui.grid.ComparisonGridPanel

import javax.swing.{JPanel, JTabbedPane}
import javax.swing.event.{ChangeEvent, ChangeListener}
import java.awt.BorderLayout

/**
 * Allows comparing the performance of two player games played against each other
 * with different search strategies.
 * There are two main tabs
 *  1) configuration tab - allows you to define what search strategies will be used
 *  2) comparison table tab - Compares all the search strategies played off against each
 *    other using the specified two player game.
 *
 *  @author Barry Becker
 */
class GameComparisonPanel extends JPanel with ChangeListener {

  private val tabbedPane = new JTabbedPane()
  private val comparisonConfigurationPanel = new ConfigurationPanel()
  private val comparisonGridPanel = new ComparisonGridPanel()

  comparisonConfigurationPanel.setName("Configuration")
  comparisonGridPanel.setName("Comparison")

  initGui()

  def initGui(): Unit = {
    setLayout(new BorderLayout())

    tabbedPane.add(comparisonConfigurationPanel)
    tabbedPane.setToolTipTextAt(0,
      "Configure the search strategies to compete against each other")
    tabbedPane.addChangeListener(this)
    tabbedPane.add(comparisonGridPanel)
    tabbedPane.setToolTipTextAt(0,
      "Shows the results of the two player game competing against itself with specified search strategies")

    add(tabbedPane, BorderLayout.CENTER)
  }

  def getGridPanel: ComparisonGridPanel = comparisonGridPanel

  override def stateChanged(e: ChangeEvent): Unit = {
    val index = tabbedPane.getSelectedIndex
    if (index == 1) {
      comparisonGridPanel.setOptionsList(comparisonConfigurationPanel.getConfigurations)
    }
  }
}
