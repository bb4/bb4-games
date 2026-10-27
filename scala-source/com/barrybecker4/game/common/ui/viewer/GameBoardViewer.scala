/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.viewer

import com.barrybecker4.game.common.{GameContext, GameController, GameViewModel, Move, MoveList}
import com.barrybecker4.game.common.board.{Board, IBoard}
import com.barrybecker4.game.common.persistence.GameExporter
import com.barrybecker4.game.common.ui.SgfFileFilter
import com.barrybecker4.game.common.ui.panel.{GameChangedEvent, GameChangedListener}
import com.barrybecker4.game.common.ui.ComputerMoveProgressBar
import com.barrybecker4.ui.file.FileChooserUtil
import javax.swing._
import java.awt.{Color, Cursor, Graphics}
import java.io.File
import scala.jdk.CollectionConverters._

/**
 * This class contains a GameController and displays the current state of the Game.
 * The GameController contains a Board which describes this state.
 * The game specific GameController is created upon construction to be used internally.
 * This class contains all that is needed to render the board and its pieces.
 * There should be no references to swing classes outside the ui subpackage.
 *
 * This class displays the game and takes input from the user.
 * It passes the user's input to the GameController, which in turn tells the GameViewer
 * things such as whether the user's move was legal or not, and also tells the GameViewer
 * what the computer's move is.
 *
 *  note: subclasses must override paintComponent to have the board show up.
 *  TODO: split out GameViewModel parts into a separate class. The viewer should have a model not be a model.
 *
 *  @author Barry Becker
 */
abstract class GameBoardViewer[M <: Move, B <: Board[M]] extends JPanel
  with GameViewModel with GameChangedListener[M] {

  /** every GameBoardViewer must contain a controller. */
  var controller: GameController[M, B] = createController()
  controller.setViewer(this)

  /** for restoring undone moves. */
  val undoneMoves_ = new MoveList[M]()

  private var mouseListener_ : ViewerMouseListener[M, B] = scala.compiletime.uninitialized

  /** list of listeners for handling those events. */
  private val gameListeners_ = new scala.collection.mutable.ListBuffer[GameChangedListener[M]]()

  val waitCursor_ = new Cursor(Cursor.WAIT_CURSOR)
  var origCursor_ : Cursor = scala.compiletime.uninitialized
  var parent_ : JFrame = scala.compiletime.uninitialized

  def getParentFrame: JFrame = parent_

  def setParentFrame(parent: JFrame): Unit =
    parent_ = parent
  private var lastFileAccessed: File = scala.compiletime.uninitialized

  // this activates tooltip text for the component
  this.setToolTipText("")
  ToolTipManager.sharedInstance().setDismissDelay(100000)
  origCursor_ = this.getCursor

  mouseListener_ = createViewerMouseListener()
  addMouseListener(mouseListener_)
  addMouseMotionListener(mouseListener_)
  // add a listener so that we realize when the computer (or human) has finished making his move
  addGameChangedListener(this)

  def createViewerMouseListener(): ViewerMouseListener[M, B] = {
    new ViewerMouseListener[M, B](this)
  }

  /**
   * @return the game specific controller for this viewer.
   */
  def createController(): GameController[M, B]

  /**
   * @return our game controller.
   */
  override def getController: GameController[M, B] = controller

  /**
   * @param progressBar an optional progress bar for showing progress as the computer thinks about its next move.
   */
  def setProgressBar(progressBar: ComputerMoveProgressBar): Unit = {
    // not used
  }

  /**
   * restore a game from a previously saved file (in SGF = Smart Game Format)
   * Derived classes should implement the details of the open.
   */
  def openGame(): Unit = {
    val chooser = FileChooserUtil.getFileChooser(new SgfFileFilter())
    if (lastFileAccessed != null) {
      println("lastFile=" + lastFileAccessed.toString)
      chooser.setCurrentDirectory(lastFileAccessed)
    }
    val state = chooser.showOpenDialog(null)

    val file = chooser.getSelectedFile
    if (file != null && state == JFileChooser.APPROVE_OPTION) {
      lastFileAccessed = file
      controller.restoreFromFile(file.getAbsolutePath)
      sendGameChangedEvent(controller.getLastMove)
      this.refresh()
    }
  }

  /**
   * save the current game to the specified file (in SGF = Smart Game Format)
   */
  def saveGame(): Unit = {
    saveGame(null)
  }

  /**
   * save the current game to the specified file (in SGF = Smart Game Format)
   * Derived classes should implement the details of the save
   */
  private def saveGame(ae: AssertionError): Unit = {
    val chooser = FileChooserUtil.getFileChooser(new SgfFileFilter())
    val state = chooser.showSaveDialog(null)
    val file = chooser.getSelectedFile
    if (file != null && state == JFileChooser.APPROVE_OPTION) {
      // if it does not have the .sgf extension already then add it
      var fPath = file.getAbsolutePath
      if (!fPath.endsWith("." + SgfFileFilter.SGF_EXTENSION)) {
        fPath = fPath + "." + SgfFileFilter.SGF_EXTENSION
      }
      val exporter = controller.getExporter
      exporter.saveToFile(fPath, ae)
    }
  }

  /**
   * Cause the board UI to draw itself based on the current state of the game.
   */
  def refresh(): Unit = {
    this.repaint()
  }

  /**
   *  Animate the last move so the player does not lose orientation.
   *  By default this just redraws the board, but for games with complex moves,
   *  we may want to do more.
   */
  def showLastMove(): Unit = {
    this.refresh()
  }

  /**
   * return the game to its original state.
   */
  override def reset(): Unit = {
    controller.reset() //clear what's there and start over
    val board = controller.getBoard
    commonReset(board)
  }

  /**
   * Each board must create its own renderer singleton.
   * @return game viewer specific renderer
   */
  def getBoardRenderer: GameBoardRenderer

  def commonReset(board: B): Unit = {
    val nrows = board.getNumRows
    val ncols = board.getNumCols

    setSize(getBoardRenderer.getSize(nrows, ncols))
    setPreferredSize(getBoardRenderer.getPreferredSize(nrows, ncols))
    mouseListener_.reset()
  }

  /**
   * start over with a new game using the current options.
   */
  def startNewGame(): Unit

  /**
   * in some cases the viewer is used to show games only.
   */
  def setViewOnly(viewOnly: Boolean): Unit = {
    if (viewOnly) {
      removeMouseListener(mouseListener_)
      removeMouseMotionListener(mouseListener_)
    }
    else {
      addMouseListener(mouseListener_)
      addMouseMotionListener(mouseListener_)
    }
  }

  /**
   * Implements the GameChangedListener interface.
   * Called when the game has changed in some way
   */
  override def gameChanged(evt: GameChangedEvent[M]): Unit = {
    GameContext.log(1, "game changed. refreshing viewer.")
    refresh()
  }

  /**
   * This method gets called when the game has changed in some way.
   * Most likely because a move has been played. It does not need to be on the eventDispatch thread.
   */
  def sendGameChangedEvent(m: M): Unit = {
    val gce = new GameChangedEvent[M](m, controller, this)
    for (gcl <- gameListeners_) {
      gcl.gameChanged(gce)
    }
  }

  /**
   * @return true if there is a move to undo.
   */
  final def canUndoMove: Boolean = {
    controller.getLastMove != null
  }

  /**
   * @return true if there is a move to redo.
   */
  final def canRedoMove: Boolean = {
    !undoneMoves_.isEmpty
  }

  /**
   * display a dialog at the end of the game showing who won and other relevant
   * game specific information.
   */
  def showWinnerDialog(): Unit = {
    val message = getGameOverMessage
    JOptionPane.showMessageDialog(this, message, GameContext.getLabel("GAME_OVER"),
      JOptionPane.INFORMATION_MESSAGE)
  }

  /**
   * @return   the message to display at the completion of the game.
   */
  def getGameOverMessage: String

  /**
   * @param c  the new color of the board.
   */
  override def setBackground(c: Color): Unit = {
    getBoardRenderer.setBackground(c)
    refresh()
  }

  /**
   * @return c  the board color
   */
  override def getBackground: Color = {
    getBoardRenderer.getBackground
  }

  /**
   * @param c  the new color of the board's grid.
   */
  def setGridColor(c: Color): Unit = {
    getBoardRenderer.setGridColor(c)
    refresh()
  }

  /**
   * @return c  the new color of the board's grid.
   */
  def getGridColor: Color = {
    getBoardRenderer.getGridColor
  }

  /**
   * This is how the client can register itself to receive these events.
   * @param gcl the listener to add
   */
  def addGameChangedListener(gcl: GameChangedListener[M]): Unit = {
    gameListeners_ += gcl
  }

  /**
   * This is how the client can unregister itself to receive these events.
   * @param gcl the listener  to remove
   */
  private def removeGameChangedListener(gcl: GameChangedListener[?]): Unit = {
    gameListeners_ -= gcl.asInstanceOf[GameChangedListener[M]]
  }

  /**
   * This renders the current state of the Board to the screen.
   */
  override def paintComponent(g: Graphics): Unit = {
    super.paintComponents(g)

    getBoardRenderer.render(g,
      controller.getCurrentPlayer,
      controller.getPlayers,
      controller.getBoard,
      getWidth, getHeight)
  }

  /**
   * @return the cached game board if we are in the middle of processing.
   */
  def getBoard: IBoard[?] = controller.getBoard

  /**
   * implements the AssertHandler interface.
   * It gets called whenever an assertion fails.
   */
  def assertFailed(ae: AssertionError): Unit = {
    GameContext.log(1, "An assertion failed. Writing to error file.")
    ae.printStackTrace()
    // make sure the state of the game at the point of the error is displayed.
    this.refresh()
    saveGame()
  }
}
