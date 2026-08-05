// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.panel

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.ui.util.GUIUtil
import javax.swing._
import java.awt.Font

/**
 * An info label has a label a colon and a value.
 * @author Barry Becker
 */
class InfoLabel(s: String) extends JLabel(s + InfoLabel.COLON) {
  
  def this() = this(null)
  
  setFont(InfoLabel.LABEL_FONT)
  setOpaque(false)
}

object InfoLabel {
  private val COLON = ' ' + GameContext.getLabel("COLON") + ' '
  private val LABEL_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 12)
}
