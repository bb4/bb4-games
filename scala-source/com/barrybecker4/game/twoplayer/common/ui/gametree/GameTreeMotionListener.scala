/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.Move
import com.barrybecker4.game.twoplayer.common.TwoPlayerController
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.TwoPlayerViewModel
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer

import javax.swing.*
import javax.swing.tree.TreePath
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.util.LinkedList
import java.util.List

/**
 * Responsible for handling events related to the display, and interaction with, the visual game tree.
 *
 * @author Barry Becker
 */
final class GameTreeMotionListener(
    treeViewer: GameTreeViewer,
    boardViewer: TwoPlayerViewModel[?, ?],
    detailsPanel: MoveInfoPanel
) extends MouseMotionListener:

  /** the controller that is actually being played in the normal view. */
  private var mainController: TwoPlayerController[?, ?] = _

  @volatile private var treeViewerField: GameTreeViewer = treeViewer
  private var moveInfoPanel: MoveInfoPanel = detailsPanel

  /** the viewer in the debug window. */
  private var boardViewerField: TwoPlayerViewModel[?, ?] = boardViewer

  private var oldChainLength: Int = 0

  private val SHOW_SUCCESSIVE_MOVES = true

  def setMainController(mainController: TwoPlayerController[?, ?]): Unit =
    this.mainController = mainController

  def mouseDragged(e: MouseEvent): Unit = ()

  override def mouseMoved(e: MouseEvent): Unit =
    selectCallback(e)

  def resetOldChainLength(): Unit =
    oldChainLength = 0

  /**
   * Called when a particular move in the game tree has been selected by the user (by clicking on it or mouse-over).
   */
  private def selectCallback(e: MouseEvent): Unit =
    val tree = e.getSource.asInstanceOf[JTree]

    if mainController.isProcessing then
      // avoid concurrency problems
      return

    val row = tree.getRowForLocation(e.getX, e.getY)
    if row == -1 then return

    val path = tree.getPathForRow(row)
    treeViewerField.highlightPath(path)

    var chainLength = path.getPathCount
    val nodes = path.getPath
    val lastNode = nodes(chainLength - 1).asInstanceOf[SearchTreeNode]

    var moveList: List[TwoPlayerMove] = new LinkedList[TwoPlayerMove]()
    var move: TwoPlayerMove = null
    for i <- 0 until chainLength do
      val node = nodes(i).asInstanceOf[SearchTreeNode]
      move = node.getUserObject.asInstanceOf[TwoPlayerMove]
      assert(move != null)
      moveList.add(move)

    val viewer = boardViewerField.asInstanceOf[AbstractTwoPlayerBoardViewer[?, ?]]
    if SHOW_SUCCESSIVE_MOVES then
      // add expected successive moves to show likely outcome.
      moveList = addSuccessiveMoves(moveList, lastNode)
    GameContext.log(3, "chain len before=" + chainLength + " after=" + moveList.size)
    chainLength = moveList.size
    viewer.asInstanceOf[AbstractTwoPlayerBoardViewer[TwoPlayerMove, ?]]
      .showMoveSequence(moveList, oldChainLength, lastNode.getChildMoves)

    // remember the old chain magnitude so we know how much to back up next time
    oldChainLength = chainLength

    // we should throw an event instead of having this dependency.
    moveInfoPanel.setText(viewer, move, lastNode)

  /**
   * Add to the list all the moves that we expect are most likely to occur given the current game state.
   * This is how the computer expects the game to play out.
   * @return the list of successive moves.
   */
  private def addSuccessiveMoves(moveList: List[TwoPlayerMove], finalNode: SearchTreeNode): List[TwoPlayerMove] =
    var nextNode = finalNode.getExpectedNextNode
    while nextNode != null do
      val m = nextNode.getUserObject.asInstanceOf[Move].copy().asInstanceOf[TwoPlayerMove]
      m.setFuture(true)
      moveList.add(m)
      nextNode = nextNode.getExpectedNextNode
    moveList
