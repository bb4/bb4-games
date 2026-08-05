/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.dialogs.searchoptions

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.search.options.MonteCarloSearchOptions
import com.barrybecker4.ui.components.NumberInput

import javax.swing.*

/**
 * Panel that shows the options for search strategies that use monte carlo methods (like UCT derivatives).
 *
 * @author Barry Becker
 */
class MonteCarloOptionsPanel(private val monteCarloOptions: MonteCarloSearchOptions) extends JPanel:

  private var maxSimulationsField: NumberInput = scala.compiletime.uninitialized
  private var exploreExploitRatioField: NumberInput = scala.compiletime.uninitialized
  private var randomLookHeadField: NumberInput = scala.compiletime.uninitialized

  /** It would be unreasonable to run more than this many simulations. */
  private val ABS_MAX_NUM_SIMULATIONS = 100000000

  /** It would be unreasonable to have a exploreExploit ration more than this. */
  private val ABS_MAX_EE_RATIO = 100.0

  initialize()

  /**
   * update the options based on the user preferences.
   */
  def updateMonteCarloOptionsOptions(): Unit =
    monteCarloOptions.setMaxSimulations(maxSimulationsField.getIntValue)
    monteCarloOptions.setExploreExploitRatio(exploreExploitRatioField.getValue)
    monteCarloOptions.setRandomLookAhead(randomLookHeadField.getIntValue)

  /**
   * create the panel with ui element for setting the options
   */
  private def initialize(): Unit =
    setLayout(BoxLayout(this, BoxLayout.Y_AXIS))

    maxSimulationsField =
      NumberInput(
        GameContext.getLabel("MAX_NUM_SIMULATIONS"),
        monteCarloOptions.getMaxSimulations,
        GameContext.getLabel("MAX_NUM_SIMULATIONS_TIP"),
        1,
        ABS_MAX_NUM_SIMULATIONS,
        true
      )

    exploreExploitRatioField =
      NumberInput(
        GameContext.getLabel("EXPLORE_EXPLOIT_RATIO"),
        monteCarloOptions.getExploreExploitRatio,
        GameContext.getLabel("EXPLORE_EXPLOIT_RATIO_TIP"),
        0,
        ABS_MAX_EE_RATIO,
        false
      )

    randomLookHeadField =
      NumberInput(
        GameContext.getLabel("RANDOM_LOOK_AHEAD"),
        monteCarloOptions.getRandomLookAhead,
        GameContext.getLabel("RANDOM_LOOK_AHEAD_TIP"),
        1,
        1000,
        true
      )

    add(maxSimulationsField)
    add(exploreExploitRatioField)
    add(randomLookHeadField)
