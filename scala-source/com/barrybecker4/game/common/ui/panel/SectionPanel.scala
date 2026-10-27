// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.panel

import com.barrybecker4.ui.util.GUIUtil
import javax.swing._
import javax.swing.border.TitledBorder
import java.awt.Font

/**
 *  A section panel within the GameInfoPanel.
 *
 *  @author Barry Becker
 */
class SectionPanel(title: String) extends JPanel {

  def this() = this("")

  SectionPanel.styleSectionPanel(this, title)
}

object SectionPanel {
  
  private val SECTION_TITLE_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.BOLD, 12)

  /**
   * Create a panel with an etched border for the section.
   * @param panel the panel to style
   * @param title  the title of the panel.
   */
  def styleSectionPanel(panel: JPanel, title: String): JPanel = {
    panel.setOpaque(false)
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title,
      TitledBorder.LEFT, TitledBorder.TOP, SECTION_TITLE_FONT))
    panel
  }
}
