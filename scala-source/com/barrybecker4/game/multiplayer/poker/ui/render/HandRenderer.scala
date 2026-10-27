// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.ui.render

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.card.{Card, Suit}
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.multiplayer.poker.hand.Hand
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerBoardRenderer
import com.barrybecker4.ui.util.GUIUtil
import javax.swing.ImageIcon
import java.awt.{Color, Font, Graphics2D}
import scala.jdk.CollectionConverters.*

class HandRenderer {
  import HandRenderer.*

  def render(g2: Graphics2D, location: Location, hand: Hand, cellSize: Int): Unit = {
    assert(hand != null, "Did you forget to deal cards to one of the players?")
    var x = ((location.col - 1) * cellSize)
    var y = ((location.row + 1.6) * cellSize).toInt
    val cardArc = (cellSize * CARD_ARC).toInt

    val it = hand.getCards.iterator()
    while (it.hasNext) {
      val c = it.next()
      if (hand.isFaceUp) {
        g2.setColor(CARD_BG_COLOR)
      } else {
        g2.setColor(CARD_BACK_COLOR)
      }
      val w = (cellSize * (CARD_WIDTH + CARD_ARC)).toInt
      val h = (cellSize * CARD_HEIGHT).toInt
      g2.fillRoundRect(x, y, w, h, cardArc, cardArc)
      g2.setColor(BLACK_COLOR)
      g2.drawRoundRect(x, y, w, h, cardArc, cardArc)

      if (hand.isFaceUp) {
        renderFaceUpCard(g2, x, y, cellSize, cardArc, c)
      }

      x += (cellSize * CARD_WIDTH).toInt
    }
  }

  private def renderFaceUpCard(g2: Graphics2D, x: Int, y: Int, cellSize: Int, cardArc: Int, c: Card): Unit = {
    val font = PokerPlayerRenderer.POKER_FONT.deriveFont(cellSize.toFloat / 24 * POKER_CARD_FONT_SIZE)

    val imgIcon = suitImages_(c.suit.ordinal())
    val rat = imgIcon.getIconHeight.toFloat / imgIcon.getIconWidth
    val imageWidth = (cellSize * 0.82 * CARD_WIDTH).toInt

    g2.drawImage(imgIcon.getImage, (x + 0.7 * cardArc).toInt, (y + cellSize * (CARD_HEIGHT / 2.16)).toInt,
                 imageWidth, (rat * imageWidth).toInt, null)

    g2.setFont(font)
    g2.setColor(if (c.suit == Suit.HEARTS || c.suit == Suit.DIAMONDS) RED_COLOR else BLACK_COLOR)

    val symbol = c.rank.getSymbol
    val r = font.getStringBounds(symbol, g2.getFontRenderContext)
    val symbWidth = r.getWidth
    g2.drawString(symbol, x + ((cellSize * CARD_WIDTH - symbWidth) / 1.7).toInt, (y + 0.7 * r.getHeight + cardArc).toInt)
  }
}

object HandRenderer {
  private val suitImages_ = Array.ofDim[ImageIcon](Suit.values.length)

  private val IMAGE_DIR = GameContext.GAME_RESOURCE_ROOT + "multiplayer/poker/ui/images/"

  suitImages_(Suit.CLUBS.ordinal()) = GUIUtil.getIcon(IMAGE_DIR + "club_small.gif")
  suitImages_(Suit.SPADES.ordinal()) = GUIUtil.getIcon(IMAGE_DIR + "spade_small.gif")
  suitImages_(Suit.HEARTS.ordinal()) = GUIUtil.getIcon(IMAGE_DIR + "heart_small.gif")
  suitImages_(Suit.DIAMONDS.ordinal()) = GUIUtil.getIcon(IMAGE_DIR + "diamond_small.gif")

  private val CARD_WIDTH = 1.7f
  private val CARD_HEIGHT = 3.3f
  private val CARD_ARC = 0.28f
  private val POKER_CARD_FONT_SIZE = 10
  private val CARD_BG_COLOR = Color.white
  private val CARD_BACK_COLOR = Color(140, 220, 255)
  private val RED_COLOR = Color(200, 0, 0)
  private val BLACK_COLOR = Color.black
}
