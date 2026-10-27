// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model

import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer

import java.awt.Color

/**
 * Outcome of the two player game.
 * @author Barry Becker
 */
enum Outcome extends java.lang.Enum[Outcome] {
  case PLAYER1_WON, PLAYER2_WON, TIE

  def getColor: Color = this match {
    case PLAYER1_WON => TwoPlayerPieceRenderer.DEFAULT_PLAYER1_COLOR
    case PLAYER2_WON => TwoPlayerPieceRenderer.DEFAULT_PLAYER2_COLOR
    case TIE => Color.GRAY.brighter()
  }
}
