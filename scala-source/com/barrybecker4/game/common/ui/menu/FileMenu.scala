/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.menu

import com.barrybecker4.game.common.GameContext

import javax.swing.{BorderFactory, JMenu, JMenuItem}
import java.awt.event.{ActionEvent, ActionListener}

/**
 * The standard file menu for all game programs.
 * Allows such common operations as new, load, save, exit.
 * @author Barry Becker
 */
class FileMenu(listener: FileMenuListener) extends JMenu(GameContext.getLabel("FILE")) with ActionListener {

  private val openItem_ = createMenuItem(GameContext.getLabel("OPEN"))
  private val saveItem_ = createMenuItem(GameContext.getLabel("SAVE"))
  private val saveImageItem_ = createMenuItem(GameContext.getLabel("SAVE_IMAGE"))
  private val exitItem_ = createMenuItem(GameContext.getLabel("EXIT"))

  setBorder(BorderFactory.createEtchedBorder())

  add(openItem_)
  add(saveItem_)
  add(saveImageItem_)
  add(exitItem_)

  private def createMenuItem(name: String): JMenuItem = {
    val item = new JMenuItem(name)
    item.addActionListener(this)
    item
  }

  /**
   * called when the user has selected a different game to play from the game menu
   */
  override def actionPerformed(event: ActionEvent): Unit = {
    val item = event.getSource.asInstanceOf[JMenuItem]
    if (item eq openItem_) {
      listener.openFile()
    } else if (item eq saveItem_) {
      listener.saveFile()
    } else if (item eq saveImageItem_) {
      listener.saveImage()
    } else if (item eq exitItem_) {
      System.exit(0)
    } else {
      assert(false, "unexpected menuItem = " + item.getName)
    }
  }
}
