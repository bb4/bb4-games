/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online.ui

import com.barrybecker4.common.app.CommandLineOptions
import com.barrybecker4.game.common.online.server.OnlineGameServer
import com.barrybecker4.game.common.plugin.PluginManager
import com.barrybecker4.ui.components.ScrollingTextArea

import javax.swing._
import java.awt._
import java.awt.event.{WindowAdapter, WindowEvent}

/**
 * UI Frame to show what the GameServer is doing.
 * There is nothing game specific about the server it just accepts commands and delegates to the controller.
 * Invoke with
 *   java OnlineGameServer -game &lt;game&gt;
 * for example:
 *   java OnlineGameServer -game poker
 *
 * @author Barry Becker
 */
class OnlineGameServerFrame private (gameName: String) extends JFrame {

  private val textArea = new ScrollingTextArea(20, 44)
  private val server = new OnlineGameServer(gameName, textArea)

  initUI(gameName)
  setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
  addWindowListener(new WindowAdapter {
    override def windowClosing(e: WindowEvent): Unit = {
      server.shutDown()
    }
  })

  /**
   * initialize UI for server.
   */
  private def initUI(gameName: String): Unit = {
    val panel = new JPanel()
    val label = new JLabel("Commands received over the socket:")

    panel.setLayout(new BorderLayout())
    panel.setBackground(Color.white)
    panel.add("North", label)
    val scrollPane = new JScrollPane(textArea)
    panel.add("Center", scrollPane)
    val gameLabel = PluginManager.getInstance.getPlugin(gameName).getLabel
    setTitle(gameLabel + " Server")

    getContentPane.add(panel)

    pack()
    setVisible(true)
  }
}

object OnlineGameServerFrame {

  /**
   * create and show the server.
   */
  def main(args: Array[String]): Unit = {
    val options = new CommandLineOptions(args)

    if (OnlineGameServer.verifyCmdLineOptions(options)) {
      val gameName = options.getValueForOption(OnlineGameServer.GAME_OPTION)
      val frame = new OnlineGameServerFrame(gameName)
      frame.setVisible(true)
    }
  }
}
