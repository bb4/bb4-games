/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.territory.{TerminalScore, TerminalScoreCalculator}

/**
 * Responsible for determining and updating the dead stones on the board at the end of the game.
 * Uses terminal scoring (not mid-game relative health) so living groups are not marked dead
 * merely because a neighbor is stronger.
 */
final class DeadStoneUpdater(board_ : GoBoard) {

  private val deadStones_ : DeadStones = new DeadStones()
  private var blackTerritory_ : Int = 0
  private var whiteTerritory_ : Int = 0
  private var scored_ : Boolean = false

  def getNumDeadStonesOnBoard(forPlayer1: Boolean): Int = {
    deadStones_.getNumberOnBoard(forPlayer1)
  }

  def getTerritory(forPlayer1: Boolean): Int = {
    if (forPlayer1) blackTerritory_ else whiteTerritory_
  }

  def hasScored: Boolean = scored_

  /**
   * Update the final life and death status of all the stones still on the board.
   * This method must only be called once at the end of the game or stones will get prematurely marked as dead.
   */
  def determineDeadStones(): Unit = {
    deadStones_.clear()
    val score: TerminalScore = new TerminalScoreCalculator(board_).calculate()
    blackTerritory_ = score.blackTerritory
    whiteTerritory_ = score.whiteTerritory
    for (_ <- 0 until score.deadBlack) {
      deadStones_.increment(true)
    }
    for (_ <- 0 until score.deadWhite) {
      deadStones_.increment(false)
    }
    scored_ = true
    GameContext.log(0, deadStones_.toString)
    GameContext.log(0, s"terminal territory black=${score.blackTerritory} white=${score.whiteTerritory}")
  }
}
