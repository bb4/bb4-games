/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui;

import com.barrybecker4.common.app.CommandLineOptions;
import com.barrybecker4.common.i18n.LocaleType;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.plugin.PluginManager;
import com.barrybecker4.game.common.ui.menu.FileMenu;
import com.barrybecker4.game.common.ui.menu.GameMenu;
import com.barrybecker4.game.common.ui.menu.GameMenuController;
import com.barrybecker4.game.common.ui.panel.GamePanel;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;

/**
 * This is the application frame wrapper for the game programs.
 * It contains a GamePanel corresponding to the game you have selected to play.
 * If you specify a game class as an argument, then you do not get a menu of all possible games to play.
 *
 * @see GamePanel
 * @author Barry Becker
 */
public class GameApp {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;

    private JFrame frame_;

    /**
     * Game application constructor.
     * @param initialGame the initial game to show.
     */
    private GameApp(String initialGame) {

        GUIUtil.setCustomLookAndFeel();

        frame_ = new JFrame();
        addMenuBar(initialGame);

        frame_.setBounds(200, 200, WIDTH, HEIGHT);
        // display the frame
        frame_.setVisible(true);
        frame_.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * Add a top level menu to allow changing to a different game from the one currently displayed.
     */
    private void addMenuBar(String initialGame) {

        GameMenuController menuListener = new GameMenuController(frame_);

        GameMenu gameMenu = new GameMenu(initialGame);
        gameMenu.addListener(menuListener);

        JMenu fileMenu = new FileMenu(menuListener);

        JMenuBar menubar = new JMenuBar();
        menubar.add(fileMenu);
        menubar.add(gameMenu);

        frame_.getRootPane().setJMenuBar(menubar);
    }

    /**
     * Static method to start up the game playing application.
     * The arguments allowed are :
     *  gameName : one of the supported games (eg "go", "checkers", "pente", etc).
     *      If unspecified, the default specified in plugins.xml is used.
     *  locale : The locale (language) to run in. If unspecified, the locale will be "ENGLISH".
     *
     * @param args optionally the game to play and or the locale
     */
    public static void main(String[] args) {

        String defaultGame = PluginManager.getInstance().getDefaultPlugin().getName();
        String gameName = defaultGame;
        if (args.length == 1) {
            // if there is only one arg assume it is the name of the game
            gameName = args[0];
        }
        else if (args.length > 1) {
            CommandLineOptions options = new CommandLineOptions(args);

            if (options.contains("help")) {     // NON-NLS
                GameContext.log(0, "Usage: -name <game> [-locale <locale>]"); // NON_NLS
            }
            // create a game panel of the appropriate type based on the name of the class passed in.
            // if no game is specified as an argument, then we show a menu for selecting a game
            gameName = options.getValueForOption("name", defaultGame);

            if (options.contains("locale")) {
                // then a locale has been specified
                String localeName = options.getValueForOption("locale", "ENGLISH");
                LocaleType locale = GameContext.getLocale(localeName, true);
                GameContext.setLocale(locale);
            }
        }

        new GameApp(gameName);
    }

}
