// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.menu

import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.plugin.PluginManager
import com.barrybecker4.game.common.ui.panel.IGamePanel
import com.barrybecker4.ui.util.GUIUtil

import javax.swing.{JComponent, JFrame}

/**
 * Listens for menu changes and updates the UI appropriately.
 *
 * @author Barry Becker
 */
class GameMenuController(val frame_ : JFrame) extends GameMenuListener with FileMenuListener {

  var gamePanel_ : IGamePanel = null

  override def gameChanged(gameName: String): Unit = {
    showGame(gameName)
  }

  override def openFile(): Unit = {
    gamePanel_.openGame()
  }

  override def saveFile(): Unit = {
    gamePanel_.saveGame()
  }

  override def saveImage(): Unit = {
    val comp = getGameComponent
    GUIUtil.saveSnapshot(comp, FileUtil.getHomeDir)
  }

  def getGameComponent: JComponent =
    gamePanel_.asInstanceOf[JComponent]

  /**
   * Show the game panel for the specified game
   * @param gameName name of the game to show in the frame.
   */
  def showGame(gameName: String): Unit = {
    // this will load the resources for the specified game.
    GameContext.loadResources(gameName)

    if (gamePanel_ != null) {
      GameContext.log(1, "game comp = " + getGameComponent) //NON-NLS
      frame_.getContentPane.remove(getGameComponent)
    }

    gamePanel_ = PluginManager.getInstance.getPlugin(gameName).getPanelInstance
    gamePanel_.init(frame_)

    frame_.getContentPane.add(getGameComponent)
    frame_.setTitle(gamePanel_.getTitle)
    frame_.setVisible(true)
  }
}
