// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.grid.cellrenderers

import com.barrybecker4.ui.util.GUIUtil

import javax.swing._
import javax.swing.table.DefaultTableCellRenderer
import java.awt.{Color, Component, Font}

/**
 * Show the column headers with player twos color.
 * validate/revalidate overridden in cell renderer for performance reasons..
 *
 * @author Barry Becker
 */
class PlayerHeadCellRenderer extends DefaultTableCellRenderer {

  override def getTableCellRendererComponent(
    table: JTable, value: AnyRef,
    isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
  ): Component = {
    if (table != null) {
      val header = table.getTableHeader
      if (header != null) {
        setForeground(com.barrybecker4.game.twoplayer.comparison.model.Outcome.PLAYER1_WON.getColor.darker)
        setBackground(Color.WHITE)
        setFont(header.getFont)
      }
    }

    setText(if (value == null) "" else value.toString)
    setToolTipText(getText)
    setBorder(UIManager.getBorder("TableHeader.cellBorder"))
    setHorizontalAlignment(SwingConstants.CENTER)
    setFont(PlayerHeadCellRenderer.FONT)
    this
  }
}

object PlayerHeadCellRenderer {
  val FONT: Font = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.BOLD, 16)
}
