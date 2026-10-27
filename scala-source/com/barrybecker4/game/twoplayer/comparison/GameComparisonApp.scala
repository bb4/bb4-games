// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison

import com.barrybecker4.common.app.CommandLineOptions
import com.barrybecker4.common.i18n.LocaleType
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.ui.menu.FileMenu
import com.barrybecker4.game.twoplayer.comparison.ui.{GameComparisonMenu, GameComparisonMenuController, GameComparisonPanel}
import com.barrybecker4.ui.util.GUIUtil

import javax.swing.{JFrame, JMenuBar, WindowConstants}

/**
 * This application allows you to run any of the two player games
 * against each other using different search strategies.
 * This can work well for integration testing and performance testing of
 * the different search strategies when applied to all the different two player games.
 *
 * @author Barry Becker
 */
class GameComparisonApp private (initialGame: String) {

  private val WIDTH = 1200
  private val HEIGHT = 900

  private val frame_ = new JFrame()
  private val gameComparisonPanel = new GameComparisonPanel()

  GUIUtil.setCustomLookAndFeel()

  frame_.setBounds(200, 200, WIDTH, HEIGHT)
  frame_.getContentPane.add(gameComparisonPanel)
  addMenuBar(initialGame)

  frame_.setVisible(true)
  frame_.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)

  private def addMenuBar(initialGame: String): Unit = {
    val menuController = new GameComparisonMenuController(frame_)
    val gameMenu = new GameComparisonMenu(initialGame)
    gameMenu.addListener(menuController)
    gameMenu.addListener(gameComparisonPanel.getGridPanel)

    val fileMenu = new FileMenu(menuController)

    val menuBar = new JMenuBar()
    menuBar.add(fileMenu)
    menuBar.add(gameMenu)

    frame_.getRootPane.setJMenuBar(menuBar)
  }
}

object GameComparisonApp {

  private val DEFAULT_GAME = "gomoku"

  def main(args: Array[String]): Unit = {
    val defaultGame = DEFAULT_GAME
    var gameName = defaultGame
    if (args.length == 1) {
      gameName = args(0)
    } else if (args.length > 1) {
      val options = new CommandLineOptions(args)

      if (options.contains("help")) {
        GameContext.log(0, "Usage: -name <game> [-locale <locale>]")
      }
      gameName = options.getValueForOption("name", defaultGame)

      if (options.contains("locale")) {
        val localeName = options.getValueForOption("locale", "ENGLISH")
        val locale = GameContext.getLocale(localeName, true)
        GameContext.setLocale(locale)
      }
    }

    new GameComparisonApp(gameName)
  }
}
