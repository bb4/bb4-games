/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.panel

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.ui.components.{GradientButton, TexturedToolBar}
import com.barrybecker4.ui.util.GUIUtil
import javax.swing._
import java.awt.event.ActionListener

/**
 * Toolbar that appears a the top of the game application window.
 * Offers standard actions for all games, but can be given game specific buttons too.
 * @author Barry Becker
 */
class GameToolBar(texture: ImageIcon, listener: ActionListener) extends TexturedToolBar(texture, listener) {

  private var newGameButton_ : GradientButton = scala.compiletime.uninitialized
  private var undoButton_ : GradientButton = scala.compiletime.uninitialized
  private var redoButton_ : GradientButton = scala.compiletime.uninitialized
  private var optionsButton_ : GradientButton = scala.compiletime.uninitialized
  private var helpButton_ : GradientButton = scala.compiletime.uninitialized

  init()

  private def init(): Unit = {
    newGameButton_ = createToolBarButton(GameContext.getLabel("NEW_GAME_BTN"),
      GameContext.getLabel("NEW_GAME_BTN_TIP"), GameToolBar.newGameImage)
    
    undoButton_ = createToolBarButton("", GameContext.getLabel("UNDO_BTN_TIP"), GameToolBar.undoImage)
    undoButton_.setDisabledIcon(GameToolBar.undoImageDisabled)
    undoButton_.setEnabled(false)
    
    redoButton_ = createToolBarButton("", GameContext.getLabel("REDO_BTN_TIP"), GameToolBar.redoImage)
    redoButton_.setDisabledIcon(GameToolBar.redoImageDisabled)
    redoButton_.setEnabled(false)
    
    optionsButton_ = createToolBarButton(GameContext.getLabel("OPTIONS_BTN"),
      GameContext.getLabel("OPTIONS_BTN_TIP"), GameToolBar.optionsImage)
    
    helpButton_ = createToolBarButton(GameContext.getLabel("HELP_BTN"),
      GameContext.getLabel("HELP_BTN_TIP"), GameToolBar.helpImage)

    add(newGameButton_)
    if (hasUndoRedo()) {
      add(undoButton_)
      add(redoButton_)
    }
    addCustomToolBarButtons()
    add(optionsButton_)
    add(Box.createHorizontalGlue())
    add(helpButton_)
  }

  /**
   * override to add your own game dependent buttons to the toolbar.
   */
  def addCustomToolBarButtons(): Unit = {}

  def getNewGameButton: JButton = newGameButton_
  def getUndoButton: JButton = undoButton_
  def getRedoButton: JButton = redoButton_
  def getOptionsButton: JButton = optionsButton_
  def getHelpButton: JButton = helpButton_

  def hasUndoRedo(): Boolean = true
}

object GameToolBar {
  private val CORE_IMAGE_PATH = GameContext.GAME_RESOURCE_ROOT + "common/ui/images/"
  val DIR: String = CORE_IMAGE_PATH
  
  private val newGameImage = GUIUtil.getIcon(DIR + "newGame.gif")
  private val helpImage = GUIUtil.getIcon(DIR + "help.gif")
  private val undoImage = GUIUtil.getIcon(DIR + "undo_on.gif")
  private val redoImage = GUIUtil.getIcon(DIR + "redo_on.gif")
  private val undoImageDisabled = GUIUtil.getIcon(DIR + "undo_off.gif")
  private val redoImageDisabled = GUIUtil.getIcon(DIR + "redo_off.gif")
  private val optionsImage = GUIUtil.getIcon(DIR + "iconDesktop.gif")
}
