/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.{GoBoard, PositionalScore}
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.options.GoWeights
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Used to keep track of evaluating a measure of score based only on values at positions.
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
      if (position.isOccupied) {
        createNormalizedOccupiedScore(position, weights, positionalScore)
      }
      else {
        PositionalScore.createZeroScore()
      }
    score
  }

  /**
   * Line and bad-shape only. Health and eye points are scored in SearchScoreCalculator.
   */
  private def createNormalizedOccupiedScore(
      position: GoBoardPosition, weights: ParameterArray, positionalScore: Double): PositionalScore = {

    val stone = position.getPiece.asInstanceOf[GoStone]

    val side = if (position.getPiece.isOwnedByPlayer1) 1 else -1
    val badShapeWt = weights.get(GoWeights.BAD_SHAPE_WEIGHT_INDEX).getValue
    val positionalWt = weights.get(GoWeights.POSITIONAL_WEIGHT_INDEX).getValue
    val shapeWeight = badShapeWt + positionalWt
    val shapeAnalyzer = new StringShapeAnalyzer(board)

    val badShapeScore =
      if (shapeWeight == 0) 0.0
      else -(side * shapeAnalyzer.formsBadShape(position) * badShapeWt) / shapeWeight

    val posScore =
      if (shapeWeight == 0) 0.0
      else side * positionalWt * GAME_STAGE_BOOST * positionalScore / shapeWeight

    val score = PositionalScore.createOccupiedScore(badShapeScore, posScore, 0.0)
    if (GameContext.getDebugMode > 1) {
      stone.setPositionalScore(score)
    }
    score
  }
}
