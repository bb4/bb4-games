package com.barrybecker4.game.twoplayer.hex

import com.barrybecker4.game.common.GameWeights

/**
 * These weights determine how the computer values each pattern.
 * These weights determine how the computer values features of the board relative to each other.
 * If only one computer is playing, then only one of the weights arrays is used.
 * Use these weights if no others are provided.
 */
class HexWeights extends GameWeights(
  HexWeights.DEFAULT_WEIGHTS, 
  HexWeights.MIN_WEIGHTS, 
  HexWeights.MAX_WEIGHTS, 
  HexWeights.WEIGHT_SHORT_DESCRIPTIONS, 
  HexWeights.WEIGHT_DESCRIPTIONS
)

object HexWeights:
  val CENTRICITY_INDEX = 0
  val PATH_COST_INDEX = 1

  /** If greater than this threshold, then opponent is in jeopardy. */
  val JEOPARDY_WEIGHT = 5

  /** These defaults may be overridden in by the user in the UI. */
  private val DEFAULT_WEIGHTS = Array[Double](
    0.1, JEOPARDY_WEIGHT, 2 * GameWeights.ASSUMED_WINNING_VALUE
  )

  /** Don't allow the weights to go below these minimum values. Upper limit. */
  private val MIN_WEIGHTS = Array[Double](
    0.0, 1.0, GameWeights.ASSUMED_WINNING_VALUE
  )

  /** Don't allow the weights to exceed these maximum values. Upper limit. */
  private val MAX_WEIGHTS = Array[Double](
    50.0, 500.0, 3 * GameWeights.ASSUMED_WINNING_VALUE
  )

  private val WEIGHT_SHORT_DESCRIPTIONS = Array(
    "centricity", "linkProximity", "fullPath"
  )

  private val WEIGHT_DESCRIPTIONS = Array(
    "Control of the center",
    "Number of blank spaces need to make full path",
    "Has a path that spsns the board"
  )
