// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison;

import com.barrybecker4.common.app.CommandLineOptions;
import com.barrybecker4.common.i18n.LocaleType;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.ui.menu.FileMenu;
import com.barrybecker4.game.twoplayer.comparison.ui.GameComparisonMenu;
import com.barrybecker4.game.twoplayer.comparison.ui.GameComparisonMenuController;
import com.barrybecker4.game.twoplayer.comparison.ui.GameComparisonPanel;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;

/**
 * This application allows you to run any of the two player games
 * against each other using different search strategies.
 * This can work well for integration testing and performance testing of
 * the different search strategies when applied to all the different two player games.
 *
 * @author Barry Becker
 */
public class GameComparisonApp {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 900;
    private static final String DEFAULT_GAME = "pente";

    private JFrame frame_;
    private GameComparisonPanel gameComparisonPanel;


    /**
     * Game application constructor.
     * @param initialGame the initial game to show.
     */
    private GameComparisonApp(String initialGame) {

        GUIUtil.setCustomLookAndFeel();

        frame_ = new JFrame();

        frame_.setBounds(200, 200, WIDTH, HEIGHT);
        gameComparisonPanel = new GameComparisonPanel();
        frame_.getContentPane().add(gameComparisonPanel);
        addMenuBar(initialGame);

        // display the frame
        frame_.setVisible(true);
        frame_.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * Add a top level menu to allow changing to a different game from the one currently displayed.
     */
    private void addMenuBar(String initialGame) {
        GameComparisonMenuController menuController = new GameComparisonMenuController(frame_);
        GameComparisonMenu gameMenu = new GameComparisonMenu(initialGame);
        gameMenu.addListener(menuController);
        gameMenu.addListener(gameComparisonPanel.getGridPanel());

        JMenu fileMenu = new FileMenu(menuController);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(gameMenu);

        frame_.getRootPane().setJMenuBar(menuBar);
    }

    /**
     * Static method to start up the game playing application.
     * The arguments allowed are :
     *  gameName : one of the supported games (eg "go", "checkers", "pente", etc).
     *      If unspecified, the default is DEFAULT_GAME.
     *  locale : The locale (language) to run in. If unspecified, the locale will be "ENGLISH".
     *
     * @param args optionally the game to play and or the locale
     */
    public static void main(String[] args) {

        String defaultGame = DEFAULT_GAME;
        String gameName = defaultGame;
        if (args.length == 1) {
            // if there is only one arg assume it is the name of the game
            gameName = args[0];
        }
        else if (args.length > 1) {
            CommandLineOptions options = new CommandLineOptions(args);

            if (options.contains("help")) {
                GameContext.log(0, "Usage: -name <game> [-locale <locale>]");
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

        new GameComparisonApp(gameName);
    }
}
