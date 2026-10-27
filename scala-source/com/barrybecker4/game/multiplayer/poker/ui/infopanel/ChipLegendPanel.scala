// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.ui.infopanel

import com.barrybecker4.game.multiplayer.poker.ui.chips.PokerChip
import com.barrybecker4.ui.legend.DiscreteColorLegend
import javax.swing.JPanel
import java.awt.{Color, Dimension}

class ChipLegendPanel extends JPanel {
  initUI()

  private def initUI(): Unit = {
    val chipTypes = PokerChip.values
    val n = chipTypes.length
    val colors = Array.ofDim[Color](n)
    val values = Array.ofDim[String](n)
    for (i <- n to 1 by -1) {
      colors(n - i) = chipTypes(i - 1).getColor
      values(n - i) = chipTypes(i - 1).getLabel
    }
    val legend = DiscreteColorLegend(null, colors, values)
    legend.setPreferredSize(Dimension(500, 100))
    add(legend)
  }
}
