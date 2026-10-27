/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.territory

/**
 * Final Japanese-style territory and dead-stone counts after both players have passed.
 * Territory includes empty points (and points under dead stones) owned by one side.
 * Dead stones are counted separately as prisoners for the opponent.
 */
case class TerminalScore(
  blackTerritory: Int,
  whiteTerritory: Int,
  deadBlack: Int,
  deadWhite: Int
)
