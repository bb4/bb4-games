/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.menu

import javax.swing.{BorderFactory, JMenu, JMenuItem}
import java.awt.event.{ActionEvent, ActionListener}
import scala.jdk.CollectionConverters._

/**
 * The standard file menu for all game programs.
 * Allows such common operations as new, load, save, exit.
 * @author Barry Becker
 */
abstract class AbstractGameMenu(title: String) extends JMenu(title) with ActionListener {

  var currentGameName: String = null

  val listeners: java.util.List[GameMenuListener] = new java.util.ArrayList[GameMenuListener]()

  setBorder(BorderFactory.createEtchedBorder())

  def addListener(listener: GameMenuListener): Unit = {
    listeners.add(listener)
    notifyOfChange(currentGameName)
  }

  def notifyOfChange(gameName: String): Unit = {
    listeners.asScala.foreach(_.gameChanged(gameName))
  }

  /**
   * Create a menu item.
   * @param name name of the menu item. The label.
   * @return the menu item to add.
   */
  def createMenuItem(name: String): JMenuItem = {
    val item = new JMenuItem(name)
    item.addActionListener(this)
    item
  }
}
