/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.menu

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.plugin.{GamePlugin, PluginManager}

import javax.swing.JMenuItem
import java.awt.event.{ActionEvent, ActionListener}
import scala.jdk.CollectionConverters._

/**
 * The standard main menu for all game programs.
 * Allows such common operations as new, load, save, exit.
 * @author Barry Becker
 */
class GameMenu(initialGame: String) extends AbstractGameMenu(GameContext.getLabel("GAME")) with ActionListener {

  getPlugins.asScala.foreach { gamePlugin =>
    val gameNameLabel = gamePlugin.getLabel
    add(createMenuItem(gameNameLabel))
  }

  currentGameName = initialGame

  def getPlugins: java.util.List[GamePlugin] =
    PluginManager.getInstance.getPlugins

  /**
   * called when the user has selected a different game to play from the game menu
   */
  override def actionPerformed(e: ActionEvent): Unit = {
    val item = e.getSource.asInstanceOf[JMenuItem]

    currentGameName =
      PluginManager.getInstance.getPluginFromLabel(item.getText).getName
    System.out.println("the current game is " + currentGameName)

    notifyOfChange(currentGameName)
  }
}
