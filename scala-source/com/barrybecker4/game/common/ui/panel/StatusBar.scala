// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.panel

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.ui.components.TexturedPanel
import com.barrybecker4.ui.util.GUIUtil
import javax.swing._
import java.awt.{BorderLayout, Dimension, Font}

/**
 * Shows game status.
 *
 * @author Barry Becker
 */
class StatusBar(texture: ImageIcon) extends TexturedPanel(texture) {

  private val statusBarLabel = new JLabel()
  statusBarLabel.setFont(StatusBar.STATUS_FONT)
  statusBarLabel.setOpaque(false)
  statusBarLabel.setText(GameContext.getLabel("STATUS_MSG"))

  setLayout(new BorderLayout())
  setMaximumSize(new Dimension(1000, 16))
  add(statusBarLabel, BorderLayout.WEST)
}

object StatusBar {
  private val STATUS_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 10)
}
