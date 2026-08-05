/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy

/**
 * Determines the approximate likelihood of a player winning the game.
 *
 * @author Barry Becker
 */
object WinProbabilityCalculator:
  /**
   * Returns a number between 0 and 1 representing the estimated probability of player 1 winning the game.
   * The chance of player2 winning = 1 - chance of p1 winning.
   *
   * @param score best estimate of current move score.
   * @return estimated chance of player one winning the game
   */
  def getChanceOfPlayer1Winning(score: Int): Float =
    computeChanceOfWinning(score.toFloat)

  /**
   * @param lastMoveValue inherited value for last move played.
   * @return Something close to 1 if the chance of winning for player 1 is really good. Close to -1 if bad.
   */
  private def computeChanceOfWinning(lastMoveValue: Float): Float =
    val value = lastMoveValue + SearchStrategy.WINNING_VALUE
    val chance = value / (2.0f * SearchStrategy.WINNING_VALUE)
    Math.max(0, Math.min(chance, 1.0)).toFloat

class WinProbabilityCalculator:

  /**
   * Returns a number between 0 and 1 representing the estimated probability of player 1 winning the game.
   * The chance of player2 winning = 1 - chance of p1 winning.
   *
   * @param moveList list of moves made so far.
   * @return estimated chance of player one winning the game
   */
  final def getChanceOfPlayer1Winning(moveList: MoveList[?]): Double =
    // if true then too early in the game to tell.
    val lastMove = moveList.getLastMove.asInstanceOf[TwoPlayerMove]

    // at the beginning of the game its anybody's guess : 50-50
    if moveList.getNumMoves < 4 then
      return 0.5

    assert(lastMove != null, "last move was null")

    // we can use this formula to estimate the outcome:
    val inherVal = lastMove.getInheritedValue
    if Math.abs(inherVal) > SearchStrategy.WINNING_VALUE then
      GameContext.log(1, s"TwoPlayerController: warning: the score for p1 is greater than WINNING_VALUE(" +
        s"${SearchStrategy.WINNING_VALUE})  inheritedVal=$inherVal")

    WinProbabilityCalculator.computeChanceOfWinning(inherVal)
