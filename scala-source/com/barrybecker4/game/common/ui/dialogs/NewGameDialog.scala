/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.dialogs

import com.barrybecker4.game.common.{GameContext, GameController, GameViewModel}
import com.barrybecker4.game.common.board.IBoard
import com.barrybecker4.game.common.online.ui.OnlineGameManagerPanel
import com.barrybecker4.game.common.ui.panel.GridBoardParamPanel
import com.barrybecker4.ui.components.GradientButton
import com.barrybecker4.ui.dialogs.OptionsDialog

import javax.swing._
import javax.swing.event.{ChangeEvent, ChangeListener}
import java.awt._
import java.awt.event.{ActionEvent, WindowEvent}

/**
 * Use this modal dialog to let the user configure a new local game.
 * The have a choice of a new player vs player game or combinations of player vs computer or all computer.
 *
 * @author Barry Becker
 */
abstract class NewGameDialog(parent: Component, val viewer_ : GameViewModel)
  extends OptionsDialog(parent) with ChangeListener {

  /** the options get set directly on the game controller that is passed in. */
  val controller_ : GameController[?, ?] = viewer_.getController

  /** contains potentially 2 tabs that shows options for creating a new game, or playing online */
  var tabbedPanel_ : JTabbedPane = null

  var playLocalPanel_ : JPanel = null
  private var playOnlinePanel_ : OnlineGameManagerPanel = null

  var gridParamPanel_ : GridBoardParamPanel = null

  var startButton_ : GradientButton = null

  /** the options get set directly on the game controller and viewer that are passed in  */
  val board_ : IBoard[?] = controller_.getBoard

  showContent()

  override def createDialogContent: JComponent = {
    val mainPanel = new JPanel(true)
    mainPanel.setLayout(new BorderLayout())

    playLocalPanel_ = createNewLocalGamePanel()

    val buttonsPanel = createButtonsPanel

    // add the tabs. Tabs because there may be local or remote games.
    tabbedPanel_ = new JTabbedPane()
    tabbedPanel_.add(GameContext.getLabel("NEW_GAME"), playLocalPanel_)
    tabbedPanel_.setToolTipTextAt(0, GameContext.getLabel("NEW_GAME_TIP"))
    tabbedPanel_.addChangeListener(this)

    mainPanel.add(tabbedPanel_, BorderLayout.CENTER)
    mainPanel.add(buttonsPanel, BorderLayout.SOUTH)

    mainPanel
  }

  def createPlayOnlinePanel(): OnlineGameManagerPanel =
    null // nothing if no online play supported

  def createNewLocalGamePanel(): JPanel = {
    val playLocalPanel = new JPanel(true)
    playLocalPanel.setLayout(new BoxLayout(playLocalPanel, BoxLayout.Y_AXIS))
    val playerPanel = createPlayerAssignmentPanel()
    gridParamPanel_ = createBoardParamPanel()
    val customPanel = createCustomPanel()

    if (playerPanel != null) {
      playLocalPanel.add(playerPanel)
    }
    if (gridParamPanel_ != null) {
      playLocalPanel.add(gridParamPanel_)
    }
    if (customPanel != null) {
      playLocalPanel.add(customPanel)
    }

    playLocalPanel
  }

  def createPlayerAssignmentPanel(): JPanel

  override def createButtonsPanel: JPanel = {
    val buttonsPanel = new JPanel(new FlowLayout(), true)

    startButton_ = new GradientButton()
    initBottomButton(startButton_, GameContext.getLabel("START_GAME"), GameContext.getLabel("START_GAME_TIP"))
    initBottomButton(cancelButton, GameContext.getLabel("CANCEL"), GameContext.getLabel("NGD_CANCEL_TIP"))

    buttonsPanel.add(startButton_)
    buttonsPanel.add(cancelButton)

    buttonsPanel
  }

  /**
   * Subclasses use this to create their own custom options
   * Default is to have no custom panel.
   */
  def createCustomPanel(): JPanel =
    null

  /**
   * panel which allows changing board specific properties.
   */
  def createBoardParamPanel(): GridBoardParamPanel

  /**
   * Subclasses use this to create their own custom board configuration options
   * Default is to have no custom panel.
   */
  def createCustomBoardConfigPanel(): JPanel =
    null

  override def getTitle: String =
    GameContext.getLabel("NEW_GAME_DLG_TITLE")

  def ok(): Unit = {
    super.closeWithoutCanceling()
  }

  override def showDialog: Boolean = {
    val serverAvailable = controller_.isOnlinePlayAvailable
    if (serverAvailable) {
      if (playOnlinePanel_ == null) {
        playOnlinePanel_ = createPlayOnlinePanel()
        tabbedPanel_.add(playOnlinePanel_, 0)
        tabbedPanel_.setTitleAt(0, "Play Online")
        tabbedPanel_.setSelectedIndex(0)
        pack()
      }
      tabbedPanel_.setEnabledAt(0, true)
    } else {
      if (playOnlinePanel_ != null) {
        tabbedPanel_.setEnabledAt(0, false)
      }
    }
    super.showDialog
  }

  /**
   * Called when one of the buttons at the bottom pressed
   */
  override def actionPerformed(e: ActionEvent): Unit = {
    super.actionPerformed(e)
    val source = e.getSource

    if (source eq startButton_) {
      ok()
    }
  }

  /**
   * cancel button pressed
   */
  override def cancel(): Unit = {
    // You are only allowed to participate in only games when the dialog is open.
    if (playOnlinePanel_ != null) {
      playOnlinePanel_.closing()
    }
    super.cancel()
  }

  /**
   * Called when the selected tab changes,
   * Or in the case of online play when the player has joined a table that is now ready to play.
   * I that case the dialog will close and play will begin.
   */
  override def stateChanged(event: ChangeEvent): Unit = {
    if (event.getSource eq tabbedPanel_) {
      startButton_.setVisible(tabbedPanel_.getSelectedComponent ne playOnlinePanel_)
    }
    //else if (e.getSource() == playOnlinePanel_) {
    //    this.setVisible(false);
    //}
  }

  /**
   * If the window gets closed, then the player has stood up from his table if online.
   */
  override def processWindowEvent(e: WindowEvent): Unit = {
    if (e.getID == WindowEvent.WINDOW_CLOSING) {
      System.err.println("Window closing!")
      if (controller_.isOnlinePlayAvailable) {
        GameContext.log(0, "Standing up from table.")
        playOnlinePanel_.closing()
      }
    }
    super.processWindowEvent(e)
  }
}
