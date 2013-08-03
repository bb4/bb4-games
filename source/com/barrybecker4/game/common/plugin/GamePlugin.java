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

    private final String name_;
    private final String label_;
    private final PluginType type_;
    private final String msgBundleBase_;
    private final String panelClass_;
    private final String controllerClass_;
    private final boolean isDefault_;

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
        name_ = name;
        label_ = label;
        type_ = type;
        msgBundleBase_ = msgBundleBase;
        panelClass_ = panelClass;
        controllerClass_ = controllerClass;
        isDefault_ = isDefault;
    }


    public String getName() {
        return name_;
    }

    public String getLabel() {
        return label_;
    }

    public PluginType getType() {
        return type_;
    }

    public String getMsgBundleBase() {
        return msgBundleBase_;
    }

    private String getPanelClass() {
        return panelClass_;
    }

    public GamePanel getPanelInstance() {

       Class gameClass = ClassLoaderSingleton.loadClass(getPanelClass());

       GamePanel gamePanel = null;
        try {
            gamePanel = (GamePanel)gameClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gamePanel;
    }

    public String getControllerClass() {
        return controllerClass_;
    }

    public boolean isDefault() {
        return isDefault_;
    }

    @SuppressWarnings("HardCodedStringLiteral")
    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder(name_);
        bldr.append('(').append(getLabel());
        bldr.append("panelClass=").append(panelClass_).append(' ');
        bldr.append("controllerClass=").append(controllerClass_).append(")\n");
        return bldr.toString();
    }

}
