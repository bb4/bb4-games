// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.ui.render

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.multiplayer.poker.ui.chips.{PokerChip, PokerChips}
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerBoardRenderer
import javax.swing.JComponent
import java.awt.{Color, Font, Graphics2D}
import java.text.NumberFormat

class ChipRenderer {
  import ChipRenderer.*

  def render(g2: Graphics2D, location: Location, amount: Int, cellSize: Int): Unit = {
    val chips = PokerChips(amount)

    GameContext.log(3, s"chips stacks = $chips")

    var x = 0
    var width = 0
    var height = 0
    var y = cellSize * location.row

    val it = chips.keySet().iterator()
    while (it.hasNext) {
      val chipType = it.next()
      val numChips = chips.get(chipType)
      height = (cellSize * numChips * CHIP_HEIGHT).toInt
      width = (CHIP_PILE_WIDTH * cellSize).toInt
      g2.setColor(chipType.getColor)
      x = ((chipType.ordinal() * CHIP_PILE_WIDTH + location.col + 1) * cellSize).toInt
      y = location.row * cellSize - height

      g2.fillRect(x, y, width, height)
      g2.setColor(BLACK_COLOR)
      g2.drawRect(x, y, width, height)
      for (j <- 1 until numChips) {
        y = (cellSize * (location.row - j * CHIP_HEIGHT)).toInt
        g2.drawLine(x, y, (x + cellSize * CHIP_PILE_WIDTH).toInt, y)
      }
    }

    g2.setColor(BLACK_COLOR)
    val currencyFormat = NumberFormat.getCurrencyInstance(JComponent.getDefaultLocale)

    val cashAmount = currencyFormat.format(amount)
    x = (location.col + 1) * cellSize
    val f = PokerPlayerRenderer.POKER_FONT.deriveFont(cellSize.toFloat / 24 * PokerPlayerRenderer.FONT_SIZE)

    g2.setFont(f)
    g2.drawString(cashAmount, x, (y + height + cellSize / 1.2).toInt)
  }
}

object ChipRenderer {
  private val BLACK_COLOR = Color.black
  private val CHIP_PILE_WIDTH = 0.9
  private val CHIP_HEIGHT = 0.15
}
