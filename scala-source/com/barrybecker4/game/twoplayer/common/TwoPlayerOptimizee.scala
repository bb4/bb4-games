/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common

import com.barrybecker4.game.common.GameWeights
import com.barrybecker4.optimization.optimizee.Optimizee
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Class that can optimize the gameWeights by applying repetitive play.
 *
 * @author Barry Becker
 */
class TwoPlayerOptimizee[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
  private val controller_ : TwoPlayerController[M, B]
) extends Optimizee:

  override def getName: String = "TwoPlayer Game"

  /**
   * If true is returned, then compareFitness will be used and evaluateFitness will not
   * otherwise the reverse will be true.
   * @return return true if we evaluate the fitness by comparison
   */
  override def evaluateByComparison: Boolean = true

  /**
   * Attributes a measure of fitness to the specified set of parameters.
   * There's no good way for a game playing program to do this because it
   * can only evaluate itself relative to another player.
   * see compareFitness below.
   * @param params the set of parameters to misc
   * @return the fitness measure. The higher, the better
   */
  override def evaluateFitness(params: ParameterArray): Double = 0.0

  override def getOptimalFitness: Double = 0

  /**
   * Compares to sets of game parameters.
   * It does this by playing the computer against itself. One computer player has the params1
   * weights and the other computer player uses the params2 weights.
   * If the player using params1 wins then a positive value proportional to the strength of the win is returned.
   *
   * To remove the advantage we get from playing first, 2 runs are done
   *  The first one where params1 plays first, and the second where params2 plays first.
   *  This should remove the bias.
   *
   * @param params1 set of weights for one of the sides
   * @param params2 set of weights for the other side
   * @return the amount that params1 are better than params2. May be negative if params2 are better.
   */
  override def compareFitness(params1: ParameterArray, params2: ParameterArray): Double =
    val weights = controller_.getComputerWeights()
    weights.setPlayer1Weights(params1)
    weights.setPlayer2Weights(params2)
    val run1 = runComputerVsComputer()

    weights.setPlayer1Weights(params2)
    weights.setPlayer2Weights(params1)
    val run2 = runComputerVsComputer()

    val delta = run1 - run2
    delta

  /**
   * Run a computer player against itself
   * @return if positive then computer1 won, else computer2 won.
   *   the magnitude of this returned value indicates how much it won by.
   */
  private def runComputerVsComputer(): Double =
    var done = false
    controller_.reset()
    controller_.computerMovesFirst()

    if controller_.getViewer != null then
      controller_.getViewer.doComputerVsComputerGame()
    else
      // run in batch mode where the viewer is not available.
      while !done do
        done = controller_.getSearchable().done(controller_.findComputerMove(false), true)
        // if done the final move was played
        if !done then
          done = controller_.getSearchable().done(controller_.findComputerMove(true), true)

    if controller_.getPlayers.getPlayer1.hasWon then
      controller_.getStrengthOfWin
    else
      -controller_.getStrengthOfWin
