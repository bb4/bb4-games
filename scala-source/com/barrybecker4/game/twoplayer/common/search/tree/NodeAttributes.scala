/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.tree

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.SearchWindow

import java.util.LinkedHashMap

/**
 * Represents a move/node in the game tree.
 * Each SearchTreeNode contains a Move as its userObject.
 * When showing the game tree graphically, these variables can take a lot of space
 * since they are in every node in the tree. Still it is better to have them here than
 * in the move structure so that when we are not in debug mode the space is not used.
 *
 * Extends Java LinkedHashMap so toString matches Java HashMap style (`{k=v, ...}`).
 *
 * @author Barry Becker
 */
class NodeAttributes extends LinkedHashMap[String, String]:

  var pruned: Boolean = false

object NodeAttributes:

  /**
   * Factory method to create a pruned node.
   */
  def createPrunedNode(selectedValue: Int, window: SearchWindow): NodeAttributes =
    val attributes = new NodeAttributes()
    attributes.put("value", FormatUtil.formatNumber(selectedValue))
    attributes.put("window", window.toString)
    attributes.pruned = true
    attributes.put("pruned", "(value outside window)")
    attributes

  /**
   * Factory method to create an inner node.
   */
  def createInnerNode(theMove: TwoPlayerMove, window: SearchWindow): NodeAttributes =
    val attributes = new NodeAttributes()
    attributes.put("value", FormatUtil.formatNumber(theMove.getValue))
    attributes.put("inhVal", FormatUtil.formatNumber(theMove.getInheritedValue))
    attributes.put("window", window.toString)
    attributes
