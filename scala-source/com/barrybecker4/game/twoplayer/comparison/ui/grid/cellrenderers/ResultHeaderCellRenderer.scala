// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.grid.cellrenderers

import com.barrybecker4.game.twoplayer.comparison.model.Outcome

import javax.swing._
import java.awt.{Color, Component}

/**
 * Show the column headers with player two's color.
 * validate/revalidate overridden in cell renderer for performance reasons..
 *
 * @author Barry Becker
 */
class ResultHeaderCellRenderer extends PlayerHeadCellRenderer {

  override def getTableCellRendererComponent(
    table: JTable, value: AnyRef,
    isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
  ): Component = {
    if (table != null) {
      val header = table.getTableHeader
      if (header != null) {
        setForeground(Outcome.PLAYER2_WON.getColor.darker)
        setBackground(Color.WHITE)
        setFont(header.getFont)
      }
    }

    value match {
      case t: ResultHeaderCellRenderer.TextAndIcon =>
        setIcon(t.icon)
        setText(t.text)
      case _ =>
        setText(if (value == null) "" else value.toString)
        setIcon(null)
    }
    setToolTipText(getText)
    setBorder(UIManager.getBorder("TableHeader.cellBorder"))
    setHorizontalAlignment(SwingConstants.CENTER)
    setFont(PlayerHeadCellRenderer.FONT)
    this
  }
}

object ResultHeaderCellRenderer {
  class TextAndIcon(val text: String, val icon: Icon)
}
