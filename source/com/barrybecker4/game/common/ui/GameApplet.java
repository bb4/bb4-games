/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui;

import com.barrybecker4.ui.application.MyJApplet;
import com.barrybecker4.common.i18n.LocaleType;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.plugin.PluginManager;
import com.barrybecker4.game.common.ui.panel.GamePanel;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * This is the applet wrapper for the game programs.
 * It contains a PlayerPanel corresponding to the specified game.
 *
 * @see GamePanel
 * @author Barry Becker
 */
public class GameApplet extends MyJApplet {

    private GamePanel gamePanel = null;

    private static final long serialVersionUID = 0L;
    private static final Dimension INITIAL_SIZE =  new Dimension(600, 500);

    @Override
    public void init() {
        GUIUtil.setCustomLookAndFeel();

        String gameName = getParameter("name");
        String localeName = getParameter("locale");

        if (localeName == null) {
            localeName = "ENGLISH";
        }
        if (gameName == null) {
            gameName = "gomoku";
        }

        LocaleType locale = GameContext.getLocale(localeName, true);
        GameContext.log(0, "Game: " + gameName);
        GameContext.log(0, "setting the locale to " + locale + " for language=" + localeName);  // NON-NLS

        GameContext.loadResources(gameName);
        GameContext.setLocale(locale);

        gamePanel = PluginManager.getInstance().getPlugin(gameName).getPanelInstance();
        gamePanel.init(null);   // applet has no frame.

        gamePanel.setSize(INITIAL_SIZE);
        this.getContentPane().add(gamePanel);
        GameContext.log(0, "Done applet init");
    }

    /**
     * This method allow javascript to resize the applet from the browser.
     * Usually applets are not resizable within a web page, but this is a neat trick that allows you to do it.
     */
    @Override
    public void setSize( int width, int height )  {

        GameContext.log(0, "in setSize w="+width+" h="+height);  // NON-NLS
        gamePanel.setSize( width, height );
    }
}
