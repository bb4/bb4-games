/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.{GoBoard, PositionalScore}
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.options.GoWeights
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Used to keep track of evaluating a measure of score based only on values at positions.
 *
 * @author Barry Becker
 */
final class PositionalScoreAnalyzer(val board: GoBoard) {

  /**
   * I used to pass this in as a parameter and have it vary with how far along into the game we are,
   * but I am trying to eliminate scores that might vary for the same position.
   * Identical positions could have different number of moves because of captures and kos.
   */
  private val GAME_STAGE_BOOST = 0.5

  private val positionalScores_ : PositionalScoreArray = PositionalScoreArray.getArray(board.getNumRows)

  /**
   * @param weights game weights
   * @return accumulated totalScore so far.
   */
  def determineScoreForPosition(row: Int, col: Int, weights: ParameterArray): PositionalScore = {
    val position = board.getPosition(row, col).asInstanceOf[GoBoardPosition]
    val positionalScore = positionalScores_.getValue(row, col)
    val score = calcPositionalScore(position, weights, positionalScore)

    val s = score.getPositionScore
    position.setScoreContribution(s)
    score
  }

  /**
   * @return the score contribution from a single point on the board
   */
  private def calcPositionalScore(position: GoBoardPosition, weights: ParameterArray,
                                   positionalScore: Double): PositionalScore = {
    val score =
      if (position.isInEye) {
        createEyePointScore(position)
      }
      else if (position.isOccupied) {
        createNormalizedOccupiedScore(position, weights, positionalScore)
      }
      else {
        PositionalScore.createZeroScore()
      }
    score
  }

  /**
   * Score gets updated with value.
   * A dead enemy stone in the eye counts twice.
   */
  private def createEyePointScore(position: GoBoardPosition): PositionalScore = {
    val scoreForPosition = if (position.getEye.isOwnedByPlayer1) 1.0f else -1.0f
    var deadStoneScore = 0.0f
    var eyeSpaceScore = 0.0f
    if (position.isOccupied) {
      deadStoneScore = scoreForPosition
    }
    else {
      eyeSpaceScore = scoreForPosition
    }

    PositionalScore.createEyePointScore(deadStoneScore, eyeSpaceScore)
  }

  /**
   * Normalize each of the scores by the game weights.
   */
  private def createNormalizedOccupiedScore(
      position: GoBoardPosition, weights: ParameterArray, positionalScore: Double): PositionalScore = {

    val stone = position.getPiece.asInstanceOf[GoStone]

    val side = if (position.getPiece.isOwnedByPlayer1) 1 else -1
    val badShapeWt = weights.get(GoWeights.BAD_SHAPE_WEIGHT_INDEX).getValue
    val positionalWt = weights.get(GoWeights.POSITIONAL_WEIGHT_INDEX).getValue
    val healthWt = weights.get(GoWeights.HEALTH_WEIGHT_INDEX).getValue
    val totalWeight = badShapeWt + positionalWt + healthWt
    val shapeAnalyzer = new StringShapeAnalyzer(board)

    val badShapeScore =
      -(side * shapeAnalyzer.formsBadShape(position) * badShapeWt) / totalWeight

    val posScore = side * positionalWt * GAME_STAGE_BOOST * positionalScore / totalWeight
    val healthScore = healthWt * stone.getHealth / totalWeight

    val score = PositionalScore.createOccupiedScore(badShapeScore, posScore, healthScore)
    if (GameContext.getDebugMode > 1) {
      stone.setPositionalScore(score)
    }
    score
  }
}
