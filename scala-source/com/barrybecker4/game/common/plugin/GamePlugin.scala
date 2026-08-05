/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.plugin

import com.barrybecker4.common.app.ClassLoaderSingleton
import com.barrybecker4.game.common.ui.panel.GamePanel

/**
 * Immutable class representing information info about a game plugin.
 * There is a game plugin for each game that we support.
 * see plugins.xml
 *
 * @author Barry Becker
 */
class GamePlugin(
  val name: String,
  val label: String,
  val `type`: PluginType,
  val msgBundleBase: String,
  private val panelClass: String,
  val controllerClass: String,
  val isDefault: Boolean
) {

  def getName: String = name

  def getLabel: String = label

  def getType: PluginType = `type`

  def getMsgBundleBase: String = msgBundleBase

  private def getPanelClass: String = panelClass

  def getPanelInstance: GamePanel = {
    val gameClass = ClassLoaderSingleton.loadClass(getPanelClass)
    var gamePanel: GamePanel = null
    try {
      gamePanel = gameClass.getConstructor().newInstance().asInstanceOf[GamePanel]
    } catch {
      case e: Exception => e.printStackTrace()
    }
    gamePanel
  }

  def getControllerClass: String = controllerClass

  override def toString: String =
    s"$name($getLabel panelClass=$panelClass controllerClass=$controllerClass)\n"
}
