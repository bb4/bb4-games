// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui

import com.barrybecker4.game.common.plugin.{GamePlugin, PluginManager, PluginType}
import com.barrybecker4.game.common.ui.menu.GameMenu

import java.util.List => JList

/**
 * The standard main menu for all game programs.
 * Allows such common operations as new, load, save, exit.
 * @author Barry Becker
 */
class GameComparisonMenu(initialGame: String) extends GameMenu(initialGame) {

  /** @return a list of only the two player games */
  override def getPlugins: JList[GamePlugin] =
    PluginManager.getInstance.getPlugins(PluginType.TWO_PLAYER_GAME)
}
