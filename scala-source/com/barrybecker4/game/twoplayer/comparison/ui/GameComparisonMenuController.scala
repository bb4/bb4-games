// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui

import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.plugin.PluginManager
import com.barrybecker4.game.common.ui.menu.GameMenuController
import com.barrybecker4.ui.util.GUIUtil

import javax.swing.{JComponent, JFrame}

/**
 * Listens for menu changes and updates the UI appropriately.
 *
 * @author Barry Becker
 */
class GameComparisonMenuController(frame: JFrame) extends GameMenuController(frame) {

  override def showGame(gameName: String): Unit = {
    GameContext.loadResources(gameName)

    gamePanel_ = PluginManager.getInstance.getPlugin(gameName).getPanelInstance
    gamePanel_.init(frame_)

    frame_.setTitle(gamePanel_.getTitle)
  }

  override def saveImage(): Unit = {
    GUIUtil.saveSnapshot(frame_.getComponent(0).asInstanceOf[JComponent], FileUtil.getHomeDir + "/game") // NON-NLS
  }
}
