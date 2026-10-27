/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui

import com.barrybecker4.game.common.{GameContext, GameController}
import com.barrybecker4.game.common.ui.ComputerMoveProgressBar
import com.barrybecker4.game.common.ui.dialogs.GameOptionsDialog
import com.barrybecker4.game.common.ui.panel.{GameChangedListener, GamePanel}
import com.barrybecker4.game.twoplayer.common.{TwoPlayerController, TwoPlayerOptions}
import com.barrybecker4.game.twoplayer.common.ui.dialogs.TwoPlayerOptionsDialog
import com.barrybecker4.game.twoplayer.common.ui.gametree.{GameTreeCellRenderer, GameTreeDialog}
import com.barrybecker4.ui.components.TexturedPanel

import javax.swing.JPanel
import java.awt.event.{ActionEvent, ActionListener}
import java.awt.{BorderLayout, Component}

/**
 * This is an abstract base class for a two player game UI.
 * See derived classes for specific game implementations.
 *
 * It contains a dockable toolbar which shows at least 5 buttons:
 *  new game, undo, redo, options, and help.
 * It puts the game board viewer in a scrollable pane on the left.
 * There is an info window on the right that gives statistics about the current game state.
 * There is a progress bar at the bottom that shows whenever the computer is thinking.
 *
 * This class is the main panel in the applet or application.
 * It contains everything related to actually playing the board game.
 *
 *  @author Barry Becker
 */
abstract class TwoPlayerPanel extends GamePanel with ActionListener with GameChangedListener[?]:

  /** for showing progress while the computer thinks. */
  private var progressBar_ : ComputerMoveProgressBar = null

  /** dialog to show the game tree while processing and other debug info. */
  private var treeDialog_ : GameTreeDialog = null

  def get2PlayerController(): TwoPlayerController[?, ?] =
    boardViewer_.getController.asInstanceOf[TwoPlayerController[?, ?]]

  /**
   * UIComponent initialization.
   */
  override def initGui(parent: Component): Unit =
    super.initGui(parent)

    // we create a separate controller for the TreeDialog so it can browse without
    // disturbing the state of the actual game.
    treeDialog_ = createGameTreeDialog()

    val options = get2PlayerController().getOptions
    GameContext.log(2, "2player pane init  get2PlayerController().getShowGameTree() ="
      + options.getShowGameTree())
    if options.getShowGameTree() then
      showGameTreeDialog()

  override def createBottomDecorationPanel(): JPanel =
    progressBar_ = createProgressBar()
    boardViewer_.setProgressBar(progressBar_)

    // put the progress bar in a panel so it does not cause a re-layout
    val progressPanel = new TexturedPanel(GamePanel.BG_TEXTURE)
    progressPanel.setLayout(new BorderLayout())
    progressPanel.add(progressBar_, BorderLayout.CENTER)
    progressPanel

  def createProgressBar(): ComputerMoveProgressBar =
    val progressBar = new ComputerMoveProgressBar()
    // show only when used
    setVisible(!get2PlayerController().getPlayers.allPlayersHuman)
    progressBar

  /**
   * @return  the dialog used to specify various game options and parameters.
   */
  override def createOptionsDialog(parent: Component, controller: GameController[?, ?]): GameOptionsDialog =
    new TwoPlayerOptionsDialog(parent, controller.asInstanceOf[TwoPlayerController[?, ?]])

  /**
   * The game tree dialog shows the game tree for better understanding.
   * but it also has its own (view only) viewer that is used to show any particular node in the game tree.
   * @return the new game tree dialog
   */
  protected def createGameTreeDialog(): GameTreeDialog =
    val viewer = createBoardViewer().asInstanceOf[AbstractTwoPlayerBoardViewer[?, ?]]
    // we don't want it to receive click events
    viewer.setViewOnly(true)
    val renderer = new GameTreeCellRenderer(viewer.getPieceRenderer.asInstanceOf[TwoPlayerPieceRenderer])
    new GameTreeDialog(null, viewer, renderer)

  /**
   * Take the root from the treeDialog and set it on the TwoPlayerController so it can
   * create the tree and allow the treeDialog to show it when the
   * change event happens.
   */
  private def showGameTreeDialog(): Unit =
    treeDialog_.reset()
    // Java API used raw GameChangedListener; erase generics at the boundary.
    boardViewer_
      .asInstanceOf[com.barrybecker4.game.common.ui.viewer.GameBoardViewer[com.barrybecker4.game.common.Move, com.barrybecker4.game.common.board.Board[com.barrybecker4.game.common.Move]]]
      .addGameChangedListener(treeDialog_.asInstanceOf[GameChangedListener[com.barrybecker4.game.common.Move]])
    get2PlayerController().setGameTreeViewable(treeDialog_.getGameTreeViewable)
    treeDialog_.setVisible(true)

  /**
   * handle button click actions.
   * If you add your own custom buttons, you should override this, but be sure the first line is
   * <P>
   * super.actionPerformed(e);
   */
  override def actionPerformed(e: ActionEvent): Unit =
    val source = e.getSource()

    if source == toolBar_.getNewGameButton then
      get2PlayerController().pause()
      val canceled = newGameDialog_.showDialog
      if !canceled then
        // start game with the newly defined options
        startGame()
      else
        getBoardViewer().continueProcessing()
    else if source == toolBar_.getUndoButton then
      undoMove()
    else if source == toolBar_.getRedoButton then
      redoMove()
    if source == toolBar_.getOptionsButton then
      showOptionsDialog()
    else if source == toolBar_.getHelpButton then
      showHelpDialog()

  private def undoMove(): Unit =
    getBoardViewer().undoLastManMove()
    // gray it if there are now no more moves to undo
    toolBar_.getUndoButton.setEnabled(boardViewer_.canUndoMove)
    toolBar_.getRedoButton.setEnabled(true)

  private def redoMove(): Unit =
    getBoardViewer().redoLastManMove()
    // gray it if there are now no more moves to undo
    toolBar_.getRedoButton.setEnabled(boardViewer_.canRedoMove)
    toolBar_.getUndoButton.setEnabled(true)

  private def showOptionsDialog(): Unit =
    val canceled = optionsDialog_.showDialog
    GameContext.log(2, "options selected  canceled=" + canceled)
    if !canceled then // start a game with the newly defined options
      GameContext.log(0, "options selected not canceled  show game tree="
        + get2PlayerController().getOptions.getShowGameTree())
      if get2PlayerController().getOptions.getShowGameTree() then
        showGameTreeDialog()
      else
        treeDialog_.setVisible(false)

  private def getBoardViewer(): AbstractTwoPlayerBoardViewer[?, ?] =
    boardViewer_.asInstanceOf[AbstractTwoPlayerBoardViewer[?, ?]]

  /**
   * We don't show the progress bar is if both players are human.
   */
  def startGame(): Unit =
    progressBar_.setVisible(!get2PlayerController().getPlayers.allPlayersHuman)
    getBoardViewer().startNewGame()
