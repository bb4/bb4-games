// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui;

import com.barrybecker4.common.util.FileUtil;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.plugin.PluginManager;
import com.barrybecker4.game.common.ui.menu.GameMenuController;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;

/**
 * Listens for menu changes and updates the UI appropriately.
 *
 * @author Barry Becker
 */
public class GameComparisonMenuController extends GameMenuController {

    /**
     * GConstructor.
     */
    public GameComparisonMenuController(JFrame frame) {
        super(frame);
    }

    /**
     * Show the game panel for the specified game
     * @param gameName name of the game to show in the frame.
     */
    @Override
    protected void showGame(String gameName) {

        // this will load the resources for the specified game.
        GameContext.loadResources(gameName);

        gamePanel_ = PluginManager.getInstance().getPlugin(gameName).getPanelInstance();
        gamePanel_.init(frame_);

        frame_.setTitle(gamePanel_.getTitle());
    }

    @Override
    public void saveImage() {
        GUIUtil.saveSnapshot((JComponent)frame_.getComponent(0), FileUtil.getHomeDir() + "/game");  // NON-NLS
    }
}
