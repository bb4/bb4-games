// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.grid

import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.game.common.{GameContext, GameViewModel}
import com.barrybecker4.game.common.plugin.PluginManager
import com.barrybecker4.game.common.ui.menu.GameMenuListener
import com.barrybecker4.game.common.ui.panel.IGamePanel
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPanel
import com.barrybecker4.game.twoplayer.comparison.execution.{PerformanceRunner, PerformanceRunnerListener}
import com.barrybecker4.game.twoplayer.comparison.model.ResultsModel
import com.barrybecker4.game.twoplayer.comparison.model.config.SearchOptionsConfigList
import com.barrybecker4.ui.components.GradientButton
import com.barrybecker4.ui.file.{DirFileFilter, FileChooserUtil}

import javax.swing._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import java.io.File

/**
 * Show grid of game trials with run button at top.
 *
 * @author Barry Becker
 */
final class ComparisonGridPanel extends JPanel with ActionListener with GameMenuListener with PerformanceRunnerListener {

  private val DEFAULT_SAVE_LOCATION = FileUtil.getHomeDir + "temp/comparisonResults/" // NON-NLS

  private var runButton_ : GradientButton = null
  private var resultsLocationButton_ : GradientButton = null
  private var saveResultsButton_ : GradientButton = null
  private var resultFolderLabel_ : JLabel = null
  private var grid_ : ComparisonGrid = ComparisonGrid.createInstance(new SearchOptionsConfigList())
  private var scrollPane: JScrollPane = null
  private var optionsList: SearchOptionsConfigList = null
  private var gameName: String = null
  private var finalResultsModel: ResultsModel = null

  init()

  def setOptionsList(optionsList: SearchOptionsConfigList): Unit = {
    grid_ = ComparisonGrid.createInstance(optionsList)
    this.optionsList = optionsList
    runButton_.setEnabled(optionsList.size() > 0)
    scrollPane.setViewportView(grid_.getTable)
  }

  private def init(): Unit = {
    setLayout(new BorderLayout())
    add(createTopControls(), BorderLayout.NORTH)
    scrollPane = createGridPane()
    add(scrollPane, BorderLayout.CENTER)
  }

  private def createTopControls(): JPanel = {
    val topControlsPanel = new JPanel(new BorderLayout())
    runButton_ = new GradientButton("Run Comparisons")
    runButton_.addActionListener(this)
    runButton_.setEnabled(false)

    topControlsPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3))
    topControlsPanel.add(runButton_, BorderLayout.WEST)

    saveResultsButton_ = new GradientButton("Save results to")
    resultFolderLabel_ = new JLabel(DEFAULT_SAVE_LOCATION)

    saveResultsButton_.addActionListener(this)
    saveResultsButton_.setEnabled(false)

    resultsLocationButton_ = new GradientButton("...")
    resultsLocationButton_.addActionListener(this)

    val saveLocationPanel = new JPanel()
    saveLocationPanel.add(saveResultsButton_)
    saveLocationPanel.add(resultFolderLabel_)
    saveLocationPanel.add(resultsLocationButton_)
    topControlsPanel.add(saveLocationPanel, BorderLayout.CENTER)

    topControlsPanel
  }

  private def createGridPane(): JScrollPane = {
    val sp = new JScrollPane(grid_.getTable)
    sp.setPreferredSize(new Dimension(360, 120))
    sp.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(5, 5, 5, 5),
        sp.getBorder))
    sp
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    val source = e.getSource

    if (source eq runButton_) {
      val gamePanel = createGamePanel(gameName)

      val runner =
        new PerformanceRunner(gamePanel.asInstanceOf[TwoPlayerPanel], optionsList, this)

      runner.doComparisonRuns()
    } else if (source eq resultsLocationButton_) {
      updateSaveToLocation()
    } else if (source eq saveResultsButton_) {
      assert(finalResultsModel != null)
      finalResultsModel.saveModel(resultFolderLabel_.getText)

      val message = "Results written to " + resultFolderLabel_.getText
      JOptionPane.showMessageDialog(null, message)
    }
  }

  private def updateSaveToLocation(): Unit = {
    val chooser = FileChooserUtil.getFileChooser(new DirFileFilter())

    val dir = new File(resultFolderLabel_.getText)
    chooser.setCurrentDirectory(dir)
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
    chooser.setAcceptAllFileFilterUsed(false)
    chooser.showDialog(null, "Select this location")
    val file = chooser.getSelectedFile
    if (file != null) {
      resultFolderLabel_.setText(file.getAbsolutePath)
    }
  }

  override def gameChanged(gameName: String): Unit = {
    this.gameName = gameName
  }

  def createGamePanel(gameName: String): IGamePanel = {
    GameContext.loadResources(gameName)
    grid_.setGameName(gameName)
    repaint()

    PluginManager.getInstance.getPlugin(gameName).getPanelInstance
  }

  override def paint(g: Graphics): Unit = {
    grid_.updateRowHeight(scrollPane.getHeight)
    super.paint(g)
  }

  override def performanceRunsDone(model: ResultsModel): Unit = {
    finalResultsModel = model
    grid_.updateWithResults(finalResultsModel)
    saveResultsButton_.setEnabled(true)
  }
}
