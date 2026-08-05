// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.analysis

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition, Homes}
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.board.path.{Path, PathList}
import javax.swing.tree.{DefaultMutableTreeNode, MutableTreeNode}
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

class ShortestPathFinder(private val board: BlockadeBoard):
  
  private val moveAnalyzer = PossibleMoveAnalyzer(board)

  def findShortestPaths(position: BlockadeBoardPosition): PathList =
    val opponentIsPlayer1 = !position.getPiece.isOwnedByPlayer1
    val homeSet = mutable.LinkedHashSet[MutableTreeNode]()
    position.setVisited(true)

    val queue = mutable.ListBuffer[DefaultMutableTreeNode]()
    val root = DefaultMutableTreeNode(null)

    if position.isHomeBase(opponentIsPlayer1) then
      homeSet.add(root)
    else
      queue.addAll(findPathChildren(position, root, opponentIsPlayer1))

      while homeSet.size < Homes.NUM_HOMES && queue.nonEmpty do
        val node = queue.remove(0)
        val nodeMove = node.getUserObject.asInstanceOf[BlockadeMove]
        val toPosition = board.getPosition(nodeMove.getToRow, nodeMove.getToCol)
        
        if !toPosition.isVisited then
          toPosition.setVisited(true)
          val parentNode = node.getParent.asInstanceOf[MutableTreeNode]
          node.setParent(null)
          parentNode.insert(node, parentNode.getChildCount)
          
          if toPosition.isHomeBase(opponentIsPlayer1) then
            homeSet.add(node)
          
          val children = findPathChildren(toPosition, node, opponentIsPlayer1)
          queue.addAll(children)

    val paths = extractPaths(homeSet)
    unvisitAll()
    paths

  private def findPathChildren(
      pos: BlockadeBoardPosition,
      parent: MutableTreeNode,
      oppPlayer1: Boolean
  ): List[DefaultMutableTreeNode] =
    val moves = moveAnalyzer.getPossibleMoveList(pos, oppPlayer1).asScala
    val children = mutable.ListBuffer[DefaultMutableTreeNode]()

    for move <- moves do
      val childNode = DefaultMutableTreeNode(move)
      childNode.setParent(parent)
      children.addOne(childNode)

    children.toList

  private def unvisitAll(): Unit =
    for i <- 1 to board.getNumRows; j <- 1 to board.getNumCols do
      board.getPosition(i, j).setVisited(false)

  private def extractPaths(homeSet: mutable.LinkedHashSet[MutableTreeNode]): PathList =
    val paths = PathList()

    for home <- homeSet do
      val node = home.asInstanceOf[DefaultMutableTreeNode]
      val path = Path(node)
      if path.getLength == 0 then
        GameContext.log(2, s"found 0 magnitude path = $path for home $node")
      paths.add(path)

    paths
