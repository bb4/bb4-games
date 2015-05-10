/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.plugin;

import com.barrybecker4.common.util.FileUtil;
import com.barrybecker4.common.xml.DomUtil;
import com.barrybecker4.game.common.GameContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton class for loading and managing the GamePlugins.
 *
 * @author Barry Becker Date: Jan 20, 2007
 */
@SuppressWarnings("HardCodedStringLiteral")
public class PluginManager {

    private static final String PLUGINS_FILE = GameContext.GAME_RESOURCE_ROOT + "plugins.xml";

    private static PluginManager manager_ = null;
    private List<GamePlugin> plugins_;
    /** mapping from the name to the plugin */
    private Map<String, GamePlugin> hmNameToPlugin_;
    /** mapping from the label to the plugin */
    private Map<String, GamePlugin> hmLabelToPlugin_;
    private GamePlugin defaultGame_;

    /**
     * Load plugin games from plugins.xml
     */
    private PluginManager() {

        URL url = FileUtil.getURL(PLUGINS_FILE);
        GameContext.log(0,
            "about to parse url=" + url +"\n plugin file location=" + PLUGINS_FILE);
        Document xmlDocument = DomUtil.parseXML(url);

        initializePlugins(xmlDocument);
        GameContext.log(0,
            "plugin file parsed");
    }

    /**
     * Get the plugins from the xml document
     * @param document parsed xml from the plugins.xml file.
     */
    private void initializePlugins(Document document) {
        Node root = document.getDocumentElement();    // games element
        NodeList children = root.getChildNodes();
        plugins_ = new ArrayList<>();
        hmNameToPlugin_ = new HashMap<>();
        hmLabelToPlugin_ = new HashMap<>();
        GamePlugin defaultGame = null;

        int num = children.getLength();
        for (int i = 0; i < num; i++) {

            Node n = children.item(i);

            if (!"#comment".equals(n.getNodeName())) {

                GamePlugin plugin = createPlugin(n);

                plugins_.add(plugin);
                hmNameToPlugin_.put(plugin.getName(), plugin);
                hmLabelToPlugin_.put(plugin.getLabel(), plugin);
                if (plugin.isDefault()) {
                    defaultGame = plugin;
                }
            }
        }
        if (defaultGame == null) {
            defaultGame = plugins_.get(0);
        }
        defaultGame_ = defaultGame;
    }

    /**
     * @param node node to create game plugin based off of.
     * @return a plugin object that we created from the xml node.
     */
    private GamePlugin createPlugin(Node node) {
        String name = DomUtil.getAttribute(node, "name");
        String msgKey = DomUtil.getAttribute(node, "msgKey");
        String typeStr = DomUtil.getAttribute(node, "type");
        PluginType type = PluginType.valueOf(typeStr);
        String msgBundleBase = DomUtil.getAttribute(node, "msgBundleBase");

        String label = GameContext.getLabel(msgKey);

        String panelClass =  DomUtil.getAttribute(node, "panelClass");
        String controllerClass =  DomUtil.getAttribute(node, "controllerClass");
        String def = DomUtil.getAttribute(node, "default", "false");
        boolean isDefault = Boolean.parseBoolean(def);
        return new GamePlugin(name, label, type, msgBundleBase,
                              panelClass, controllerClass, isDefault);
    }

    public static PluginManager getInstance() {
        if (manager_ == null) {
            manager_ = new PluginManager();
        }
        return manager_;
    }

    public List<GamePlugin> getPlugins() {
        return plugins_;
    }

    /**
     * @param type type of plugins to get.
     * @return a list of plugins filtered by the specified type.
     */
    public List<GamePlugin> getPlugins(PluginType type) {
        List<GamePlugin> plugins = new ArrayList<>();
        for (GamePlugin plugin : plugins_) {
            if (plugin.getType() == type)  {
                plugins.add(plugin);
            }
        }
        return plugins;
    }

    public GamePlugin getPlugin(String name) {
        return hmNameToPlugin_.get(name);
    }

    public GamePlugin getPluginFromLabel(String name) {
        return hmLabelToPlugin_.get(name);
    }

    public GamePlugin getDefaultPlugin() {
       return defaultGame_;
    }
}
