// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.common.online.ui

import javax.swing._
import java.awt._
import java.awt.event.{KeyListener, MouseEvent, MouseListener}

/**
 * For entering and managing the multi-players name.
 * It must be unique among the players at the table.
 *
 * @author Barry Becker
 */
class NamePanel extends JPanel(new BorderLayout) with MouseListener {

  private val DEFAULT_NAME = "<name>"

  private val localPlayerName_ = new JTextField(DEFAULT_NAME)

  localPlayerName_.addMouseListener(this)

  private val nameLabel = new JLabel("Your Name: ")
  add(nameLabel, BorderLayout.WEST)
  add(localPlayerName_, BorderLayout.CENTER)

  private val fill = new JPanel()
  fill.setPreferredSize(new Dimension(180, 20))
  add(fill, BorderLayout.EAST)

  override def addKeyListener(listener: KeyListener): Unit = {
    localPlayerName_.addKeyListener(listener)
  }

  def nameChecksOut: Boolean = {
    val checksOut = !getCurrentName.equals(DEFAULT_NAME)
    if (!checksOut) {
      JOptionPane.showMessageDialog(
        this, "You must enter your name at the top first.", "Warning",
        JOptionPane.INFORMATION_MESSAGE)
    }
    checksOut
  }

  def getCurrentName: String =
    localPlayerName_.getText

  override def mouseClicked(e: MouseEvent): Unit = {}
  override def mousePressed(e: MouseEvent): Unit = giveFocus()
  override def mouseReleased(e: MouseEvent): Unit = {}
  override def mouseEntered(e: MouseEvent): Unit = {}
  override def mouseExited(e: MouseEvent): Unit = {}

  /** Call when the user first clicks in the field */
  private def giveFocus(): Unit = {
    if (localPlayerName_.getText.equals(DEFAULT_NAME))
      localPlayerName_.setText("")
  }
}
