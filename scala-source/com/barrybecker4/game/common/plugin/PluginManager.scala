/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.plugin

import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.common.xml.DomUtil
import com.barrybecker4.game.common.GameContext
import org.w3c.dom.{Document, Node}
import scala.jdk.CollectionConverters._
import java.net.URL
import java.util.{ArrayList, HashMap, List, Map}

/**
 * Singleton class for loading and managing the GamePlugins.
 *
 * @author Barry Becker Date: Jan 20, 2007
 */
class PluginManager private() {

  private var plugins: List[GamePlugin] = _
  private var hmNameToPlugin: Map[String, GamePlugin] = _
  private var hmLabelToPlugin: Map[String, GamePlugin] = _
  private var defaultGame: GamePlugin = _

  val url = FileUtil.getURL(PluginManager.PLUGINS_FILE)
  GameContext.log(1, s"about to parse url=$url\n plugin file location=${PluginManager.PLUGINS_FILE}")
  val xmlDocument = DomUtil.parseXML(url)
  initializePlugins(xmlDocument)
  GameContext.log(1, "plugin file parsed")

  private def initializePlugins(document: Document): Unit = {
    val root = document.getDocumentElement
    val children = root.getChildNodes
    plugins = new ArrayList[GamePlugin]()
    hmNameToPlugin = new HashMap[String, GamePlugin]()
    hmLabelToPlugin = new HashMap[String, GamePlugin]()
    var defaultGame: GamePlugin = null

    val num = children.getLength
    for (i <- 0 until num) {
      val n = children.item(i)
      if (!"#comment".equals(n.getNodeName)) {
        val plugin = createPlugin(n)
        plugins.add(plugin)
        hmNameToPlugin.put(plugin.getName, plugin)
        hmLabelToPlugin.put(plugin.getLabel, plugin)
        if (plugin.isDefault) {
          defaultGame = plugin
        }
      }
    }
    if (defaultGame == null) {
      defaultGame = plugins.get(0)
    }
    this.defaultGame = defaultGame
  }

  private def createPlugin(node: Node): GamePlugin = {
    val name = DomUtil.getAttribute(node, "name")
    val msgKey = DomUtil.getAttribute(node, "msgKey")
    val typeStr = DomUtil.getAttribute(node, "type")
    val `type` = PluginType.valueOf(typeStr)
    val msgBundleBase = DomUtil.getAttribute(node, "msgBundleBase")
    val label = GameContext.getLabel(msgKey)
    val panelClass = DomUtil.getAttribute(node, "panelClass")
    val controllerClass = DomUtil.getAttribute(node, "controllerClass")
    val defaultStr = DomUtil.getAttribute(node, "default", "false")
    val isDefault = java.lang.Boolean.parseBoolean(defaultStr)
    new GamePlugin(name, label, `type`, msgBundleBase, panelClass, controllerClass, isDefault)
  }

  def getPlugins: List[GamePlugin] = plugins

  def getPlugins(`type`: PluginType): List[GamePlugin] = {
    val plugins = new ArrayList[GamePlugin]()
    this.plugins.asScala.foreach { plugin =>
      if (plugin.getType == `type`) {
        plugins.add(plugin)
      }
    }
    plugins
  }

  def getPlugin(name: String): GamePlugin = hmNameToPlugin.get(name)

  def getPluginFromLabel(name: String): GamePlugin = hmLabelToPlugin.get(name)

  def getDefaultPlugin: GamePlugin = defaultGame
}

object PluginManager {
  private val PLUGINS_FILE: String = GameContext.GAME_RESOURCE_ROOT + "plugins.xml"
  private var manager: PluginManager = _

  def getInstance: PluginManager = {
    if (manager == null) {
      manager = new PluginManager()
    }
    manager
  }
}
