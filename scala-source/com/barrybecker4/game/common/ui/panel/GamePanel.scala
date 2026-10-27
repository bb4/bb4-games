/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.panel

import com.barrybecker4.game.common.{GameContext, GameController, GameViewModel, Move}
import com.barrybecker4.game.common.ui.dialogs.{GameOptionsDialog, HelpDialog, NewGameDialog}
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.ui.components.{ResizableAppletPanel, TexturedPanel}
import com.barrybecker4.ui.dialogs.OutputWindow
import com.barrybecker4.ui.util.{GUIUtil, Log}
import javax.swing._
import java.awt.{AWTEvent, BorderLayout, Component}
import java.awt.event.{ActionEvent, ActionListener, ComponentAdapter, ComponentEvent}
import scala.compiletime.uninitialized

/**
 * This is an abstract base class for a Game UI.
 * See derived classes for specific game implementations.
 *
 * It contains a dockable toolbar which shows at least 5 buttons:
 * new game, undo, redo, options, and help.
 *  @see GameToolBar
 *
 * It puts the game board viewer in a scrollable pane on the left.
 * There is an info window on the right that gives statistics about the current game state.
 * There is a progress bar at the bottom that shows whenever the computer is thinking.
 *
 * This class is the main panel in the applet or application.
 * It contains everything related to actually playing the board game.
 *
 *  @author Barry Becker
 */
abstract class GamePanel extends TexturedPanel(GamePanel.BG_TEXTURE)
  with ActionListener with GameChangedListener[Move] with IGamePanel {

  var toolBar_ : GameToolBar = uninitialized
  var boardViewer_ : GameBoardViewer[?, ?] = uninitialized
  var newGameDialog_ : NewGameDialog = uninitialized
  var optionsDialog_ : GameOptionsDialog = uninitialized

  private var infoPanel_ : GameInfoPanel = uninitialized
  private var resizablePanel_ : ResizableAppletPanel = uninitialized

  def getViewer: GameBoardViewer[?, ?] = boardViewer_

  /**
   * common initialization in the event that there are multiple constructors.
   */
  override def init(parent: JFrame): Unit = {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK)
    initGui(parent)

    addComponentListener(new ComponentAdapter() {
      override def componentResized(ce: ComponentEvent): Unit = {
        GameContext.log(2, "resized")
      }
    })
  }

  override def openGame(): Unit = {
    boardViewer_.openGame()
  }

  override def saveGame(): Unit = {
    boardViewer_.saveGame()
  }

  def createToolbar(): GameToolBar = {
    new GameToolBar(GamePanel.BG_TEXTURE, this)
  }

  /**
   * Currently most games do not support online play (see poker)
   * @return true if the game supports online play and there is a server available
   */
  def isOnlinePlayAvailable: Boolean = false

  /**
   *  UIComponent initialization.
   */
  def initGui(parent: Component): Unit = {
    val mainPanel = new JPanel(new BorderLayout(), true)

    toolBar_ = createToolbar()
    boardViewer_ = createBoardViewer()

    val logWindow = new OutputWindow(GameContext.getLabel("LOG_OUTPUT"), null)
    GameContext.setLogger(new Log(logWindow))

    newGameDialog_ = createNewGameDialog(parent, boardViewer_)
    optionsDialog_ = createOptionsDialog(parent, boardViewer_.getController)

    infoPanel_ = createInfoPanel(boardViewer_.getController)
    infoPanel_.setTexture(GamePanel.BG_TEXTURE)

    // Erase viewer move type at the registration boundary (same pattern as TwoPlayerPanel).
    val viewer = boardViewer_.asInstanceOf[GameBoardViewer[Move, ?]]
    viewer.addGameChangedListener(infoPanel_.asInstanceOf[GameChangedListener[Move]])
    viewer.addGameChangedListener(this)

    mainPanel.setBorder(BorderFactory.createRaisedBevelBorder())
    mainPanel.add(toolBar_, BorderLayout.NORTH)
    mainPanel.add(new StatusBar(GamePanel.BG_TEXTURE), BorderLayout.SOUTH)
    mainPanel.add(infoPanel_, BorderLayout.EAST)
    mainPanel.add(createViewerPanel(boardViewer_), BorderLayout.CENTER)

    resizablePanel_ = new ResizableAppletPanel(mainPanel)

    setLayout(new BorderLayout())
    add(resizablePanel_, BorderLayout.CENTER)

    boardViewer_.startNewGame()
    Greeter.doGreeting()
  }

  private def createViewerPanel(boardViewer: GameBoardViewer[?, ?]): JPanel = {
    val viewerPanel = new JPanel(true)

    val boardViewerScrollPane = new JScrollPane()
    boardViewerScrollPane.setViewportView(boardViewer)

    val bottomDecorationPanel = createBottomDecorationPanel()
    viewerPanel.setLayout(new BorderLayout())
    viewerPanel.add(boardViewerScrollPane, BorderLayout.CENTER)
    if (bottomDecorationPanel != null) {
      viewerPanel.add(bottomDecorationPanel, BorderLayout.SOUTH)
    }
    viewerPanel
  }

  def createBottomDecorationPanel(): JPanel = null

  /**
   * @return the ui component used to display the current board state.
   */
  def createBoardViewer(): GameBoardViewer[?, ?]

  /**
   * @return the dialog used for configuring a new game to play.
   */
  def createNewGameDialog(parent: Component, viewer: GameViewModel): NewGameDialog

  /**
   * @return  the dialog used to specify various game options and parameters.
   */
  def createOptionsDialog(parent: Component, controller: GameController[?, ?]): GameOptionsDialog

  /**
   * @return the panel shown on the right hand side that displays statistics about the current game state.
   */
  def createInfoPanel(controller: GameController[?, ?]): GameInfoPanel

  /**
   * Display a help dialog.
   * This dialog should tell about the game and give instructions on how to play.
   */
  def showHelpDialog(): Unit

  /**
   * show a modal help dialog.
   * @param gameName  name of the game we are showing help for.
   * @param comments  version or other comments.
   * @param overview  Instructions on how to play and other info for the user.
   */
  final def showHelpDialog(gameName: String, comments: String, overview: String): Unit = {
    val dlg = new HelpDialog(null, gameName, comments, overview)
    dlg.setLocationRelativeTo(this)
    dlg.setModal(true)
    dlg.setVisible(true)
  }

  /**
   * This method allows javascript to resize the applet from the browser.
   */
  override def setSize(width: Int, height: Int): Unit = {
    resizablePanel_.setSize(width, height)
  }

  /**
   * implements the GameChangedListener interface.
   * This method called whenever a move has been made.
   */
  override def gameChanged(gce: GameChangedEvent[Move]): Unit = {
    toolBar_.getUndoButton.setEnabled(boardViewer_.getController.getLastMove != null)
  }

  /**
   * handle button click actions.
   * If you add your own custom buttons, you should override this, but be sure the first line is
   * super.actionPerformed(e);
   */
  override def actionPerformed(e: ActionEvent): Unit = {
    val source = e.getSource
    
    if (source == toolBar_.getNewGameButton) {
      newGameDialog_.setLocationRelativeTo(this)
      val canceled = newGameDialog_.showDialog
      if (!canceled) {
        boardViewer_.startNewGame()
        infoPanel_.reset()
      }
    }
    else if (source == toolBar_.getUndoButton) {
      GameContext.log(1, "undo clicked")
      toolBar_.getUndoButton.setEnabled(boardViewer_.canUndoMove)
      toolBar_.getRedoButton.setEnabled(true)
    }
    else if (source == toolBar_.getRedoButton) {
      GameContext.log(1, "redo clicked")
      toolBar_.getRedoButton.setEnabled(boardViewer_.canRedoMove)
      toolBar_.getUndoButton.setEnabled(true)
    }
    
    if (source == toolBar_.getOptionsButton) {
      optionsDialog_.showDialog
    }
    else if (source == toolBar_.getHelpButton) {
      showHelpDialog()
    }
  }
}

object GamePanel {
  private val CORE_IMAGE_PATH = GameContext.GAME_RESOURCE_ROOT + "common/ui/images/"
  
  val BG_TEXTURE: ImageIcon = {
    GameContext.log(2, "get ocean image")
    GUIUtil.getIcon(CORE_IMAGE_PATH + "ocean_trans_10.png")
  }
}
