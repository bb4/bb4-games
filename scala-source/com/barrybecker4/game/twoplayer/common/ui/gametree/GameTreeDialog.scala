/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree

import com.barrybecker4.game.common.board.Board
import com.barrybecker4.game.common.ui.panel.GameChangedEvent
import com.barrybecker4.game.common.ui.panel.GameChangedListener
import com.barrybecker4.game.twoplayer.common.TwoPlayerController
import com.barrybecker4.game.twoplayer.common.TwoPlayerViewModel
import com.barrybecker4.game.twoplayer.common.search.tree.IGameTreeViewable
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer
import com.barrybecker4.ui.dialogs.AbstractDialog

import javax.swing.*
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeExpansionListener
import java.awt.*
import java.util.List

/**
 * Draw the entire game tree using a java tree control.
 * Contains 3 sub representations: a java text tree with nodes that can be expanded and collapsed,
 * game viewer, and the graphical GameTreeViewer at the bottom that renders a tree.
 *
 * @author Barry Becker
 */
final class GameTreeDialog(
    parent: Component,
    boardViewer: AbstractTwoPlayerBoardViewer[?, ?],
    cellRenderer: GameTreeCellRenderer
) extends AbstractDialog(parent)
    with GameChangedListener[com.barrybecker4.game.twoplayer.common.TwoPlayerMove]
    with TreeExpansionListener:

  /** the options get set directly on the game controller that is passed in. */
  @volatile private var controller: TwoPlayerController[?, ?] = scala.compiletime.uninitialized

  @volatile private var treeViewer: GameTreeViewer = scala.compiletime.uninitialized
  @volatile private var textTree: TextualGameTree = scala.compiletime.uninitialized
  @volatile private var gameTreeButtons: GameTreeButtons = scala.compiletime.uninitialized
  @volatile private var tree: GameTreeViewable = scala.compiletime.uninitialized
  private var moveInfo: MoveInfoPanel = scala.compiletime.uninitialized

  /** the viewer in the debug window. */
  @volatile private var boardViewerField: TwoPlayerViewModel[?, ?] = scala.compiletime.uninitialized

  /** the controller that is actually being played in the normal view. */
  private var mainController: TwoPlayerController[?, ?] = scala.compiletime.uninitialized

  @volatile private var cellRendererField: GameTreeCellRenderer = scala.compiletime.uninitialized
  private var motionListener: GameTreeMotionListener = scala.compiletime.uninitialized

  initialize(boardViewer, cellRenderer)

  private def initialize(boardViewer: TwoPlayerViewModel[?, ?], cellRenderer: GameTreeCellRenderer): Unit =
    tree = GameTreeViewable(null)
    boardViewerField = boardViewer
    controller = boardViewer.getController.asInstanceOf[TwoPlayerController[?, ?]]
    cellRendererField = cellRenderer
    showContent()
    motionListener = GameTreeMotionListener(treeViewer, boardViewerField, moveInfo)

  /**
   * ui initialization of the tree control.
   */
  override def createDialogContent: JComponent =
    setTitle("Game Tree")
    textTree = createTextualTree()
    val mainPanel = JPanel(BorderLayout())

    val pieceRenderer =
      boardViewerField.asInstanceOf[AbstractTwoPlayerBoardViewer[?, ?]].getPieceRenderer.asInstanceOf[TwoPlayerPieceRenderer]
    treeViewer =
      GameTreeViewer(tree.getRootNode, cellRendererField.getColorMap, pieceRenderer)
    treeViewer.setPreferredSize(Dimension(500, 120))

    // the graphical tree goes below the top split pane.
    val splitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, true, createTopSplitPane(), treeViewer)

    mainPanel.add(splitPane, BorderLayout.CENTER)

    gameTreeButtons = GameTreeButtons(this)
    mainPanel.add(gameTreeButtons, BorderLayout.SOUTH)

    mainPanel

  def createTopSplitPane(): JSplitPane =
    val previewPanel = JPanel(BorderLayout())

    boardViewerField.asInstanceOf[Component].setPreferredSize(Dimension(200, 500))

    val viewerPanel = JPanel()
    viewerPanel.setLayout(BorderLayout())
    viewerPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3))
    moveInfo = MoveInfoPanel(cellRendererField.getColorMap)

    // this goes to the right of the test tree view
    viewerPanel.add(boardViewerField.asInstanceOf[Component], BorderLayout.CENTER)
    viewerPanel.add(moveInfo, BorderLayout.SOUTH)
    previewPanel.add(viewerPanel, BorderLayout.CENTER)

    JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, textTree, previewPanel)

  /**
   * start over from scratch.
   */
  def reset(): Unit =
    if textTree != null then
      textTree.removeMouseMotionListener(motionListener)
    tree = GameTreeViewable(null)
    boardViewerField.reset()
    textTree.reset(tree.getRootNode)
    treeViewer.setRoot(tree.getRootNode)

  override def treeExpanded(e: TreeExpansionEvent): Unit =
    refresh()
    treeViewer.refresh()

  override def treeCollapsed(e: TreeExpansionEvent): Unit =
    refresh()
    treeViewer.refresh()

  /**
   * called when the game has changed.
   * @param gce the event spawned when the game changed.
   */
  override def gameChanged(gce: GameChangedEvent[com.barrybecker4.game.twoplayer.common.TwoPlayerMove]): Unit =
    mainController = gce.getController.asInstanceOf[TwoPlayerController[?, ?]]
    motionListener.setMainController(mainController)
    gameTreeButtons.setMainController(mainController)
    // it is possible that the size of the game has changed since the game tree controller
    // was initialized. Make sure that it is synched up.
    val mainBoard = mainController.getBoard.asInstanceOf[Board[?]]
    val board = controller.getBoard.asInstanceOf[Board[?]]
    if mainBoard.getNumRows != board.getNumRows || mainBoard.getNumCols != board.getNumCols then
      board.setSize(mainBoard.getNumRows, mainBoard.getNumCols)

    // can't do it if we are in the middle of searching
    if mainController.isProcessing then
      return

    showCurrentGameTree()

    motionListener.resetOldChainLength()

  /**
   * show whatever portion of the game tree that has been searched so far.
   */
  def showCurrentGameTree(): Unit =
    val root = tree.getTreeCopy

    textTree.reset(root)

    treeViewer.setRoot(root)
    if textTree == null then return

    textTree.expandRow(0)
    textTree.addMouseMotionListener(motionListener)

    // make the viewer shows the game so far
    setMoveList(mainController.getMoveList.copy())

    refresh()

  def getGameTreeViewable: IGameTreeViewable = tree

  /**
   * Create the game tree representation
   * @return the the java tree control itself
   */
  private def createTextualTree(): TextualGameTree =
    if tree.getRootNode == null then
      return null
    val textTree = TextualGameTree(tree.getRootNode, cellRendererField)
    textTree.addTreeExpansionListener(this)
    textTree

  /**
   * Initialize the tree previewer to show the moves made so far.
   */
  private def setMoveList(moveList: List[?]): Unit =
    boardViewerField.reset()
    boardViewerField.asInstanceOf[AbstractTwoPlayerBoardViewer[com.barrybecker4.game.twoplayer.common.TwoPlayerMove, ?]]
      .showMoveSequence(moveList.asInstanceOf[java.util.List[com.barrybecker4.game.twoplayer.common.TwoPlayerMove]])

  /**
   * refresh the game tree.
   */
  private def refresh(): Unit =
    paint(getGraphics())

  /**
   * called when the ok button is clicked.
   */
  override def close(): Unit =
    // if we set the root to null, then it doesn't have to build the tree
    controller.setGameTreeViewable(null)
    super.close()
