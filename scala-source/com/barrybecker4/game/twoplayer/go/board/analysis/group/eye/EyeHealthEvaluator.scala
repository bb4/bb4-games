/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.GoProfiler
import com.barrybecker4.game.twoplayer.go.board.analysis.group.LifeAnalyzer

/**
 * Evaluate the health of a group based on the status of its eye(s).
 * @author Barry Becker
 */
class EyeHealthEvaluator(val lifeAnalyzer_ : LifeAnalyzer) {

  private val BEST_TWO_EYED_HEALTH = 1.0f
  private val BEST_ALMOST_TWO_EYED_HEALTH = 0.94f
  private val BEST_ONE_EYED_HEALTH = 0.89f

  /**
   * @return the health of the group based on the number of eyes and the number of liberties.
   */
  def determineHealth(side: Float, numEyes: Float, numLiberties: Int, numStones: Int): Float = {
    val health =
      if (numEyes >= 2.0) {
        calcTwoEyedHealth(side)
      }
      else if (numEyes >= 1.5) {
        calcAlmostTwoEyedHealth(side, numLiberties)
      }
      else if (numEyes >= 1.0) {
        calcOneEyedHealth(side, numLiberties)
      }
      else {
        calcNoEyeHealth(side, numLiberties, numStones)
      }
    health
  }

  /**
   * @return the health of a group that has 2 eyes.
   */
  private def calcTwoEyedHealth(side: Float): Float = {
    GoProfiler.getInstance.startBensonsCheck()
    val health =
      if (lifeAnalyzer_.isUnconditionallyAlive) {
        BEST_TWO_EYED_HEALTH * side
      }
      else {
        BEST_ALMOST_TWO_EYED_HEALTH * side
      }
    GoProfiler.getInstance.stopBensonsCheck()
    health
  }

  /**
   * @return the health of a group that has only one eye.
   */
  private def calcAlmostTwoEyedHealth(side: Float, numLiberties: Int): Float = {
    var health = 0.0f
    if (numLiberties > 6) {
      health = side * Math.min(BEST_ALMOST_TWO_EYED_HEALTH, (1.15f - 20.0f / (numLiberties + 23.0f)))
    }
    else {
      health = numLiberties match {
        case 0 | 1 => 0.0f
        case 2 =>
          GameContext.log(1, "We have almost 2 eyes but only 2 Liberties. How can that be? " + this.toString)
          side * 0.02f
        case 3 => side * 0.05f
        case 4 => side * 0.1f
        case 5 => side * 0.19f
        case 6 => side * 0.29f
        case _ => assert(false); 0.0f
      }
    }
    health
  }

  /**
   * @return the health of a group that has only one eye.
   */
  private def calcOneEyedHealth(side: Float, numLiberties: Int): Float = {
    var health = 0.0f
    if (numLiberties > 6) {
      health = side * Math.min(BEST_ONE_EYED_HEALTH, (1.03f - 20.0f / (numLiberties + 20.0f)))
    }
    else {
      health = numLiberties match {
        case 0 => assert(false, "can't have 1 eye and no liberties!"); 0.0f
        case 1 => -side * 0.8f
        case 2 => -side * 0.3f
        case 3 => -side * 0.1f
        case 4 => side * 0.01f
        case 5 => side * 0.05f
        case 6 => side * 0.19f
        case _ => assert(false); 0.0f
      }
    }
    health
  }

  /**
   * @return the health of a group that has no eyes.
   */
  private def calcNoEyeHealth(side: Float, numLiberties: Int, numStones: Int): Float = {
    if (numLiberties > 5) {
      side * Math.min(0.8f, (1.2f - 46.0f / (numLiberties + 40.0f)))
    }
    else if (numStones == 1) {
      calcSingleStoneHealth(side, numLiberties)
    } else {
      calcMultiStoneHealth(side, numLiberties)
    }
  }

  /**
   * @return health of a single stone based on number of liberties it has.
   */
  private def calcSingleStoneHealth(side: Float, numLiberties: Int): Float = {
    numLiberties match {
      case 0 => assert(false, s"can't have no liberties and still be on the board! $this"); -side
      case 1 => -side * 0.7f
      case 2 => side * 0.02f
      case 3 => side * 0.1f
      case 4 => side * 0.1f
      case _ => assert(false, s"there were too many liberties for a single stone: $numLiberties"); 0.0f
    }
  }

  /**
   * @return health for multi-stone group with less than 5 liberties.
   */
  private def calcMultiStoneHealth(side: Float, numLiberties: Int): Float = {
    numLiberties match {
      case 0 => -side
      case 1 => -side * 0.6f
      case 2 => -side * 0.3f
      case 3 => side * 0.02f
      case 4 => side * 0.05f
      case 5 => side * 0.1f
      case _ => assert(false, "We should have already covered the case of >5 liberties"); 0.0f
    }
  }
}
