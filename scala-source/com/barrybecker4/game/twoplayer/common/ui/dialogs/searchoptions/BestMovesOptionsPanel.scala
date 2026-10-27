/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.dialogs.searchoptions

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions
import com.barrybecker4.ui.components.NumberInput

import javax.swing.*

/**
 * Panel that shows the options for search strategies that use monte carlo methods (like UCT derivatives).
 *
 * @author Barry Becker
 */
class BestMovesOptionsPanel(private val bestMovesOptions: BestMovesSearchOptions) extends JPanel:

  private var percentLessThanBestThreshField: NumberInput = scala.compiletime.uninitialized
  private var bestPercentageField: NumberInput = scala.compiletime.uninitialized
  private var minBestMovesField: NumberInput = scala.compiletime.uninitialized

  initialize()

  /**
   * update the options based on what the user selected.
   */
  def updateBestMovesOptions(): Unit =
    bestMovesOptions.setPercentLessThanBestThresh(percentLessThanBestThreshField.getIntValue)
    bestMovesOptions.setPercentageBestMoves(bestPercentageField.getIntValue)
    bestMovesOptions.setMinBestMoves(minBestMovesField.getIntValue)

  /**
   * create the panel with ui element for setting the options
   */
  private def initialize(): Unit =
    this.setLayout(BoxLayout(this, BoxLayout.Y_AXIS))

    percentLessThanBestThreshField =
      NumberInput(
        GameContext.getLabel("PERCENT_LESS_THAN_BEST_THRESH"),
        bestMovesOptions.getPercentLessThanBestThresh,
        GameContext.getLabel("PERCENT_LESS_THAN_BEST_THRESH_TIP"),
        0,
        100,
        true
      )

    bestPercentageField =
      NumberInput(
        GameContext.getLabel("PERCENTAGE_AT_PLY"),
        bestMovesOptions.getPercentageBestMoves,
        GameContext.getLabel("PERCENTAGE_AT_PLY_TIP"),
        0,
        100,
        true
      )

    minBestMovesField =
      NumberInput(
        GameContext.getLabel("MIN_BEST_MOVES"),
        bestMovesOptions.getMinBestMoves,
        GameContext.getLabel("MIN_BEST_MOVES_TIP"),
        1,
        100,
        true
      )

    add(percentLessThanBestThreshField)
    add(bestPercentageField)
    add(minBestMovesField)
