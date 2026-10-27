/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui

import com.barrybecker4.game.common.ui.panel.GameToolBar
import com.barrybecker4.ui.components.GradientButton
import com.barrybecker4.ui.util.GUIUtil

import java.awt.event.ActionListener
import javax.swing.{ImageIcon, JButton}
import scala.compiletime.uninitialized

class SetToolBar(texture: ImageIcon, listener: ActionListener) extends GameToolBar(texture, listener):

  private var addButton: GradientButton = uninitialized
  private var removeButton: GradientButton = uninitialized
  private var solveButton: GradientButton = uninitialized

  override def addCustomToolBarButtons(): Unit =
    addButton = createToolBarButton("Add Card", "Add another card to those shown", SetToolBar.addImage)
    removeButton = createToolBarButton("Remove Card", "Remove a card from those shown", SetToolBar.removeImage)
    solveButton = createToolBarButton(
      "Show Sets",
      "Have the computer determine all the sets present and show them in a separate window.",
      SetToolBar.solveImage)
    add(addButton)
    add(removeButton)
    add(solveButton)

  override def hasUndoRedo(): Boolean = false

  def getAddButton: JButton = addButton
  def getRemoveButton: JButton = removeButton
  def getSolveButton: JButton = solveButton

object SetToolBar:
  private val addImage = GUIUtil.getIcon(GameToolBar.DIR + "plus.gif")
  private val removeImage = GUIUtil.getIcon(GameToolBar.DIR + "minus.gif")
  private val solveImage = GUIUtil.getIcon(GameToolBar.DIR + "greenFlag.gif")
