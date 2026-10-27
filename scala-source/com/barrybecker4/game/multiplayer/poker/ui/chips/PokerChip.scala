/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.chips

import java.awt.Color

enum PokerChip(private val label_ : String, private val value_ : Int, private val color_ : Color) 
    extends java.lang.Enum[PokerChip] {
  case ONE extends PokerChip("One Dollar Chip", 1, Color(255, 200, 0))
  case FIVE extends PokerChip("Five Dollar Chip", 5, Color(200, 0, 0))
  case TEN extends PokerChip("Ten Dollar Chip", 10, Color(0, 80, 255))
  case TWENTY_FIVE extends PokerChip("25 Dollar Chip", 25, Color(0, 200, 10))
  case FIVE_HUNDRED extends PokerChip("500 Dollar Chip", 500, Color(220, 0, 200))

  def getLabel: String = label_

  def getValue: Int = value_

  def getColor: Color = color_
}
