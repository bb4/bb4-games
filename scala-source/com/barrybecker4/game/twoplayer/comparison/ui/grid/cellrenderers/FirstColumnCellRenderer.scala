// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.grid.cellrenderers

import com.barrybecker4.game.twoplayer.comparison.model.Outcome

import javax.swing._
import java.awt.{Color, Component}

/**
 * Show the column headers with player twos color.
 * validate/revalidate overridden in cell renderer for performance reasons..
 *
 * @author Barry Becker
 */
class FirstColumnCellRenderer extends PlayerHeadCellRenderer {

  override def getTableCellRendererComponent(
    table: JTable, value: AnyRef,
    isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
  ): Component = {
    if (table != null) {
      val header = table.getTableHeader
      if (header != null) {
        setForeground(Outcome.PLAYER1_WON.getColor.darker)
        setBackground(Color.WHITE)
        setFont(header.getFont)
      }
    }

    setText(if (value == null) "" else value.toString)

    setBorder(UIManager.getBorder("TableHeader.cellBorder"))
    setHorizontalAlignment(SwingConstants.CENTER)
    setFont(PlayerHeadCellRenderer.FONT)
    this
  }
}
