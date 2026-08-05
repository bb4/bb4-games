/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui

import com.barrybecker4.common.app.CommandLineOptions
import com.barrybecker4.common.i18n.LocaleType
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.plugin.PluginManager
import com.barrybecker4.game.common.ui.menu.{FileMenu, GameMenu, GameMenuController}
import com.barrybecker4.ui.util.GUIUtil

import javax.swing.{JFrame, JMenuBar, WindowConstants}

/**
 * This is the application frame wrapper for the game programs.
 * It contains a GamePanel corresponding to the game you have selected to play.
 * If you specify a game class as an argument, then you do not get a menu of all possible games to play.
 *
 * @see GamePanel
 * @author Barry Becker
 */
class GameApp private (initialGame: String) {

  private val WIDTH = 600
  private val HEIGHT = 600

  private val frame = new JFrame()

  GUIUtil.setCustomLookAndFeel()

  addMenuBar(initialGame)

  frame.setBounds(200, 200, WIDTH, HEIGHT)
  // display the frame
  frame.setVisible(true)
  frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)

  /**
   * Add a top level menu to allow changing to a different game from the one currently displayed.
   */
  private def addMenuBar(initialGame: String): Unit = {
    val menuListener = new GameMenuController(frame)

    val gameMenu = new GameMenu(initialGame)
    gameMenu.addListener(menuListener)

    val fileMenu = new FileMenu(menuListener)

    val menubar = new JMenuBar()
    menubar.add(fileMenu)
    menubar.add(gameMenu)

    frame.getRootPane.setJMenuBar(menubar)
  }
}

object GameApp {

  /**
   * Static method to start up the game playing application.
   * The arguments allowed are :
   *  gameName : one of the supported games (eg "go", "checkers", "gomoku", etc).
   *      If unspecified, the default specified in plugins.xml is used.
   *  locale : The locale (language) to run in. If unspecified, the locale will be "ENGLISH".
   *
   * @param args optionally the game to play and or the locale
   */
  def main(args: Array[String]): Unit = {
    val defaultGame = PluginManager.getInstance.getDefaultPlugin.getName
    var gameName = defaultGame
    if (args.length == 1) {
      // if there is only one arg assume it is the name of the game
      gameName = args(0)
    } else if (args.length > 1) {
      val options = new CommandLineOptions(args)

      if (options.contains("help")) { // NON-NLS
        GameContext.log(0, "Usage: -name <game> [-locale <locale>]") // NON_NLS
      }
      // create a game panel of the appropriate type based on the name of the class passed in.
      // if no game is specified as an argument, then we show a menu for selecting a game
      gameName = options.getValueForOption("name", defaultGame)

      if (options.contains("locale")) {
        // then a locale has been specified
        val localeName = options.getValueForOption("locale", "ENGLISH")
        val locale = GameContext.getLocale(localeName, true)
        GameContext.setLocale(locale)
      }
    }

    new GameApp(gameName)
  }
}
