/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online.ui

import com.barrybecker4.game.common.online.{GameCommand, OnlineChangeListener}
import com.barrybecker4.game.common.online.server.IServerConnection
import com.barrybecker4.ui.components.ScrollingTextArea

import javax.swing._
import java.awt._
import java.awt.event.{KeyEvent, KeyListener}

/**
 * Allows IM chatting with other online players.
 *
 * @author Barry Becker
 */
class ChatPanel(connection: IServerConnection) extends JPanel with OnlineChangeListener with KeyListener {

  private val connection_ = connection
  private val textArea_ = new ScrollingTextArea()
  private val messageField_ = new JTextField()

  setLayout(new BorderLayout())
  connection_.addOnlineChangeListener(this)

  textArea_.setBackground(getBackground)
  textArea_.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)

  messageField_.addKeyListener(this)
  val messagePanel = new JPanel(new BorderLayout())
  messagePanel.add(new JLabel("Chat:"), BorderLayout.WEST)
  messagePanel.add(messageField_, BorderLayout.CENTER)

  add(textArea_, BorderLayout.CENTER)
  add(messagePanel, BorderLayout.SOUTH)

  /**
   * Post messages from other players.
   */
  override def handleServerUpdate(cmd: GameCommand): Boolean = {
    if (cmd.getName == GameCommand.Name.CHAT_MESSAGE) {
      textArea_.append(cmd.getArgument.toString)
      textArea_.append("\n")
      true
    } else {
      false
    }
  }

  /**
   * Send the message when you press enter.
   */
  override def keyTyped(e: KeyEvent): Unit = {
    if (e.getKeyChar == '\n') {
      var txt = messageField_.getText
      txt = txt.trim
      if (txt.length > 0) {
        messageField_.setText("")
        connection_.sendCommand(GameCommand(GameCommand.Name.CHAT_MESSAGE, txt))
      }
    }
  }

  override def keyPressed(e: KeyEvent): Unit = {}
  override def keyReleased(e: KeyEvent): Unit = {}
}
