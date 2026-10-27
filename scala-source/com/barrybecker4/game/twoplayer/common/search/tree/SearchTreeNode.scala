/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.tree

import com.barrybecker4.game.twoplayer.common.TwoPlayerMove

import javax.swing.tree.DefaultMutableTreeNode
import java.awt.Point
import java.util.Enumeration
import scala.jdk.CollectionConverters.*

/**
 * Represents a move/node in the game tree.
 * Each SearchTreeNode contains a Move as its userObject.
 * When showing the game tree graphically, these variables can take a lot of space
 * since they are in every node in the tree. Still it is better to have them here than
 * in the move structure so that when we are not in debug mode the space is not used.
 *
 * @author Barry Becker
 */
class SearchTreeNode(move: TwoPlayerMove, var attributes: NodeAttributes) 
    extends DefaultMutableTreeNode(move):

  /**
   * Used to layout the tree. Roughly based on the num descendants.
   * initialized by GameTreeViewer.
   */
  private var spaceAllocation: Int = 0

  /**
   * location in the board viewer
   */
  private var position: Point = scala.compiletime.uninitialized

  /**
   * Default Constructor
   * @param move a twoplayer board move.
   */
  def this(move: TwoPlayerMove) =
    this(move, new NodeAttributes())

  def getChildMoves: Array[TwoPlayerMove] =
    if children == null then null
    else
      val moves = new Array[TwoPlayerMove](children.size)
      val enumeration = children()
      var i = 0
      while enumeration.hasMoreElements do
        val node = enumeration.nextElement().asInstanceOf[SearchTreeNode]
        moves(i) = node.getUserObject.asInstanceOf[TwoPlayerMove]
        i += 1
      moves

  /**
   * Show nodes corresponding to pruned branches in the game tree (if one is used).
   *
   * @param list list of moves that resulted in pruned branches.
   * @param i th child.
   * @param attributes list of name values to show.
   */
  def addPrunedChildNodes(list: java.util.List[?], i: Int, attributes: NodeAttributes): Unit =
    var index = i
    val listCopy = new java.util.ArrayList(list)
    while !listCopy.isEmpty do
      val theMove = listCopy.remove(0).asInstanceOf[TwoPlayerMove]
      val child = new SearchTreeNode(theMove, attributes)
      this.insert(child, index)
      index += 1

  /**
   * @return the move that the computer expects will be played next
   */
  def getExpectedNextNode: SearchTreeNode =
    if children == null then null
    else
      val enumeration = children()
      while enumeration.hasMoreElements do
        val node = enumeration.nextElement().asInstanceOf[SearchTreeNode]
        val m = node.getUserObject.asInstanceOf[TwoPlayerMove]
        if m.isSelected then
          return node
      null

  /**
   * One of our existing children must be the next node.
   * It must never be null, but it may not be there yet because they are added in a different thread
   * So we block until there.
   * @param move the move whose child moves we will search.
   * @return the next child node to use as parent. Must never be null.
   */
  def findChild(move: TwoPlayerMove): SearchTreeNode =
    val enumeration = this.children()
    while enumeration.hasMoreElements do
      val node = enumeration.nextElement().asInstanceOf[SearchTreeNode]
      if move.equals(node.getUserObject) then
        return node
    null

  /**
   * Serialize all the children.
   * @return children represented in string form
   */
  private def childrenAsString: String =
    val bld = new StringBuilder
    val enumeration = this.children()
    while enumeration.hasMoreElements do
      val node = enumeration.nextElement().asInstanceOf[SearchTreeNode]
      bld.append(node)
    bld.toString

  def isPruned: Boolean = attributes.pruned

  override def toString: String =
    val m = getUserObject
    if m == null then null
    else
      val s = new StringBuilder(m.toString)
      s.append(attributes.toString)
      s.toString

  def getSpaceAllocation: Int = spaceAllocation

  def setSpaceAllocation(allocation: Int): Unit =
    this.spaceAllocation = allocation

  def getPosition: Point = position

  def setLocation(x: Int, y: Int): Unit =
    position = new Point(x, y)
