/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.plugin;

import com.barrybecker4.common.app.ClassLoaderSingleton;
import com.barrybecker4.game.common.ui.panel.GamePanel;

/**
 * Immutable class representing information info about a game plugin.
 * There is a game plugin for each game that we support.
 * see plugins.xml
 *
 * @author Barry Becker
 */
public class GamePlugin {

    private final String name;
    private final String label;
    private final PluginType type;
    private final String msgBundleBase;
    private final String panelClass;
    private final String controllerClass;
    private final boolean isDefault;

    /**
     * Constructor
     * @param name of the plugin game
     * @param label user visible title for the game
     * @param type one of the PluginTypes.
     * @param msgBundleBase place to get localized strings from for this game.
     * @param panelClass name of the panel class to load with reflection.
     * @param controllerClass name of the controller class to load with reflection.
     * @param isDefault if true, show this game initially.
     */
    public GamePlugin(String name, String label, PluginType type, String msgBundleBase,
                      String panelClass, String controllerClass, boolean isDefault) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.msgBundleBase = msgBundleBase;
        this.panelClass = panelClass;
        this.controllerClass = controllerClass;
        this.isDefault = isDefault;
    }


    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public PluginType getType() {
        return type;
    }

    public String getMsgBundleBase() {
        return msgBundleBase;
    }

    private String getPanelClass() {
        return panelClass;
    }

    public GamePanel getPanelInstance() {

       Class gameClass = ClassLoaderSingleton.loadClass(getPanelClass());

       GamePanel gamePanel = null;
        try {
            gamePanel = (GamePanel)gameClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gamePanel;
    }

    public String getControllerClass() {
        return controllerClass;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @SuppressWarnings("HardCodedStringLiteral")
    @Override
    public String toString() {
        return name + '(' + getLabel() + "panelClass=" + panelClass + ' '
                + "controllerClass=" + controllerClass + ")\n";
    }

}
