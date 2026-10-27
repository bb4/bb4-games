/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.dialogs

import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.game.common.{GameContext, GameViewModel}
import com.barrybecker4.game.common.board.IRectangularBoard
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog
import com.barrybecker4.game.common.ui.panel.GridBoardParamPanel
import com.barrybecker4.game.twoplayer.common.TwoPlayerController
import com.barrybecker4.ui.file.{FileChooserUtil, TextFileFilter}

import javax.swing.{BorderFactory, BoxLayout, JCheckBox, JFileChooser, JPanel}
import java.awt.event.{ActionEvent, ActionListener}
import java.awt.{BorderLayout, Component}
import java.io.File
import scala.compiletime.uninitialized

/**
 * Use this modal dialog to initialize the required game parameters that
 * are needed in order to start play in a 2 player game.
 *
 * @author Barry Becker
 */
class TwoPlayerNewGameDialog(parent: Component, viewer: GameViewModel)
  extends NewGameDialog(parent, viewer) with ActionListener:

  // Parent ctor calls createNewLocalGamePanel which sets these — do not use `= null`.
  private var optimizationCheckbox_ : JCheckBox = uninitialized
  private var playersPanel_ : PlayerAssignmentPanel = uninitialized

  protected def get2PlayerController(): TwoPlayerController[?, ?] =
    controller_.asInstanceOf[TwoPlayerController[?, ?]]

  /**
   * @return panel for the local player
   */
  override def createNewLocalGamePanel(): JPanel =
    val playLocalPanel = new JPanel()
    playLocalPanel.setLayout(new BoxLayout(playLocalPanel, BoxLayout.Y_AXIS))

    playersPanel_ = createPlayerAssignmentPanel()
    val optimizationPanel = createOptimizationPanel()
    gridParamPanel_ = createBoardParamPanel()
    val customPanel = createCustomPanel()

    playLocalPanel.add(playersPanel_)
    playLocalPanel.add(optimizationPanel)
    playLocalPanel.add(gridParamPanel_)
    if customPanel != null then
      playLocalPanel.add(customPanel)
    playLocalPanel

  /**
   * panel which allows changing board specific properties.
   */
  override def createBoardParamPanel(): GridBoardParamPanel =
    val b = board_.asInstanceOf[IRectangularBoard[?]]
    new GridBoardParamPanel(b.getNumRows, b.getNumCols, createCustomBoardConfigPanel())

  private def createOptimizationPanel(): JPanel =
    val p = new JPanel()
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS))
    p.setBorder(
      BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
        GameContext.getLabel("OPTIMIZATION_TITLE")))
    optimizationCheckbox_ = new JCheckBox(GameContext.getLabel("OPTIMIZATION"), false)
    p.setToolTipText(GameContext.getLabel("OPTIMIZATION_TIP"))
    optimizationCheckbox_.addActionListener(this)
    p.add(optimizationCheckbox_, BorderLayout.CENTER)
    p.add(new JPanel())

    p

  override def createPlayerAssignmentPanel(): PlayerAssignmentPanel =
    new PlayerAssignmentPanel(get2PlayerController(), this.myParent)

  override def ok(): Unit =
    val c = get2PlayerController()
    val board = board_.asInstanceOf[IRectangularBoard[?]]

    if board != null && gridParamPanel_ != null then
      val rowSize = gridParamPanel_.getRowSize
      val colSize = gridParamPanel_.getColSize
      board.setSize(rowSize, colSize)

    c.getOptions.setAutoOptimize(optimizationCheckbox_.isSelected())
    playersPanel_.ok()
    super.ok()

  override def actionPerformed(e: ActionEvent): Unit =
    val source = e.getSource()

    if source == startButton_ then
      ok()
    else if source == cancelButton then
      cancel()
    else if source == optimizationCheckbox_ then
      val checked = optimizationCheckbox_.isSelected()
      optimizationCheckBoxHandler(checked)
    else
      throw new IllegalStateException("unexpected source=" + source)

  private def optimizationCheckBoxHandler(checked: Boolean): Unit =
    if checked then
      // open a dlg to get a location for the optimization log
      // if they cancel this dlg then we leave the checkbox unchecked
      val chooser = FileChooserUtil.getFileChooser
      chooser.setCurrentDirectory(new File(FileUtil.getHomeDir + "/webdeployment"))
      chooser.setFileFilter(new TextFileFilter())
      val state = chooser.showOpenDialog(this)
      val file = chooser.getSelectedFile()
      if file == null || state != JFileChooser.APPROVE_OPTION then
        optimizationCheckbox_.setSelected(false)
      else
        playersPanel_.setBothComputerPlayers(true)
        playersPanel_.setPlayersSelected(false)
        get2PlayerController().getOptions.setAutoOptimizeFile(file.getAbsolutePath())
    else
      playersPanel_.setPlayersSelected(true)
