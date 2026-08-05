// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.set.ui.render

import com.barrybecker4.game.multiplayer.set.Card
import com.barrybecker4.game.multiplayer.set.ui.render.SymbolColors.ColorType

import java.awt.Color

final class SymbolColors:

  def getColorForValue(value: Card.AttributeValue, style: ColorType): Color =
    SymbolColors.SYMBOL_COLORS(value.ordinal)(style.ordinal)

  def getCardColor(card: Card): Color =
    getColorForValue(card.color(), ColorType.SOLID)

  def getBorderCardColor(card: Card): Color =
    getColorForValue(card.color(), if card.isHighlighted then ColorType.HIGHLIGHT else ColorType.BORDER)

object SymbolColors:
  enum ColorType:
    case SOLID, BORDER, HATCHED, HIGHLIGHT

  private val SYMBOL_COLORS: Array[Array[Color]] = Array(
    Array( // FIRST
      new Color(210, 90, 80),
      new Color(170, 5, 2),
      new Color(230, 42, 22),
      new Color(255, 22, 12)
    ),
    Array( // SECOND
      new Color(100, 210, 100),
      new Color(0, 140, 0),
      new Color(0, 200, 10),
      new Color(10, 252, 2)
    ),
    Array( // THIRD
      new Color(85, 95, 220),
      new Color(0, 0, 190),
      new Color(25, 25, 245),
      new Color(70, 60, 255)
    )
  )
