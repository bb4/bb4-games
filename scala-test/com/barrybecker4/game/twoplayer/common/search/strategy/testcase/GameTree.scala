package com.barrybecker4.game.twoplayer.common.search.strategy.testcase

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.common.xml.DomUtil
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub
import org.w3c.dom.Node

import scala.jdk.CollectionConverters.*

/**
 * @author Barry Becker
 */
class GameTree(rootNode: Node):

  private var moveCount = 0
  private val root: TwoPlayerMoveStub = createTreeFromNode(rootNode, null)

  /**
   * Create the tree rooted at node recursively.
   * @param node current root
   * @param parent parent of the node
   * @return the new move node (with children if any)
   */
  private def createTreeFromNode(node: Node, parent: TwoPlayerMoveStub): TwoPlayerMoveStub =
    val isPlayer1 = parent == null || !parent.isPlayer1
    val current = createMoveFromNode(node, isPlayer1, parent)
    val children = node.getChildNodes
    var i = 0
    while i < children.getLength do
      createTreeFromNode(children.item(i), current)
      i += 1
    current

  /**
   * Make a clone of the game tree with the specified player at the root
   * @param player1 if true, then player1 went last (it is their move at the root), else player2 went last
   */
  def getCloneWithRootPlayer1(player1: Boolean): TwoPlayerMoveStub =
    getCloneWithRootPlayer1(player1, getInitialMove, null)

  private def getCloneWithRootPlayer1(
      player1: Boolean,
      parent: TwoPlayerMoveStub,
      grandParent: TwoPlayerMoveStub
  ): TwoPlayerMoveStub =
    val newParent = new TwoPlayerMoveStub(
      parent.getValue,
      player1,
      parent.getToLocation,
      parent.isUrgent,
      parent.causedUrgency,
      grandParent
    )
    for child <- parent.getChildren.asScala do
      getCloneWithRootPlayer1(!player1, child.asInstanceOf[TwoPlayerMoveStub], newParent)
    newParent

  /** @return the root move in the game tree. */
  def getInitialMove: TwoPlayerMoveStub = root

  /** Print the tree in depth first search for debugging purposes */
  def print(): Unit = print(root, "")

  /** Print the tree in depth first search for debugging purposes */
  def print(subtree: TwoPlayerMoveStub): Unit = print(subtree, "")

  private def print(moveNode: TwoPlayerMoveStub, indent: String): Unit =
    System.out.println(indent + "Move: " + moveNode)
    for move <- moveNode.getChildren.asScala do
      print(move.asInstanceOf[TwoPlayerMoveStub], indent + "  ")

  private def createMoveFromNode(node: Node, isPlayer1: Boolean, parent: TwoPlayerMoveStub): TwoPlayerMoveStub =
    val value = Integer.parseInt(DomUtil.getAttribute(node, "value"))
    val move = new TwoPlayerMoveStub(value, isPlayer1, createToLocation(), parent)
    move.setCausesUrgency(java.lang.Boolean.parseBoolean(DomUtil.getAttribute(node, "causesUrgency", "false")))
    move.setUrgent(java.lang.Boolean.parseBoolean(DomUtil.getAttribute(node, "urgent", "false")))
    move

  /**
   * The location is not really used, just give it something unique so the hash works.
   * @return new to location
   */
  private def createToLocation(): Location =
    moveCount += 1
    new ByteLocation(moveCount / GameTree.FAKE_BOARD_SIZE, moveCount % GameTree.FAKE_BOARD_SIZE)

object GameTree:
  private val FAKE_BOARD_SIZE = 19
